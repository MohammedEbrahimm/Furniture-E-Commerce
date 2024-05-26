package com.example.e_commerce.fragments.shopping

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.R.*
import com.example.e_commerce.adapters.ColorsAdapter
import com.example.e_commerce.adapters.SizesAdapter
import com.example.e_commerce.adapters.ViewPager2imagesAdapter
import com.example.e_commerce.data.CartProduct
import com.example.e_commerce.data.Product
import com.example.e_commerce.databinding.FragmentProductDetailsBinding
import com.example.e_commerce.util.Resource
import com.example.e_commerce.util.hideBottomNavigationView
import com.example.e_commerce.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment(layout.fragment_product_details) {
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPager2imagesAdapter by lazy { ViewPager2imagesAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val args by navArgs<ProductDetailsFragmentArgs>() // ProductDetailsFragmentArgs this class Auto generated class that has our product argument we specified inside the nav-graph
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product // to get the product that we are named at the nav-graph

        setupSizesRv()
        setupColorsRv()
        setupViewpager()
        setTheProductInfo(product)
        submitTheListToAdapters(product)
        hideBottomNavigationView()
        onItemCloseClick()
        onAddToCartButton(product)
        collectAddToCart()
    }

    private fun collectAddToCart() {
        lifecycleScope.launchWhenCreated {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))

                    }

                    is Resource.Error -> {
                        binding.buttonAddToCart.revertAnimation()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()

                    }

                    else -> Unit

                }
            }
        }
    }

    private fun onAddToCartButton(product: Product) {
        sizesAdapter.onItemClick = {
            selectedSize = it
        }
        colorsAdapter.onItemClick = {
            selectedColor = it
        }
        binding.buttonAddToCart.setOnClickListener {

            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
        }
    }

    private fun onItemCloseClick() {
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun submitTheListToAdapters(product: Product) {
        viewPager2imagesAdapter.diffUtil.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }
    }

    private fun setTheProductInfo(product: Product) {
        binding.apply {
            tvProductName.text = product.name
            product.offerPercentage?.let {
                val remainingPricePercentage = 1f - it
                val priceAfterOffer = remainingPricePercentage * product.price
                tvProductNewPrice.visibility = View.VISIBLE
                tvProductNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                tvProductOldPrice.paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG // this will make sure to draw a line over this text view
            }
            if (product.offerPercentage == null) tvProductNewPrice.visibility = View.GONE

            tvProductOldPrice.text = "$${product.price}"
            tvProductDescription.text = product.description
            if (product.colors.isNullOrEmpty()) tvProductColors.visibility = View.INVISIBLE
            if (product.sizes.isNullOrEmpty()) tvProductSizes.visibility = View.INVISIBLE
        }
    }

    private fun setupViewpager() {

        binding.apply {
            viewPagerProductImages.adapter = viewPager2imagesAdapter
        }
    }

    private fun setupColorsRv() {

        binding.rvColors.apply {

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = colorsAdapter
        }

    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = sizesAdapter
        }

    }
}