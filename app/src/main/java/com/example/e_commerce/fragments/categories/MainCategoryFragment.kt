package com.example.e_commerce.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.adapters.BestDealsAdapter
import com.example.e_commerce.adapters.BestProductAdapter
import com.example.e_commerce.adapters.SpecialProductsAdapter
import com.example.e_commerce.databinding.FragmentMainCaregoryBinding
import com.example.e_commerce.util.Resource
import com.example.e_commerce.util.showBottomNavigationView
import com.example.e_commerce.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG = "MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_caregory) {
    private lateinit var binding: FragmentMainCaregoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter: BestProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCaregoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductRV()
        observeSpecialProduct()
        setupBestDealsRV()
        observeBestDeals()
        setupBestProduct()
        observeBestProducts()
        atTheEndOfNestedScrollView()
        atTheEndOfBestDealsRV()
        atTheEndOfSpecialProductsRV()
        onItemClick()

    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    private fun onItemClick() {
        specialProductsAdapter.onClick = {
            val bundle = Bundle().apply {putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }
        bestDealsAdapter.onClick = {
            val bundle = Bundle().apply {putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }
        bestProductAdapter.onClick = {
            val bundle = Bundle().apply {putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }

    }

    private fun atTheEndOfSpecialProductsRV() {
        binding.rvSpecialProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.rvSpecialProducts.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItemPosition == totalItemCount - 1) {
                    viewModel.fetchSpecialProducts()
                }
            }
        })
    }

    private fun atTheEndOfBestDealsRV() {
        binding.rvBestDeals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.rvBestDeals.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItemPosition == totalItemCount - 1) {
                    viewModel.fetchBestDeals()
                }
            }
        })

    }

    private fun atTheEndOfNestedScrollView() {
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                // if this ture that is means that we are reached to the bottom of our nested scroll view ( the (v.getChildAt(0).bottom) check if the bottom of our nested scroll is (<= v.height + scrollY) equal or less than to the height plus the amount of scrolled y -> if that the case then we are at the bottom of our nested scroll view )
                viewModel.fetchBestProduct()

            }

        })
    }

    private fun observeBestProducts() {
        lifecycleScope.launchWhenCreated {
            viewModel.bestProducts.collectLatest {

                // we can use collect latest because we are only interested in the latest list
                when (it) {
                    is Resource.Loading -> {
                        binding.bestProductsProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        bestProductAdapter.differ.submitList(it.data)
                        binding.bestProductsProgressbar.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.bestProductsProgressbar.visibility = View.GONE
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }

                    else -> Unit


                }


            }
        }
    }

    private fun setupBestProduct() {
        bestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }

    private fun observeSpecialProduct() {
        lifecycleScope.launchWhenCreated {
            viewModel.specialProducts.collectLatest {
                // we can use collect latest because we are only interested in the latest list
                when (it) {
                    is Resource.Loading -> {
                      showLoading()
                    }

                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                      hideLoading()
                    }

                    is Resource.Error -> {
                      hideLoading()

                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }

                    else -> Unit


                }
            }
        }
    }

    private fun observeBestDeals() {
        lifecycleScope.launchWhenCreated {

            viewModel.bestDeals.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                      showLoading()
                    }

                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                       hideLoading()
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "${it.message.toString()} BestDeals",
                            Toast.LENGTH_SHORT
                        ).show()
                       hideLoading()
                    }

                    else -> Unit

                }
            }
        }
    }

    private fun setupBestDealsRV() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDeals.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility = View.VISIBLE
    }

    private fun setupSpecialProductRV() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }


    }


}