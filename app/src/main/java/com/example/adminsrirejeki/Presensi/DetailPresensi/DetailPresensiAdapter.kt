package com.example.adminsrirejeki.Presensi.DetailPresensi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsrirejeki.R
import java.text.SimpleDateFormat
import java.util.*

class DetailPresensiAdapter(
    private val tanggalList: List<String>,
    private val presensiMap: Map<String, Presensi>,
    private val tanggalAbsensiTerakhir: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DATE_FORMAT = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    private val JAM_FORMAT = SimpleDateFormat("HH:mm", Locale("id", "ID"))

    override fun getItemCount(): Int = tanggalList.size

    override fun getItemViewType(position: Int): Int {
        val tanggal = tanggalList[position]
        // Cek apakah tanggal absensi lebih kecil dari tanggal absensi terakhir
        return if (tanggal <= tanggalAbsensiTerakhir && !presensiMap.containsKey(tanggal)) {
            2 // Tanggal tanpa presensi
        } else if (presensiMap.containsKey(tanggal)) {
            1 // Tanggal dengan presensi
        } else {
            0 // Tanggal tanpa presensi
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> MasukViewHolder(inflater.inflate(R.layout.item_detail_presensi_masuk, parent, false))
            2 -> TidakMasukViewHolder(inflater.inflate(R.layout.item_detail_presensi_tidak_masuk, parent, false))
            else -> BelumAdaViewHolder(inflater.inflate(R.layout.item_detail_presensi_belum_ada, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tanggal = tanggalList[position]
        val tanggalFormatted = DATE_FORMAT.format(SimpleDateFormat("yyyy-MM-dd").parse(tanggal)!!)
        when (holder) {
            is MasukViewHolder -> {
                val presensi = presensiMap[tanggal]!!
                holder.bind(presensi, tanggalFormatted)
            }
            is TidakMasukViewHolder -> {
                (holder as TidakMasukViewHolder).tvHari.text = tanggalFormatted
            }
            is BelumAdaViewHolder -> {
                (holder as BelumAdaViewHolder).tvHari.text = tanggalFormatted
            }
        }
    }

    inner class MasukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvJam: TextView = itemView.findViewById(R.id.tvJam)
        private val tvStatusPresensi: TextView = itemView.findViewById(R.id.tvStatusPresensi)
        private val tvHari: TextView = itemView.findViewById(R.id.tvHari)

        fun bind(presensi: Presensi, tanggal: String) {
            val waktuSplit = presensi.waktu.split(" ")
            tvJam.text = waktuSplit.getOrNull(1)?.substring(0, 5) ?: "-"  // Menampilkan hanya jam dan menit
            tvStatusPresensi.text = presensi.jenis_presensi.replace("Presensi Berhasil", "Berhasil")
            tvHari.text = tanggal
        }
    }

    inner class TidakMasukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHari: TextView = itemView.findViewById(R.id.tvHari)
    }

    inner class BelumAdaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHari: TextView = itemView.findViewById(R.id.tvHari)
    }
}


