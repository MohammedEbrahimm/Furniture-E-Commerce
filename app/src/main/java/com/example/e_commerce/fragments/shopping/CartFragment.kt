package com.example.e_commerce.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.R.*
import com.example.e_commerce.adapters.CartProductAdapter
import com.example.e_commerce.databinding.FragmentCartBinding
import com.example.e_commerce.firebase.FirebaseCommon
import com.example.e_commerce.util.Resource
import com.example.e_commerce.util.VerticalItemDecoration
import com.example.e_commerce.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

class CartFragment:Fragment(layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartProductAdapter by lazy { CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>() // this time we will use [activityViewModel] because we sharing the view model with shopping activity and if we use [viewModels] this will create another object and this is incorrect

    var totalPrice = 0f
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()
        collectCartProduct()
        collectProductPrice()


        cartProductAdapter.onProductClick = {
            val bundle = Bundle().apply {putParcelable("product",it.product)  }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,bundle)
        }
        cartProductAdapter.onPlusClick = {
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.INCREASE)
        }
        cartProductAdapter.onMinusClick = {
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.DECREASE)
        }
        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice , cartProductAdapter.differ.currentList.toTypedArray(),true)
            findNavController().navigate(action)
        }

        collectDeleteDialog()

    }

    private fun collectDeleteDialog() {
        lifecycleScope.launchWhenCreated {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel"){ dialog,_->
                        dialog.dismiss()

                    }
                    setPositiveButton("Yes"){dialog,_->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
    }

    fun collectProductPrice() {

        lifecycleScope.launchWhenCreated {
            viewModel.productsPrice.collectLatest { price ->
            price?.let {
                totalPrice = it

                binding.tvTotalPrice.text = "${String.format("%.2f", price)}"
            }

            }
        }
    }

    fun collectCartProduct() {

          lifecycleScope.launchWhenCreated {
              viewModel.cartProducts.collectLatest {
                  when (it) {
                      is Resource.Loading -> {
                          binding.progressbarCart.visibility = View.VISIBLE
                      }

                      is Resource.Success -> {
                          binding.progressbarCart.visibility = View.INVISIBLE
                          if (it.data!!.isEmpty()){
                              showEmptyCart()
                              hideOtherViews()
                          }else{
                              showOtherViews()
                              hideEmptyCart()
                              cartProductAdapter.differ.submitList(it.data)
                          }
                      }

                      is Resource.Error -> {
                          binding.progressbarCart.visibility = View.INVISIBLE
                          Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()

                      }

                      else -> Unit

                  }
              }
          }

    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
     binding.apply {
         layoutCartEmpty.visibility = View.INVISIBLE
     }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            adapter = cartProductAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

}