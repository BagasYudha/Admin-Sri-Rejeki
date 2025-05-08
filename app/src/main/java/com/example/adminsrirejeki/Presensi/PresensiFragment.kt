package com.example.adminsrirejeki.Presensi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminsrirejeki.databinding.FragmentPresensiBinding
import com.google.firebase.database.*

class PresensiFragment : Fragment() {

    private var _binding: FragmentPresensiBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPresensiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPresensi.layoutManager = LinearLayoutManager(requireContext())
        fetchDataGaji()
    }

    private fun fetchDataGaji() {
        databaseRef = FirebaseDatabase.getInstance().getReference("gaji")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listData = mutableListOf<Gaji>()
                for (data in snapshot.children) {
                    val model = data.getValue(Gaji::class.java)
                    model?.let { listData.add(it) }
                }

                val sortedList = listData.sortedByDescending { it.totalPresensi ?: 0 }

                val adapter = GajiAdapter(sortedList) { selectedGaji ->
                    val bundle = Bundle().apply {
                        putString("username", selectedGaji.username)
                        putString("fullname", selectedGaji.fullname)
                        putInt("totalPresensi", selectedGaji.totalPresensi ?: 0)
                    }
                    findNavController().navigate(
                        com.example.adminsrirejeki.R.id.detailPresensiFragment,
                        bundle
                    )
                }
                binding.rvPresensi.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error jika perlu
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}