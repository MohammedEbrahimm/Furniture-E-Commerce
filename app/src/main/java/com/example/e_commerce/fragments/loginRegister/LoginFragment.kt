package com.example.e_commerce.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.activities.ShoppingActivity
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.example.e_commerce.dialog.setupBottomSheetDialog
import com.example.e_commerce.util.Resource
import com.example.e_commerce.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString().trim()
                if (email.isEmpty()) {

                    binding.edEmailLogin.apply {
                        requestFocus()
                        error = "Email cannot be empty"
                    }


                } else if (password.isEmpty()) {

                    binding.edPasswordLogin.apply {
                        requestFocus()
                        error = "Enter the password"
                    }


                } else {
                    viewModel.login(email, password)
                }


            }


        }

        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog {email->
                viewModel.resetPassword(email)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when (it) {
                    is Resource.Loading -> {


                    }

                    is Resource.Success -> {

                        Snackbar.make(requireView(),"Reset link was sent to your email",Snackbar.LENGTH_LONG).show()

                    }

                    is Resource.Error -> {

                        Snackbar.make(requireView(),"Error:${it.message}",Snackbar.LENGTH_LONG).show()


                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()

                    }

                    is Resource.Success -> {

                        binding.buttonLoginLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            // when we use this flag this will make sure to kind of pop this activity from the stack so when the users login into their account they will navigate to the shopping activity and if they navigate back we don't need to go back to the login and register activity
                            // so this flag will make sure to remove that activity
                            startActivity(intent)

                        }

                    }

                    is Resource.Error -> {

                        binding.buttonLoginLogin.revertAnimation()
                        Toast.makeText(requireActivity(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

    }

}