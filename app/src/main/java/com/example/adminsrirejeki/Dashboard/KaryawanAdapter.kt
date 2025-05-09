package com.example.adminsrirejeki.Dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsrirejeki.R
import com.example.adminsrirejeki.databinding.ItemKaryawanBinding

class KaryawanAdapter(
    private var listKaryawan: List<Karyawan>,  // ganti menjadi var untuk memodifikasi list
    private val onItemClick: (Karyawan) -> Unit
) : RecyclerView.Adapter<KaryawanAdapter.KaryawanViewHolder>() {

    inner class KaryawanViewHolder(val binding: ItemKaryawanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(karyawan: Karyawan) {
            binding.tvNama.text = karyawan.fullname ?: "Tidak diketahui"
            binding.itemKaryawan.setOnClickListener {
                Log.d("MyDebugKaryawanAdapter", "Item clicked: $karyawan")
                onItemClick(karyawan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaryawanViewHolder {
        val binding = ItemKaryawanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KaryawanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KaryawanViewHolder, position: Int) {
        holder.bind(listKaryawan[position])
    }

    override fun getItemCount(): Int = listKaryawan.size

    // Fungsi untuk update list karyawan di adapter
    fun updateList(newList: List<Karyawan>) {
        listKaryawan = newList
        notifyDataSetChanged()
    }
}
