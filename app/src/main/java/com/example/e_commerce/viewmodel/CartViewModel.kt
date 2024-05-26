package com.example.e_commerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.CartProduct
import com.example.e_commerce.firebase.FirebaseCommon
import com.example.e_commerce.helper.getProductPrice
import com.example.e_commerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val auth:FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
):ViewModel() {
    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()


    val productsPrice = cartProducts.map {
        when(it){
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }

    }
      private fun calculatePrice(data: List<CartProduct>): Float {
          return  data.sumByDouble {cartProduct ->
              (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
          }.toFloat()
    }

    init {
        getCartProducts()
    }

    fun deleteCartProduct(cartProduct: CartProduct){
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firebaseFirestore.collection("user").document(auth.uid!!).collection("cart").document(documentId).delete()
        }

    }

    private fun getCartProducts(){
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }
        firebaseFirestore.collection("user").document(auth.uid!!).collection("cart") /*.get()*/ // this time we are not going to use get but we want to use addSnapshotListener and this is like a callback that gets executed whenever the cart in this collection get changed and we use it because we wanna to refresh our ui every time the user adds a new product
            .addSnapshotListener { value, error ->

                if (error != null || value == null){
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                    }else{
                        cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(cartProducts))
                    }
                }
            }


    }


    fun changeQuantity(cartProduct: CartProduct,quantityChanging: FirebaseCommon.QuantityChanging){

        val index = cartProducts.value.data?.indexOf(cartProduct) // this will return us the index of this cart product that is  inside our cartProducts State then we will use this index to get the document pf this product
        // it might be -1 because if the user spans the button of increase or decrease  this getCartProducts function will not guarantee that it will change our state as fast as the user pressing the button , so there's a little bit of delay
        /* index could be equal -1 if the function [getCartProducts] delays which will also delay yhe result we expect to be inside the [_cartProducts]
        * and ro prevent the app from crashing we make a check
        * */

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when(quantityChanging){

                 FirebaseCommon.QuantityChanging.INCREASE -> {
                     viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                     increaseQuantity(documentId)
                 }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1){
                        viewModelScope.launch{_deleteDialog.emit(cartProduct)}
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId:String) {
        firebaseCommon.decreaseQuantity(documentId){_,e ->
            if (e != null ) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId:String) {
        firebaseCommon.increaseQuantity(documentId){_,e ->
            if (e != null ) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

}