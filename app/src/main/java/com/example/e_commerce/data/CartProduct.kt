package com.example.e_commerce.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int?,
    val selectedSize: String?
) : Parcelable {
    constructor():this(Product(),1,null,null)
}
