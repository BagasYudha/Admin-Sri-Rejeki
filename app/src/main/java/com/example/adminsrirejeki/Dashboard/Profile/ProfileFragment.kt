package com.example.adminsrirejeki.Dashboard.Profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.adminsrirejeki.AppViewModel
import com.example.adminsrirejeki.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: AppViewModel by activityViewModels()  // Pastikan menggunakan activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengamati perubahan pada selectedKaryawan
        viewModel.selectedKaryawan.observe(viewLifecycleOwner) { karyawan ->
            Log.d("ProfileFragment", "Selected Karyawan: $karyawan")

            if (karyawan != null) {
                Log.d("ProfileFragment", "Nama Lengkap: ${karyawan.fullname}")
                binding.tvFullName.text = karyawan.fullname
                binding.tvUsername.text = karyawan.username
                binding.tvEmail.text = karyawan.email
                binding.tvPassword.text = karyawan.password
            } else {
                Log.d("ProfileFragment", "Karyawan tidak ditemukan")
            }
        }


        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}