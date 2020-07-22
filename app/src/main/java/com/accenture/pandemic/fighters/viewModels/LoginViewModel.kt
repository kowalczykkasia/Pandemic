package com.accenture.pandemic.fighters.viewModels

import android.content.Context
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.utils.FirebaseAuthentication
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.accenture.pandemic.fighters.utils.Storage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.lang.Exception
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val firebaseAuthentication: FirebaseAuthentication,
    private val storage: Storage,
    private val context:Context
) : ViewModel() {

    var done = MutableLiveData<Int>()
    var block = MutableLiveData<PopupWindow>()
    val REQUEST = 9001

    fun logIn(email: String, password: String) {
        viewModelScope.launch {
            firebaseAuthentication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("TAG", "LoginUserWithEmail:success");
                        firebaseAuthentication.setCurrentUser()
                        storage.downloadProfileImage(firebaseAuthentication.auth.currentUser!!.uid,context)
                        done.postValue(1)
                        return@addOnCompleteListener
                    } else {
                        done.postValue(2)

                }
            }
        }
    }

    fun googleSignIn(fragment: Fragment, activity: Activity): GoogleSignInClient =
        firebaseAuthentication.googleSignIn(fragment, activity)

    fun firebaseAuthWithGoogle(idToken: String, activity: Activity) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        activity.let {
            firebaseAuthentication.auth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "signInWithCredential:success")
                        firebaseAuthentication.setCurrentUser()
                        storage.downloadProfileImage(firebaseAuthentication.auth.currentUser!!.uid,context)
                        done.postValue(1)
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.exception)
                        done.postValue(2)
                    }
                }
        }
    }

}
