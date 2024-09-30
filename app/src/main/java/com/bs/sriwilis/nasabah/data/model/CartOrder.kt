package com.bs.sriwilis.nasabah.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CartOrder (
    val kategori: String,
    val berat_perkiraan: Int,
    val harga_perkiraan: Double,
    val gambar: String?,
) : Parcelable