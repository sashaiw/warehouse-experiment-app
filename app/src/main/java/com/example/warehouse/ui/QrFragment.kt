package com.example.warehouse.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.warehouse.R
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import com.example.warehouse.model.Goal
import com.example.warehouse.model.Result
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [QrFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class QrFragment : Fragment() {
    // TODO: Rename and change types of parameters
    // private var param1: String? = null
    // private var param2: String? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private var cameraProvider: ProcessCameraProvider? = null
    private var navController: NavController? = null

    private lateinit var viewModel: ApiViewModel

    private var currentGoalId: String? = null
    private var currentGoalLabel: String? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_qr, container, false)
        previewView = view.findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()
        return view
    }

    private suspend fun completeGoalAndNavigate() {
        viewModel.completeGoal()
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    findNavController().navigate(R.id.action_QrFragment_to_SuccessFragment)
                    }
                is Result.Error -> {
                    _handleError(result.toString())
                }
                Result.Loading -> TODO()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("QrFragment", "onViewCreated")

        viewModel = ViewModelProvider(this, ApiViewModelFactory(requireContext()))
            .get(ApiViewModel::class.java)

        viewModel.currentGoal.observe(viewLifecycleOwner) { currentGoal ->
            when (currentGoal) {
                is Result.Success -> {
                    currentGoalId = currentGoal.data?.id
                    currentGoalLabel = currentGoal.data?.label
                    view.findViewById<TextView>(R.id.stationId).text = currentGoal.data?.id.toString()
                }
                is Result.Error -> {
                    _handleError(currentGoal.toString())
                }
                Result.Loading -> TODO()
            }
        }

        viewModel.fetchCurrentGoal()
        lifecycleScope.launch {
            viewModel.startGoal()
        }
        startCamera()
    }

    private fun _handleError(errorMsg: String) {
        val builder = AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder
            .setTitle("Experiment error")
            .setMessage("""There was an error with the app. Please notify an experimenter.
                |
                |Error details:
                |${errorMsg}
            """.trimMargin())
            .setPositiveButton("Okay") { dialog, id ->
                // user tries again
            }
        builder.create().show()
    }

    private fun onQrDetected(qrCode: String) {
        Log.d("QrFragment", "QR found: $qrCode")

        // navigate to different fragments depending on whether QR is correct
        if (qrCode == currentGoalId) {
            stopCamera()

            lifecycleScope.launch {
                completeGoalAndNavigate()
            }

            activity?.runOnUiThread {
                Log.d("QrFragment", "Navigating to success fragment")
                findNavController().navigate(R.id.action_QrFragment_to_SuccessFragment)
            }
        } else {
            // stopCamera()
            // Log.d("QrFragment", "Navigating to failure fragment")
            // findNavController().navigate(R.id.action_QrFragment_to_FailureFragment)

            // create dialog warning about incorrect QR
            Log.d("QrFragment", "Creating incorrect QR dialog")
            activity?.runOnUiThread {
                stopCamera()
                val builder = AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setTitle("Scan error")
                    .setMessage("Incorrect QR code. Please try again.")
                    .setPositiveButton("Retry") { dialog, id ->
                        // user tries again
                        startCamera()
                    }
                builder.create().show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer {qrCode -> onQrDetected(qrCode)})
                }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
//            } catch (exc: com.google.zxing.NotFoundException) {
//                // Ignore QR code not found
            } catch (exc: Exception) {
                Log.e("QrFragment", "Binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun stopCamera() {
        previewView.post{
            cameraProvider?.unbindAll()
            }
    }

    private class QrCodeAnalyzer(private val onQrCodeDetected: (String) -> Unit) :
        ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
//            if (image.format != ImageFormat.YUV_420_888) {
//                image.close()
//                Log.e("QrCodeAnalyzer","Incompatible image format: %d".format(image.format))
//                return
//            }

            try {

                val yBuffer = image.planes[0].buffer
                val uBuffer = image.planes[1].buffer
                val vBuffer = image.planes[2].buffer

                val ySize = yBuffer.remaining()
                val uSize = uBuffer.remaining()
                val vSize = vBuffer.remaining()

                val nv21 = ByteArray(ySize + uSize + vSize)

                yBuffer.get(nv21, 0, ySize)
                uBuffer.get(nv21, ySize + vSize, uSize)
                vBuffer.get(nv21, ySize, vSize)

                val source: LuminanceSource = PlanarYUVLuminanceSource(
                    nv21,
                    image.width,
                    image.height,
                    0, 0,
                    image.width,
                    image.height,
                    false
                )

                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                val result = QRCodeMultiReader().decode(binaryBitmap)
                onQrCodeDetected(result.text)
            } catch (e: com.google.zxing.NotFoundException) {
                // Ignore QR code not found
            } catch (e: com.google.zxing.ChecksumException) {
                // ignore checksum error
            } catch (e: Exception) {
                Log.e("QrCodeAnalyzer", "Error analyzing QR code:", e)
            } finally {
                image.close()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment QrFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QrFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}