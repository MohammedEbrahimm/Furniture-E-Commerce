package com.example.e_commerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.Category
import com.example.e_commerce.data.Product
import com.example.e_commerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category // we cant pass this argument with dagger hilt so we don't need to use dagger hilt here so we actually want to create our own factory for this class
) : ViewModel() {
    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct = _bestProducts.asStateFlow()

    val pagingInfo = PagingInformation()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }


    fun fetchOfferProducts() {
        if (!pagingInfo.isOfferPagingEnd) {
            viewModelScope.launch {
                _offerProducts.emit(Resource.Loading())
            }

            firestore.collection("Products").whereEqualTo("category", category.category)
                .whereNotEqualTo("offerPercentage", null).limit(pagingInfo.offerProductsPage * 4)
                .get()
                .addOnSuccessListener {
                    val offerProducts = it.toObjects(Product::class.java)
                    pagingInfo.isOfferPagingEnd =
                        offerProducts == pagingInfo.oldOfferProducts// first updating the value of(isPagingEnd) and check if comes product is equal to the old product
                    pagingInfo.oldOfferProducts = offerProducts

                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Success(offerProducts))
                    }

                }.addOnFailureListener {
                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {


            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }

            firestore.collection("Products").whereEqualTo("category", category.category)
                .whereEqualTo("offerPercentage", null).limit(pagingInfo.bestProductsPage * 4).get()
                .addOnSuccessListener {
                    val bestProduct = it.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd =
                        offerProducts == pagingInfo.oldBestProducts// first updating the value of(isPagingEnd) and check if comes product is equal to the old product
                    pagingInfo.oldBestProducts = bestProduct
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProduct))
                    }
                    pagingInfo.bestProductsPage++

                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }

    }

}

data class PagingInformation(
    var offerProductsPage: Long = 1,
    var oldOfferProducts: List<Product> = emptyList(),
    var isOfferPagingEnd: Boolean = false,


    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false,
)