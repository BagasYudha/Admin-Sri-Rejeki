package com.example.adminsrirejeki.Dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminsrirejeki.R
import com.example.adminsrirejeki.databinding.FragmentDashboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val listKaryawan = mutableListOf<Karyawan>()
    private lateinit var karyawanAdapter: KaryawanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        karyawanAdapter = KaryawanAdapter(listKaryawan) {
            findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
        }

        // setup RecyclerView
        binding.rvRiwayatPresensi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayatPresensi.adapter = karyawanAdapter

        loadKaryawanFromFirebase()
    }


    private fun loadKaryawanFromFirebase() {
        val ref = FirebaseDatabase.getInstance().getReference("users") // atau path lain yang menyimpan data karyawan
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listKaryawan.clear()
                for (data in snapshot.children) {
                    val karyawan = data.getValue(Karyawan::class.java)
                    karyawan?.let { listKaryawan.add(it) }
                }
                karyawanAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}