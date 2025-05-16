package com.example.adminsrirejeki.Notifikasi

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsrirejeki.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.example.adminsrirejeki.databinding.FragmentNotifikasiBinding

class NotifikasiFragment : Fragment() {

    private lateinit var binding: FragmentNotifikasiBinding
    private lateinit var notifikasiAdapter: NotifikasiAdapter
    private lateinit var notifikasiList: MutableList<Notifikasi>

    // Simpan ID notifikasi yang belum dilihat
    private val unseenNotifikasiIds = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifikasiBinding.inflate(inflater, container, false)

        notifikasiList = mutableListOf()
        notifikasiAdapter = NotifikasiAdapter(notifikasiList)
        binding.rvNotifikasi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifikasi.adapter = notifikasiAdapter

        ambilDataNotifikasi()

        // Hapus badge titik merah di BottomNavigation
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_bar)
        val badge = bottomNav.getBadge(R.id.notifikasiFragment)
        badge?.isVisible = false

        return binding.root
    }

    private fun ambilDataNotifikasi() {
        val ref = FirebaseDatabase.getInstance().getReference("notifikasi")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notifikasiList.clear()
                unseenNotifikasiIds.clear()

                for (itemSnapshot in snapshot.children) {
                    val notifikasi = itemSnapshot.getValue(Notifikasi::class.java)
                    val key = itemSnapshot.key

                    if (notifikasi != null) {
                        notifikasiList.add(notifikasi)
                        if (notifikasi.seen == false && key != null) {
                            unseenNotifikasiIds.add(key)
                        }
                    }
                }

                notifikasiList.reverse()
                notifikasiAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat notifikasi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        tandaiNotifikasiSudahDilihat()
    }

    private fun tandaiNotifikasiSudahDilihat() {
        val ref = FirebaseDatabase.getInstance().getReference("notifikasi")
        for (id in unseenNotifikasiIds) {
            ref.child(id).child("seen").setValue(true)
        }
        unseenNotifikasiIds.clear()
    }
}
