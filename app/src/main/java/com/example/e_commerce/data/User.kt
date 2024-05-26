package com.example.e_commerce.data

data class User(
    val firstName:String,
    val lastName:String,
    val email:String,
    var imagePath:String="" // user can't add image among Register so we will add initial value
    ){
    constructor(): this("","","","")
}
