package com.example.e_commerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.Product
import com.example.e_commerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDeals = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDeals: StateFlow<Resource<List<Product>>> = _bestDeals

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProduct()
    }

    fun fetchSpecialProducts() {
        if (!pagingInfo.isSpecialProductEnd) {

            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }

            firestore.collection("Products").limit(pagingInfo.specialProductPage * 2)
                .whereEqualTo("category", "Special Products").get().addOnSuccessListener { result ->
                    // the result here i QuerySnapshot so we need to convert it to our product data class
                    val specialProductsList = result.toObjects(Product::class.java)
                    pagingInfo.isSpecialProductEnd =
                        specialProductsList == pagingInfo.oldSpecialProduct
                    pagingInfo.oldSpecialProduct = specialProductsList


                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductsList))


                    }
                    pagingInfo.specialProductPage++

                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }

                }
        }


    }

    fun fetchBestDeals() {
        if (!pagingInfo.isBestDealsEnd) {
            viewModelScope.launch {
                _bestDeals.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.bestDealsPage * 2)
                .whereEqualTo("category", "Best Deals").get().addOnSuccessListener { result ->
                    val bestDealsList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestDealsList == pagingInfo.oldBestDeals
                    pagingInfo.oldBestDeals = bestDealsList

                    viewModelScope.launch {
                        _bestDeals.emit(Resource.Success(bestDealsList))
                    }
                    pagingInfo.bestDealsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDeals.emit(Resource.Error(it.message.toString()))
                    }
                }

        }

    }

    fun fetchBestProduct() {

        if (!pagingInfo.isPagingEnd) {

            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            // note if you want to use multiple queries in firestore you need to create an index at the firestore
            // queris such as whereEqualTo and orderby
            firestore.collection("Products").orderBy("id", Query.Direction.ASCENDING)
                .limit(pagingInfo.bestProductsPage * 10).get()
                // limit the content that is come from the firebase
                .addOnSuccessListener { result ->
                    val bestProductList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd =
                        bestProductList == pagingInfo.oldBestProducts// first updating the value of(isPagingEnd) and check if comes product is equal to the old product
                    pagingInfo.oldBestProducts = bestProductList


                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProductList))
                    }
                    pagingInfo.bestProductsPage++
                    // we will call this function again if the user reach to the bottom of the nested scroll view so we want to increase the page to increase the limit of coming product
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }

                }


        }
    }
}

data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false,


    var bestDealsPage: Long = 1,
    var oldBestDeals: List<Product> = emptyList(),
    var isBestDealsEnd: Boolean = false,


    var specialProductPage: Long = 1,
    var oldSpecialProduct: List<Product> = emptyList(),
    var isSpecialProductEnd: Boolean = false
)

// assignment search about the way to how you can detect when you reach to the end of the horizontal recycler view
// and implement paging to the two recycler view that is in the maincategory

/*
* the init block is a special block in Kotlin that allows you to initialize properties or execute code when an instance of a class is created. In the MainCategoryViewModel class, the init block is used to initialize the specialProducts property and invoke the fetchSpecialProducts() function when an instance of MainCategoryViewModel is created.

Here's a breakdown of what the init block does:

_specialProducts is a MutableStateFlow that holds the state of the special products. It starts with an initial value of Resource.Unspecified().

specialProducts is a StateFlow that exposes the _specialProducts flow as an immutable state to external observers.

Inside the init block, the fetchSpecialProducts() function is invoked. This function is responsible for fetching the special products from Firestore and updating the _specialProducts flow accordingly.

By calling fetchSpecialProducts() in the init block, the special products will be fetched and the state of _specialProducts will be updated as soon as an instance of MainCategoryViewModel is created.

The init block is a convenient place to perform setup operations or initialize properties that are required for the class to function properly. In this case, it ensures that the special products are fetched and the state flow is initialized when the MainCategoryViewModel is created.
*
* */