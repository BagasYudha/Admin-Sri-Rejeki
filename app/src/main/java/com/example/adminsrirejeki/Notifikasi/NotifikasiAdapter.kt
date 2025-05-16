package com.example.adminsrirejeki.Notifikasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsrirejeki.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifikasiAdapter(private val notifikasiList: List<Notifikasi>) :
    RecyclerView.Adapter<NotifikasiAdapter.NotifikasiViewHolder>() {

    inner class NotifikasiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvName)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktu)
        val viewBadge: View = itemView.findViewById(R.id.viewBadge)  // badge titik merah
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifikasiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notifikasi, parent, false)
        return NotifikasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifikasiViewHolder, position: Int) {
        val notifikasi = notifikasiList[position]
        holder.tvNama.text = notifikasi.fullname
        holder.tvWaktu.text = getWaktuLalu(notifikasi.waktu)

        // Tampilkan badge jika seen == false
        if (notifikasi.seen == false) {
            holder.viewBadge.visibility = View.VISIBLE
        } else {
            holder.viewBadge.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = notifikasiList.size

    private fun getWaktuLalu(waktuStr: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val waktu = sdf.parse(waktuStr)
            val now = Date()
            val diff = now.time - waktu.time

            val menit = diff / (60 * 1000)
            val jam = diff / (60 * 60 * 1000)
            val hari = diff / (24 * 60 * 60 * 1000)

            when {
                hari > 0 -> "$hari hari lalu"
                jam > 0 -> "$jam jam lalu"
                menit > 0 -> "$menit menit lalu"
                else -> "Baru saja"
            }
        } catch (e: Exception) {
            "Waktu tidak valid"
        }
    }
}
