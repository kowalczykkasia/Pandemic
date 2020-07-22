package com.accenture.pandemic.fighters.viewModels

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.PopupWindow
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accenture.pandemic.fighters.utils.FirebaseAuthentication
import com.accenture.pandemic.fighters.utils.Storage
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.launch
import javax.inject.Inject


class RegistrationScreenViewModel @Inject constructor(
    private val firebaseAuthentication: FirebaseAuthentication,
    private val storage: Storage,
    private val context: Context
) :
    ViewModel() {

    var done = MutableLiveData<Int>()
    var block = MutableLiveData<PopupWindow>()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            firebaseAuthentication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseAuthentication.setCurrentUser()
                        storage.downloadProfileImage(
                            firebaseAuthentication.auth.currentUser!!.uid,
                            context
                        )
                        done.postValue(1)
                        return@addOnCompleteListener
                    } else {
                        done.postValue(2)
                    }
                }
        }
    }

    fun updateUserName(name: String) {
        firebaseAuthentication.auth.currentUser!!.updateProfile(userProfileChangeRequest {
            displayName = name
        }).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(ContentValues.TAG, "User name updated.")
            }
        }
    }
}
