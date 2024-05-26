package com.example.e_commerce.fragments.shopping

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce.R
import com.example.e_commerce.adapters.BestProductAdapter
import com.example.e_commerce.adapters.CategoriesRecyclerAdapter
import com.example.e_commerce.adapters.SearchRecyclerAdapter
import com.example.e_commerce.databinding.FragmentSearchBinding
import com.example.e_commerce.util.Resource
import com.example.e_commerce.util.VerticalItemDecoration
import com.example.e_commerce.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment:Fragment(R.layout.fragment_search) {
    private val TAG = "SearchFragment"
    private lateinit var binding:FragmentSearchBinding
    private val viewModel:SearchViewModel by viewModels<SearchViewModel>()
    private val searchRecyclerAdapter by lazy { SearchRecyclerAdapter() }
    private val categoriesRecyclerAdapter: CategoriesRecyclerAdapter by lazy { CategoriesRecyclerAdapter() }
    private lateinit var inputMethodManger: InputMethodManager



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvSearchCategories()
        setupSearchRv()
        showKeyboardAutomatically()
        searchProducts()
        collectSearch()
        onSearchTextClick()
        onCancelTvClick()

    }

    private fun onCancelTvClick() {
        binding.tvCancel.setOnClickListener {
            searchRecyclerAdapter.differ.submitList(emptyList())
            binding.edSearch.setText("")
            hideCancelTv()
        }
    }

    private fun onSearchTextClick() {
        searchRecyclerAdapter.onItemClick = { product ->
            val bundle = Bundle()
            bundle.putParcelable("product", product)

            /**
             * Hide the keyboard
             */

            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(requireView().windowToken, 0)

            findNavController().navigate(
                R.id.action_searchFragment_to_productDetailsFragment,
                bundle
            )

        }
    }

    var job: Job? = null
    private fun searchProducts() {
        binding.edSearch.addTextChangedListener { query ->
            val queryTrim = query.toString().trim()
            if (queryTrim.isNotEmpty()) {
                val searchQuery = query.toString().substring(0, 1).toUpperCase()
                    .plus(query.toString().substring(1))
                job?.cancel()
                job = CoroutineScope(Dispatchers.IO).launch {
                    delay(500L)
                    viewModel.search(searchQuery)
                }
            } else {
                searchRecyclerAdapter.differ.submitList(emptyList())
                hideCancelTv()
            }
        }
    }

    private fun hideCancelTv() {
        binding.tvCancel.visibility = View.GONE
        binding.imgMicrophone.visibility = View.VISIBLE
        binding.imgScan.visibility = View.VISIBLE
        binding.frameMicrophone.visibility = View.VISIBLE
        binding.frameScan.visibility = View.VISIBLE
    }

    private fun showKeyboardAutomatically() {
        inputMethodManger =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )

        binding.edSearch.requestFocus()

    }

    private fun setupSearchRv() {
        binding.rvSearch.apply {
            adapter = searchRecyclerAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupRvSearchCategories() {

        binding.rvCategories.apply {
            adapter = categoriesRecyclerAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration(40))
        }
    }

      fun collectSearch() {
        lifecycleScope.launchWhenCreated {
            viewModel.search.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        Log.d("test", "Loading")
                        return@collectLatest
                    }
                    is Resource.Success -> {
                        val products = it.data
                        searchRecyclerAdapter.differ.submitList(products)
                        showChancelTv()
                        return@collectLatest

                    }
                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        showChancelTv()
                        return@collectLatest
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showChancelTv() {
        binding.tvCancel.visibility = View.VISIBLE
        binding.imgMicrophone.visibility = View.GONE
        binding.imgScan.visibility = View.GONE
        binding.frameMicrophone.visibility = View.GONE
        binding.frameScan.visibility = View.GONE
    }


}