package com.example.adminsrirejeki

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminsrirejeki.Dashboard.Karyawan
import com.example.adminsrirejeki.Dashboard.KaryawanRepository

class AppViewModel : ViewModel() {

    private val repository = KaryawanRepository()

    private val _karyawanList = MutableLiveData<List<Karyawan>>()
    val karyawanList: LiveData<List<Karyawan>> = _karyawanList

    private val _selectedKaryawan = MutableLiveData<Karyawan>()
    val selectedKaryawan: LiveData<Karyawan> get() = _selectedKaryawan

    fun loadKaryawanVm() {
        repository.getAllKaryawanRepo { result ->
            _karyawanList.value = result
            Log.d("MyDebugAppViewModel", "Karyawan list loaded: $result")
        }
    }

    fun setSelectedKaryawanVm(karyawan: Karyawan) {
        Log.d("MyDebugAppViewModel", "Setting selected karyawan: $karyawan")
        _selectedKaryawan.value = karyawan
    }
}