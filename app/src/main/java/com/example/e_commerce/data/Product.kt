package com.example.e_commerce.data
import android.os.Parcelable
import  kotlinx.parcelize.Parcelize
@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
) : Parcelable {
    constructor():this("0","","",0f,images = emptyList())
}
// On Android, Parcelable is an interface that a class can implement to be passed within an Intent from an Activity to another one, this way, transporting data from one Activity to another one
// But with thanks to KOTLIN, we do not need to override these methods. We can directly use parcelize to handle this with minimal code.
//This Parcelize annotation tells the Kotlin compiler to generate the writeToParcel(), and describeContents() methods, as well as a CREATOR factory class automatically.
//The class can be a data class but it’s optional. All properties to serialize have to be declared in the primary constructor, and like a data class…

