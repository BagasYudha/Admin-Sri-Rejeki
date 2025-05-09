package com.example.adminsrirejeki.Dashboard

import com.google.firebase.database.*

class KaryawanRepository {
    private val dbRef = FirebaseDatabase.getInstance().getReference("users")

    fun getAllKaryawanRepo(onResult: (List<Karyawan>) -> Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Karyawan>()
                for (data in snapshot.children) {
                    val karyawan = data.getValue(Karyawan::class.java)
                    karyawan?.id = data.key
                    if (karyawan != null) list.add(karyawan)
                }

                // urutkan berdasarkan fullname (jika null, letakkan di akhir)
                val sortedList = list.sortedWith(compareBy(nullsLast()) { it.fullname?.lowercase() })
                onResult(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
