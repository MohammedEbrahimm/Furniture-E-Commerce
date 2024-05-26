package com.example.e_commerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.Product
import com.example.e_commerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val search = _search.asStateFlow()

    fun search(searchQuery: String) {
        viewModelScope.launch {
            _search.emit(Resource.Loading())
        }
        firestore.collection("Products").orderBy("name")
            .startAt(searchQuery).endAt("\u03A9+$searchQuery")
            .limit(5)
            .get()
            .addOnSuccessListener {
                val productList = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _search.emit(Resource.Success(productList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {

                    _search.emit(Resource.Error(it.message.toString()))
                }
            }

    }



}