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
                    karyawan?.id = data.key  // <--- ini penting
                    if (karyawan != null) list.add(karyawan)
                }
                onResult(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
