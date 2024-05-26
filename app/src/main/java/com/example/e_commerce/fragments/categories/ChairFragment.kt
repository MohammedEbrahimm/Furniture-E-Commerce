package com.example.e_commerce.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce.data.Category
import com.example.e_commerce.util.Resource
import com.example.e_commerce.viewmodel.CategoryViewModel
import com.example.e_commerce.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment : BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(
            firestore, Category.Chair
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectTheOfferProducts()
        collectTheBestProducts()

    }

    private fun collectTheOfferProducts() {
        lifecycleScope.launchWhenCreated {
            viewModel.offerProducts.collectLatest {
                when (it) {

                    is Resource.Loading -> {

                        showOfferLoading()
                    }

                    is Resource.Success -> {
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        hideOfferLoading()


                    }

                    else -> Unit
                }
            }
        }
    }

    private fun collectTheBestProducts() {
        lifecycleScope.launchWhenCreated {
            viewModel.bestProduct.collectLatest {
                when (it) {

                    is Resource.Loading -> {

                        showBestProductLoading()
                    }

                    is Resource.Success -> {
                        bestProductAdapter.differ.submitList(it.data)
                        hideBestProductLoading()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()

                        hideBestProductLoading()

                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onOfferPagingRequest() {
        viewModel.fetchOfferProducts()

    }

    override fun onBestProductsPagingRequest() {
        viewModel.fetchBestProducts()
    }
}