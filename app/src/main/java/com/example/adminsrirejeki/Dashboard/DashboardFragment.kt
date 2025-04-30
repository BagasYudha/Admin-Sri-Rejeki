package com.example.adminsrirejeki.Dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminsrirejeki.AppViewModel
import com.example.adminsrirejeki.R
import com.example.adminsrirejeki.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var karyawanAdapter: KaryawanAdapter

    // BENAR: ambil ViewModel scoped to activity
    private val viewModel: AppViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        karyawanAdapter = KaryawanAdapter(emptyList()) {
            viewModel.setSelectedKaryawanVm(it)  // Menyimpan data karyawan yang dipilih
            findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
        }

        binding.rvRiwayatPresensi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayatPresensi.adapter = karyawanAdapter

        viewModel.karyawanList.observe(viewLifecycleOwner) {
            karyawanAdapter = KaryawanAdapter(it) { karyawan ->
                Log.d("MyDebugDashboardFragment", "Item clicked, setting selected karyawan: $karyawan")
                viewModel.setSelectedKaryawanVm(karyawan)
                findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
            }
            binding.rvRiwayatPresensi.adapter = karyawanAdapter
            karyawanAdapter.notifyDataSetChanged()
        }

        viewModel.loadKaryawanVm()

        binding.btnAdd.setOnClickListener{
            findNavController().navigate(R.id.action_dashboardFragment_to_registerFragment)
        }
    }
}
