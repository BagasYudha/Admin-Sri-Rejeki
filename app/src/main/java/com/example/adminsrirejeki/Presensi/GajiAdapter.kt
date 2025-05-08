package com.example.adminsrirejeki.Presensi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsrirejeki.R

class GajiAdapter(
    private val listData: List<Gaji>,
    private val onItemClick: (Gaji) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_PAS_9 = 1
        private const val TYPE_LEBIH_9 = 2
    }

    override fun getItemViewType(position: Int): Int {
        val data = listData[position]
        return when {
            data.totalPresensi == 9 -> TYPE_PAS_9
            data.totalPresensi ?: 0 > 9 -> TYPE_LEBIH_9
            else -> TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_PAS_9 -> Pas9ViewHolder(inflater.inflate(R.layout.item_karyawan_presensi_9, parent, false))
            TYPE_LEBIH_9 -> Lebih9ViewHolder(inflater.inflate(R.layout.item_karyawan_presensi_gaji, parent, false))
            else -> NormalViewHolder(inflater.inflate(R.layout.item_karyawan_presensi, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = listData[position]
        when (holder) {
            is NormalViewHolder -> holder.bind(data)
            is Pas9ViewHolder -> holder.bind(data)
            is Lebih9ViewHolder -> holder.bind(data)
        }
    }

    override fun getItemCount(): Int = listData.size

    open inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindBase(data: Gaji) {
            itemView.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    inner class NormalViewHolder(itemView: View) : BaseViewHolder(itemView) {
        fun bind(data: Gaji) {
            itemView.findViewById<TextView>(R.id.tvNama).text = data.fullname
            itemView.findViewById<TextView>(R.id.totalPresensi).text = data.totalPresensi.toString()
            bindBase(data)
        }
    }

    inner class Pas9ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        fun bind(data: Gaji) {
            itemView.findViewById<TextView>(R.id.tvNama).text = data.fullname
            itemView.findViewById<TextView>(R.id.totalPresensi).text = data.totalPresensi.toString()
            bindBase(data)
        }
    }

    inner class Lebih9ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        fun bind(data: Gaji) {
            itemView.findViewById<TextView>(R.id.tvNama).text = data.fullname
            itemView.findViewById<TextView>(R.id.totalPresensi).text = data.totalPresensi.toString()
            bindBase(data)
        }
    }
}