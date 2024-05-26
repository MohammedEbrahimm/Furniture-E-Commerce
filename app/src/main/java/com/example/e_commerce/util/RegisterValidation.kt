package com.example.e_commerce.util

sealed class RegisterValidation() {
    object Success : RegisterValidation()
    data class Failed(val message: String) :
        RegisterValidation() //Using data class their because we will want to send message
}

data class RegisterFieldsState(
    val email: RegisterValidation,
    val password: RegisterValidation
)
