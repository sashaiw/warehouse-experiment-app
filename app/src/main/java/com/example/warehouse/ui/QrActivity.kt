package com.example.warehouse.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.warehouse.R
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.warehouse.databinding.ActivityQrBinding

class QrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrBinding // Use ActivityQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_qr) as NavHostFragment
        // val navController = navHostFragment.navController
        // val navController = findNavController(R.id.nav_host_fragment_content_qr)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                if (navController.currentDestination?.id == R.id.QrFragment) {
//                    // Disable back button when on QrFragment
//                    Log.d("QrActivity", "Back button disabled on QrFragment")
//                } else {
//                    // Allow back navigation for other fragments
//                    isEnabled = false // Disable this callback temporarily
//                    onBackPressedDispatcher.onBackPressed() // Trigger default back behavior
//                    isEnabled = true // Re-enable the callback
//                }
            }
        })
    }
}