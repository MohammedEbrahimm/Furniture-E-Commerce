package com.example.e_commerce.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.activities.ShoppingActivity
import com.example.e_commerce.databinding.FragmentIntroductionBinding
import com.example.e_commerce.viewmodel.IntroductionViewModel
import com.example.e_commerce.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.e_commerce.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.navigate.collect{
                when(it){
                     SHOPPING_ACTIVITY -> {
                         Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                             // when we use this flag this will make sure to kind of pop this activity from the stack so when the users login into their account they will navigate to the shopping activity and if they navigate back we don't need to go back to the login and register activity
                             // so this flag will make sure to remove that activity
                             startActivity(intent)
                         }

                    }
                    ACCOUNT_OPTIONS_FRAGMENT->{
                        findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)

                    }
                    else -> Unit

                }
            }
        }



        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)

        }
    }
}