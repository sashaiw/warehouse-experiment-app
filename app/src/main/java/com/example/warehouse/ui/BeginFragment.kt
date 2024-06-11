package com.example.warehouse.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.warehouse.R
import com.example.warehouse.databinding.FragmentBeginBinding
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

import com.example.warehouse.model.Result

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class BeginFragment : Fragment() {

    private var _binding: FragmentBeginBinding? = null
    private lateinit var sharedPref: SharedPreferences

    private lateinit var client: HttpClient
    private lateinit var serverUrl: String

    private lateinit var viewModel: ApiViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        serverUrl = sharedPref.getString(getString(R.string.server_url_key), getString(R.string.server_url_default_value)) !!

        client = HttpClient(CIO)

        _binding = FragmentBeginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ApiViewModelFactory(requireContext()))
            .get(ApiViewModel::class.java)

        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    _handleError(result.toString())
//                    Toast.makeText(requireContext(),
//                        "There was an error with the app, please notify an experimenter.",
//                        Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    val intent = Intent(context, QrActivity::class.java)
                    startActivity(intent)
                }
                is Result.Loading -> TODO()
            }
        }

        binding.beginButton.setOnClickListener {
            viewModel.beginExperiment()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}