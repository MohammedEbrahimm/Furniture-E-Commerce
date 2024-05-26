package com.example.e_commerce.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.EcommerceApplication
import com.example.e_commerce.data.User
import com.example.e_commerce.util.RegisterValidation
import com.example.e_commerce.util.Resource
import com.example.e_commerce.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application // used to access the content resolver
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
      val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
      val updateInfo = _updateInfo.asStateFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    init {
        getUser()

    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            viewModelScope.launch {
                _resetPassword.emit(Resource.Success(email))

            }

        }.addOnFailureListener {

            viewModelScope.launch {
                _resetPassword.emit(Resource.Error(it.message.toString()))

            }

        }
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(it))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updateUser(user: User, imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()
        if (!areInputsValid) {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error("Check your inputs"))
            }
            return
        }
        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }
        if (imageUri == null) {
            saveUserInformation(user, true)
        } else {
            saveNewInformationWithNewImage(user, imageUri)
        }

    }

    private fun saveNewInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<EcommerceApplication>().contentResolver,
                    imageUri
                )
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl), false)

            } catch (e: Exception) {

                viewModelScope.launch {
                    _updateInfo.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInformation(user: User, shouldRetrieveOldImage: Boolean) {
        // this boolean to determine if we should get the old image or update it
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (shouldRetrieveOldImage) {
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }
            .addOnSuccessListener {
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Success(user))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}


/*
* In the provided code snippet, it appears to be a Kotlin class named UserAccountViewModel that extends AndroidViewModel. Let's break down the usage of AndroidViewModel, Application, and the content resolver:

AndroidViewModel:
The AndroidViewModel class is a subclass of ViewModel provided by the Android Jetpack library. It is designed specifically for use with Android components, such as Activities or Fragments, and provides a context-aware ViewModel implementation. AndroidViewModel is useful when you need to access resources or application-specific features that require a context, such as retrieving system services or accessing the application context.

Application:
The Application class represents the base class for maintaining a global application state. It is an Android component that acts as the entry point for the application's lifecycle. By passing an instance of Application to the AndroidViewModel constructor, you provide access to the application context within the view model. This can be useful when you need to perform operations that require a context, such as accessing system services or resources.

Content Resolver:
The content resolver is a key component in Android that provides access to various types of data stored on the device. It acts as an intermediary between the app and the underlying data sources, such as databases, files, or content providers. The content resolver allows you to perform operations like querying, inserting, updating, or deleting data.

To use the content resolver, you can obtain it from the application context or an activity context. Here's an example of how you can use the content resolver to query contacts:

kotlin
Copy
class MyViewModel(application: Application) : AndroidViewModel(application) {
    fun queryContacts() {
        val contentResolver = getApplication<Application>().contentResolver

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        // Process the cursor and retrieve the contact data
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                // Process the contact data
            }
            cursor.close()
        }
    }
}
In this example, the getApplication<Application>().contentResolver expression retrieves the content resolver using the application context available in the AndroidViewModel. Then, a query is performed using the content resolver to retrieve contact data.

Remember to handle appropriate permissions and perform necessary error handling when working with the content resolver.

Overall, the usage of AndroidViewModel, Application, and the content resolver in the provided code suggests that the UserAccountViewModel is intended to work with the Android framework, accessing resources, and performing operations that require a context within the view model, such as interacting with the Firestore database, Firebase authentication, and storage components.
*
* */





