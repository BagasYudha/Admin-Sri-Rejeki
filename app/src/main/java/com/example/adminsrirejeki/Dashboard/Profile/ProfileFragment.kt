package com.example.adminsrirejeki.Dashboard.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.adminsrirejeki.AppViewModel
import com.example.adminsrirejeki.databinding.FragmentProfileBinding
import com.google.firebase.database.FirebaseDatabase

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
                binding.etFullName.setText(karyawan.fullname ?: "Tidak diketahui")
                binding.etUsername.setText(karyawan.username ?: "Tidak diketahui")
                binding.etEmail.setText(karyawan.email ?: "Tidak diketahui")
                binding.etPassword.setText(karyawan.password ?: "Tidak diketahui")
            } else {
                Log.d("ProfileFragment", "Karyawan tidak ditemukan")
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnEdit.setOnClickListener {
            showEditDalog()
        }

        binding.btnHapus.setOnClickListener {
            showDeleteDialog()
        }

    }

    private fun showEditDalog() {
        val bindingDialog = com.example.adminsrirejeki.databinding.DialogEditBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).setView(bindingDialog.root).create()

        bindingDialog.btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        bindingDialog.btnDialogEdit.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            val karyawan = viewModel.selectedKaryawan.value

            if (karyawan != null && !karyawan.id.isNullOrEmpty()) {
                val updatedMap = mapOf(
                    "fullname" to fullName,
                    "username" to username,
                    "email" to email,
                    "password" to password
                )

                val dbRef = FirebaseDatabase.getInstance().getReference("users")
                dbRef.child(karyawan.id!!).updateChildren(updatedMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), "Gagal memperbarui: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Data karyawan tidak valid", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showDeleteDialog() {
        val bindingDialog = com.example.adminsrirejeki.databinding.DialogHapusBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).setView(bindingDialog.root).create()

        bindingDialog.btnDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        bindingDialog.btnDialogHapus.setOnClickListener {
            val karyawan = viewModel.selectedKaryawan.value

            if (karyawan != null && !karyawan.id.isNullOrEmpty()) {
                val dbRef = FirebaseDatabase.getInstance().getReference("users")
                dbRef.child(karyawan.id!!).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        findNavController().navigateUp()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(requireContext(), "Gagal menghapus: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Data karyawan tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

}