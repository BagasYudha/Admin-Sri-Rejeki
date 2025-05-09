package com.example.adminsrirejeki.Presensi.DetailPresensi

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsrirejeki.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import java.util.*

class DetailPresensiFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var tvTotalPresensi: TextView
    private lateinit var spinnerBulan: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    private lateinit var btnPenggajian: MaterialButton

    private var totalPresensi: Int = 0
    private var username: String? = null
    private var fullname: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_presensi, container, false)

        tvNama = view.findViewById(R.id.tvNama)
        tvTotalPresensi = view.findViewById(R.id.tvTotalPresensi)
        spinnerBulan = view.findViewById(R.id.spinnerBulan)
        recyclerView = view.findViewById(R.id.rvDetailPresensi)
        btnPenggajian = view.findViewById(R.id.btnPenggajian)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        fullname = arguments?.getString("fullname")
        val totalPresensiFromArgs = arguments?.getInt("totalPresensi", -1)
        username = arguments?.getString("username")

        tvNama.text = fullname ?: "Nama Tidak Ditemukan"

        val bulanList = listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bulanList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBulan.adapter = adapter

        val calendar = Calendar.getInstance()
        val currentMonthIndex = calendar.get(Calendar.MONTH)
        spinnerBulan.setSelection(currentMonthIndex)

        if (!username.isNullOrEmpty()) {
            database = FirebaseDatabase.getInstance().getReference("gaji").child(username!!)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalPresensi = snapshot.child("totalPresensi").getValue(Int::class.java)
                        ?: totalPresensiFromArgs ?: 0
                    tvTotalPresensi.text = "Presensi Beruntun: $totalPresensi"
                    btnPenggajian.isEnabled = totalPresensi >= 10
                }

                override fun onCancelled(error: DatabaseError) {
                    tvTotalPresensi.text = "Gagal mengambil data"
                }
            })
        } else {
            tvTotalPresensi.text = "Username tidak tersedia"
        }

        spinnerBulan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val bulan = position
                val tahun = calendar.get(Calendar.YEAR)
                if (!username.isNullOrEmpty()) {
                    tampilkanPresensiHarian(username!!, bulan, tahun)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnPenggajian.setOnClickListener {
            konfirmasiPenggajian()
        }

        return view
    }

    private fun tampilkanPresensiHarian(username: String, bulan: Int, tahun: Int) {
        val database = FirebaseDatabase.getInstance().getReference("presensi")

        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, bulan)
        cal.set(Calendar.YEAR, tahun)
        val hariDalamBulan = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val tanggalList = (1..hariDalamBulan).map {
            String.format("%04d-%02d-%02d", tahun, bulan + 1, it)
        }

        val presensiMap = mutableMapOf<String, Presensi>()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var tanggalTerakhir = "0000-00-00"

                snapshot.children.forEach { data ->
                    val presensi = data.getValue(Presensi::class.java)
                    presensi?.let {
                        if (it.username == username) {
                            val tanggal = it.waktu.split(" ").firstOrNull()
                            if (tanggal != null) {
                                presensiMap[tanggal] = it
                                if (tanggal > tanggalTerakhir) tanggalTerakhir = tanggal
                            }
                        }
                    }
                }

                Log.d("DetailPresensi", "Data: ${presensiMap.size} item, Tanggal terakhir: $tanggalTerakhir")
                recyclerView.adapter = DetailPresensiAdapter(tanggalList, presensiMap, tanggalTerakhir)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat presensi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun konfirmasiPenggajian() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Penggajian")
            .setMessage("Apakah $fullname telah dilakukan penggajian?")
            .setPositiveButton("OK") { _, _ -> resetTotalPresensi() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun resetTotalPresensi() {
        if (!username.isNullOrEmpty()) {
            val gajiRef = FirebaseDatabase.getInstance().getReference("gaji").child(username!!)
            gajiRef.child("totalPresensi").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentPresensi = snapshot.getValue(Int::class.java) ?: 0
                    val updatedPresensi = (currentPresensi - 10).coerceAtLeast(0)

                    gajiRef.child("totalPresensi").setValue(updatedPresensi)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Total presensi dikurangi 10", Toast.LENGTH_SHORT).show()
                            tvTotalPresensi.text = "Presensi Beruntun: $updatedPresensi"
                            btnPenggajian.isEnabled = updatedPresensi >= 10
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Gagal memperbarui presensi", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Gagal membaca presensi", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
