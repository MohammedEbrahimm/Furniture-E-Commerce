package com.example.e_commerce.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.R
import com.example.e_commerce.adapters.BestProductAdapter
import com.example.e_commerce.databinding.FragmentBaseCategoryBinding
import com.example.e_commerce.util.showBottomNavigationView

// open keyword means that this class is extendable
open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductAdapter by lazy { BestProductAdapter() }
    protected val bestProductAdapter: BestProductAdapter by lazy { BestProductAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOfferRv()
        setupBestProductRv()
        atTheEndOfOfferRv()
        atTheEndOfNestedScroll()
        onItemClick()



    }

    private fun onItemClick() {
        bestProductAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }
        offerAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

    private fun atTheEndOfOfferRv() {
        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    onOfferPagingRequest()
                }
            }
        })
    }

    fun atTheEndOfNestedScroll() {
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                // if this ture that is means that we are reached to the bottom of our nested scroll view ( the (v.getChildAt(0).bottom) check if the bottom of our nested scroll is (<= v.height + scrollY) equal or less than to the height plus the amount of scrolled y -> if that the case then we are at the bottom of our nested scroll view )
                onBestProductsPagingRequest()

            }

        })
    }

    open fun onOfferPagingRequest() {

    }

    open fun onBestProductsPagingRequest() {


    }

    private fun setupBestProductRv() {

        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }

    private fun setupOfferRv() {

        binding.rvOffer.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }

    }

    fun showOfferLoading() {

        binding.offerProductsProgressbar.visibility = View.VISIBLE
    }

    fun hideOfferLoading() {

        binding.offerProductsProgressbar.visibility = View.GONE
    }

    fun showBestProductLoading() {

        binding.bestProductsProgressbar.visibility = View.VISIBLE
    }

    fun hideBestProductLoading() {
        binding.bestProductsProgressbar.visibility = View.GONE

    }
}