package com.example.e_commerce.firebase

import com.example.e_commerce.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val cartCollection =
        firestore.collection("user").document(auth.uid!!).collection("cart")

    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document().set(cartProduct).addOnSuccessListener {
            onResult(cartProduct, null)

        }.addOnFailureListener {

            onResult(null, it)
        }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {

        // by using runTransaction you make sure that the operation happens at once,if any operation fails then everything runs with that transaction will be failed
        // so we will use it with firestore to retrieve our product and increase it quantity then save the updated product
        // and by the way there is runbatch and it's used to only read documents or read data from your firestore while runTransaction can read and write at the same time
        // so we will use runTransaction because we wanna to read first and then update our quantity
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId) // the path to the cartProduct
            val document = transition.get(documentRef)  // get that cartProduct as Snapshot
            val productObject = document.toObject(CartProduct::class.java)// cast it to CartProduct

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject =
                    cartProduct.copy(quantity = newQuantity) // the copy here is an extension function that will copy your object and then you can only change one data or one argument over that object

                transition.set(documentRef, newProductObject)// update the object in the firestore
            }

        }.addOnSuccessListener {
            onResult(documentId, null)

        }.addOnFailureListener {

            onResult(null, it)

        }

    }


    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {

        // by using runTransaction you make sure that the operation happens at once,if any operation fails then everything runs with that transaction will be failed
        // so we will use it with firestore to retrieve our product and increase it quantity then save the updated product
        // and by the way there is runbatch and it's used to only read documents or read data from your firestore while runTransaction can read and write at the same time
        // so we will use runTransaction because we wanna to read first and then update our quantity
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId) // the path to the cartProduct
            val document = transition.get(documentRef)  // get that cartProduct as Snapshot
            val productObject = document.toObject(CartProduct::class.java)// cast it to CartProduct

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject =
                    cartProduct.copy(quantity = newQuantity) // the copy here is an extension function that will copy your object and then you can only change one data or one argument over that object

                transition.set(documentRef, newProductObject)// update the object in the firestore
            }

        }.addOnSuccessListener {
            onResult(documentId, null)

        }.addOnFailureListener {

            onResult(null, it)

        }

    }

    // this is helper class and this class is going to help us to distinguish between the decrease and increase options
    enum class QuantityChanging{
        // create two entries
        INCREASE,DECREASE

    }
}