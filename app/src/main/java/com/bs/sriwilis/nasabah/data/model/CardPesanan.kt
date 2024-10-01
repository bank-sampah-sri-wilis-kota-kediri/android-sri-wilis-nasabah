package com.bs.sriwilis.nasabah.data.model

data class CardPesanan(
    val id_pesanan: String,
    val nominal_transaksi: String,
    val tanggal: String,
    val lat: String,
    val lng: String,
    val status_pesanan: String,
    val alamat_nasabah: String,
    val nama_nasabah: String,
    val no_hp_nasabah: String,
    val total_berat: Double
)