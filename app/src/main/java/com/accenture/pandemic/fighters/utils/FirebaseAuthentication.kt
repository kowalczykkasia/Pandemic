package com.accenture.pandemic.fighters.utils

import android.app.Activity
import android.content.Context
import android.content.ContentValues.TAG
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.accenture.pandemic.fighters.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import javax.inject.Inject

class FirebaseAuthentication @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    private lateinit var firebaseUser: FirebaseUser
    var auth = firebaseAuth

    fun setCurrentUser() {
        firebaseAuth.currentUser?.let {
            firebaseUser = it
        }
    }

    fun signOut(fragment: Fragment, activity: Activity){
        firebaseAuth.signOut()
        googleSignIn(fragment, activity).signOut()
    }

    private fun gso(context: Context) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    fun googleSignIn(fragment: Fragment, activity: Activity): GoogleSignInClient = GoogleSignIn
        .getClient(activity, gso(fragment.requireContext()))

    fun getUserName(): String =
        firebaseUser.displayName ?: ""

    fun getUserEmail(): String =
        firebaseUser.email ?: ""

    fun getUserId(): String =
        firebaseUser.uid

    fun updateUserName(name: String) {
        firebaseUser.updateProfile(userProfileChangeRequest {
            displayName = name
        }).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "User profile updated.")
            }
        }
    }
     fun updateUserEmail(email: String) {
        firebaseUser.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated.")
                    setCurrentUser()
                }
            }
    }

}