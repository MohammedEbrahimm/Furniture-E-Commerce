package com.example.e_commerce.data

// sealed class because we want to have objects of this class and each object is going to have its own category
sealed class Category(val category: String) {

 object Chair:Category("Chair")
 object Cupboard:Category("Cupboard")
 object Table:Category("Table")
 object Accessory:Category("Accessory")
 object Furniture:Category("Furniture")

}