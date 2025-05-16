package com.example.adminsrirejeki

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.adminsrirejeki.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var badge: BadgeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val navController = findNavController(R.id.fragmentContainerView)

        database = FirebaseDatabase.getInstance().getReference("notifikasi")

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        // Observe Firebase for new notifications
        showNotificationBadge()

        binding.bottomBar.setOnItemSelectedListener { item ->
            val destinationId = when (item.itemId) {
                R.id.dashboardFragment -> R.id.dashboardFragment
                R.id.presensiFragment -> R.id.presensiFragment
                R.id.notifikasiFragment -> {
                    // Hapus badge saat fragment notifikasi dibuka
                    val badgeDrawable = binding.bottomBar.getOrCreateBadge(R.id.notifikasiFragment)
                    badgeDrawable.isVisible = false
                    R.id.notifikasiFragment
                }
                else -> throw IllegalArgumentException("Unexpected item selected")
            }

            if (navController.currentDestination?.id != destinationId) {
                navController.navigate(destinationId, null, navOptions)
            }
            true
        }
    }

    private fun showNotificationBadge() {
        database.orderByChild("status").equalTo("penggajian").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var showBadge = false
                for (notif in snapshot.children) {
                    val sudahDilihat = notif.child("seen").getValue(Boolean::class.java) ?: false
                    if (!sudahDilihat) {
                        showBadge = true
                        break
                    }
                }
                if (showBadge) {
                    val badge = binding.bottomBar.getOrCreateBadge(R.id.notifikasiFragment)
                    badge.isVisible = true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
