package com.example.adminsrirejeki

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.adminsrirejeki.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Inisialisasi binding dulu
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge() // <-- boleh setelah setContentView

        val navController = findNavController(R.id.fragmentContainerView)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.bottomBar.setOnItemSelectedListener { item ->
            val destinationId = when (item.itemId) {
                R.id.dashboardFragment -> R.id.dashboardFragment
                R.id.presensiFragment -> R.id.presensiFragment
                R.id.notifikasiFragment -> R.id.notifikasiFragment
                else -> throw IllegalArgumentException("Unexpected item selected")
            }

            // Hindari re-navigate ke destinasi yang sama
            if (navController.currentDestination?.id != destinationId) {
                navController.navigate(destinationId, null, navOptions)
            }

            true
        }
    }

}