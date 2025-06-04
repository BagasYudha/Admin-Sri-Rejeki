package com.example.adminsrirejeki

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
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
    private val sudahDiberiNotifikasi = mutableSetOf<String>() // Tracking karyawan yang sudah dapat notifikasi

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

        // Notifikasi badge bawah
        showNotificationBadge()

        // Pantau semua totalPresensi
        pantauTotalPresensi()

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

    private fun pantauTotalPresensi() {
        val gajiRef = FirebaseDatabase.getInstance().getReference("gaji")

        gajiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val username = data.child("username").getValue(String::class.java) ?: continue
                    val totalPresensi = data.child("totalPresensi").getValue(Int::class.java) ?: 0
                    val fullname = data.child("fullname").getValue(String::class.java) ?: "Karyawan"

                    if (totalPresensi >= 10 && !sudahDiberiNotifikasi.contains(username)) {
                        tampilkanNotifikasi("$fullname telah mencapai $totalPresensi Presensi, waktunya penggajian!")
                        sudahDiberiNotifikasi.add(username)
                    }

                    if (totalPresensi < 10 && sudahDiberiNotifikasi.contains(username)) {
                        sudahDiberiNotifikasi.remove(username)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun tampilkanNotifikasi(pesan: String) {
        val channelId = "presensi_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifikasi Presensi",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // PendingIntent untuk membuka PresensiFragment
        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_resource) // ganti jika file nav kamu beda
            .setDestination(R.id.presensiFragment)
            .setComponentName(MainActivity::class.java)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Pastikan drawable ini ada
            .setContentTitle("Penggajian")
            .setContentText(pesan)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
