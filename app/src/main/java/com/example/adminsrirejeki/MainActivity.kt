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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // NAVBAR SETUP
        // Menghubungkan NavController dengan NavHostFragment
        val navController = findNavController(R.id.fragmentContainerView)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        // Setup BottomNavigationView dengan NavController dan animasi
        binding.bottomBar.setOnItemSelectedListener { item ->
            val destinationId = when (item.itemId) {
                R.id.dashboardFragment -> R.id.dashboardFragment
                R.id.presensiFragment -> R.id.presensiFragment
                R.id.notifikasiFragment -> R.id.notifikasiFragment
                else -> throw IllegalArgumentException("Unexpected item selected")
            }

            navController.navigate(destinationId, null, navOptions)
            true
        }
        // NAVBAR SETUP END


//        binding.buttonTest.setOnClickListener {
//            val intent = Intent(this, TestActivity::class.java)
//            startActivity(intent)
//        }

    }
}