package com.example.adminsrirejeki.Dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminsrirejeki.databinding.FragmentRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseApp.initializeApp(requireContext())

        database = FirebaseDatabase.getInstance().reference

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Button for creating the account
        binding.btnCreateAccount.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val fullname = binding.etFullName.text.toString().trim()
        val username = binding.etNewUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (fullname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Harap lengkapi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPasswordStrong(password)) {
            Toast.makeText(context, "Password harus memiliki minimal 8 karakter, mengandung huruf besar, angka, dan karakter khusus", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEmailValid(email)) {
            Toast.makeText(context, "Email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val userData = hashMapOf(
            "fullname" to fullname,
            "username" to username,
            "email" to email,
            "password" to password
        )

        // Save data to Firebase Realtime Database
        database.child("users").push()
            .setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(context, "Akun berhasil dibuat", Toast.LENGTH_SHORT).show()
                Log.d("RegisterFragment", "Data akun berhasil dikirim ke Realtime Database")
                // Navigate to login screen or dashboard
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal membuat akun: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegisterFragment", "Error mengirim data akun", e)
            }
    }

    private fun isPasswordStrong(password: String): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}\$".toRegex()
        return password.matches(regex)
    }

    private fun isEmailValid(email: String): Boolean {
        val regex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$".toRegex()
        return email.matches(regex)
    }
}
