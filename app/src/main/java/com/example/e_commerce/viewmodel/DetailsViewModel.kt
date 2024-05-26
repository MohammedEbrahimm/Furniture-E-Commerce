package com.example.e_commerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.CartProduct
import com.example.e_commerce.firebase.FirebaseCommon
import com.example.e_commerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()


    fun addUpdateProductInCart(cartProduct: CartProduct) {
        // this function that use if the product is already in the cart and the user on the add cart button again so in this case we just want to increase the quantity
        // and if that product was different or was not added into the cart at all so we want to actually add it
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!)
            .collection("cart")// collection("cart") if this collection is not created then it will create it
            .whereEqualTo("product.id", cartProduct.product.id)// we deal with nested object so we typed as product.id to access the id
            .get().addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) {
                        // if the document isEmpty that means we don`t have that product already added to the cart so we will add it
                        addNewProduct(cartProduct)
                    } else { // that means the product is already exists so we will increase the quantity
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct && product.selectedColor == cartProduct.selectedColor && product.selectedSize== cartProduct.selectedSize) { // this if is to check is the products are same because the user may be change color or size
                            val documentId = it.first().id // get the document Id
                            increaseQuantity(documentId, cartProduct)
                        } else {// Added New Product
                            addNewProduct(cartProduct)
                        }
                    }
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedToCart, e ->
            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(addedToCart!!))
                } else {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }

            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, e ->

            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(cartProduct))
                } else {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }

            }

        }

    }


}