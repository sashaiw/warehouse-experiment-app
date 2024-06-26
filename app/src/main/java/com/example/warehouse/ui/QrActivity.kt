package com.example.warehouse.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.warehouse.R
import android.util.Log
import androidx.navigation.findNavController
import com.example.warehouse.databinding.ActivityQrBinding

class QrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrBinding // Use ActivityQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navController = findNavController(R.id.nav_host_fragment_content_qr)
        // Optional: navController.navigate(R.id.qrFragment)
    }
}