package com.example.warehouse.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.warehouse.R
import com.example.warehouse.databinding.FragmentBeginBinding
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class BeginFragment : Fragment() {

    private var _binding: FragmentBeginBinding? = null
    private lateinit var sharedPref: SharedPreferences

    private lateinit var client: HttpClient
    private lateinit var serverUrl: String

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

        binding.beginButton.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            val intent = Intent(context, QrActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}