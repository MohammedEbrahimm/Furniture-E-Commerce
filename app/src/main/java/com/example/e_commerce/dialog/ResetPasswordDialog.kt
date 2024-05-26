package com.example.e_commerce.dialog

import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.e_commerce.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setupBottomSheetDialog(
    onSendClick:(String)->Unit
){
    val dialog= BottomSheetDialog(requireContext(),R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog,null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val btnSend= view.findViewById<AppCompatButton>(R.id.btnSendResetPassword)
    val btnCansel= view.findViewById<AppCompatButton>(R.id.btnCancelResetPassword)

    btnSend.setOnClickListener {
        val email =  edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()

    }
    btnCansel.setOnClickListener {
        dialog.dismiss()
    }
}
