// we are using file because we want to use it later at the login fragment
package com.example.e_commerce.util

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {
    if (email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")
    if (!Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    ) // this is are function at patterns class that is check if the format of the email is correct(true) or not
        return RegisterValidation.Failed("Wrong Email Format")
    return RegisterValidation.Success

}

fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empty")
    if (password.length < 6)
        return RegisterValidation.Failed("Password should contains at least 6 characters")

    return RegisterValidation.Success


}

