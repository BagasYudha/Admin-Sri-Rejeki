package com.example.adminsrirejeki.Dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsrirejeki.databinding.ItemKaryawanBinding

class KaryawanAdapter(private val listKaryawan: List<Karyawan>) :
RecyclerView.Adapter<KaryawanAdapter.KaryawanViewHolder>() {

    inner class KaryawanViewHolder(val binding: ItemKaryawanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaryawanViewHolder {
        val binding = ItemKaryawanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KaryawanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KaryawanViewHolder, position: Int) {
        val item = listKaryawan[position]
        holder.binding.tvNama.text = item.fullname ?: "Tidak diketahui"
    }

    override fun getItemCount(): Int = listKaryawan.size
}