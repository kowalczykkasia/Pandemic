package com.accenture.pandemic.fighters.viewModels

import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.accenture.pandemic.fighters.Models.PROFILE_IMAGE_SIZE
import com.accenture.pandemic.fighters.utils.FirebaseAuthentication
import com.accenture.pandemic.fighters.utils.Storage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.squareup.picasso.Picasso
import com.google.firebase.auth.ktx.userProfileChangeRequest
import javax.inject.Inject


class ProfileViewModel @Inject constructor(
    private val firebaseAuthentication: FirebaseAuthentication,
    private val storage: Storage
) :
    ViewModel() {
    val photoBmp = MutableLiveData<Bitmap>()
    val updateDone = MutableLiveData<Boolean>()
    val block = MutableLiveData<PopupWindow>()

    init {
        photoBmp.value = storage.profileImageBmp
    }

    private fun getUserId() = firebaseAuthentication.getUserId()
    fun getUserName() = firebaseAuthentication.getUserName()
    fun getUserEmail() = firebaseAuthentication.getUserEmail()

    fun updateData(profileImageView: ImageView, email: String, name: String) {
        storage.uploadProfileImage(getUserId(), profileImageView)
        updateUserName(name)
        updateUserEmail(email)
    }

    fun setPhoto(profileImageView: ImageView, bitmap: Bitmap, context: Context) {

        profileImageView.setImageBitmap(
            Bitmap.createScaledBitmap(
                bitmap, PROFILE_IMAGE_SIZE,
                PROFILE_IMAGE_SIZE, false
            )
        )
        storage.profileImageBmp = bitmap

        GoogleSignIn.getLastSignedInAccount(context)?.let {
            Picasso.get().load(it.photoUrl).into(profileImageView)
        }

    }
    private fun updateUserName(name: String) {
        firebaseAuthentication.auth.currentUser!!.updateProfile(userProfileChangeRequest {
            displayName = name
        }).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(ContentValues.TAG, "User name updated.")
            }
        }
    }
    private fun updateUserEmail(email: String) {
        firebaseAuthentication.auth.currentUser!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "User email address updated.")
                    firebaseAuthentication.setCurrentUser()
                    updateDone.postValue(true)

                }
            }
    }
}



