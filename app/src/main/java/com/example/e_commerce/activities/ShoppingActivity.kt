package com.example.e_commerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.e_commerce.R
import com.example.e_commerce.viewmodel.CartViewModel
import com.example.e_commerce.databinding.ActivityShoppingBinding
import com.example.e_commerce.util.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }
    val viewModel by viewModels<CartViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupNavControllerWithBottomNavigation()
        notficateTheCartItem()

    }

     fun notficateTheCartItem() {

         lifecycleScope.launchWhenCreated {
             viewModel.cartProducts.collectLatest {
                 when(it){
                     is Resource.Success->{
                         val count = it.data?.size?:0
                         val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
                         bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                             number = count
                             backgroundColor = resources.getColor(R.color.g_blue)
                         }
                     }
                     else -> Unit
                 }
             }
         }
    }

    private fun setupNavControllerWithBottomNavigation() {
        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }

}
