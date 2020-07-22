package com.accenture.pandemic.fighters.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import com.accenture.pandemic.fighters.Models.ONE_MEGABYTE
import com.accenture.pandemic.fighters.Models.PROFILE_IMAGE_NAME
import com.accenture.pandemic.fighters.R
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class Storage @Inject constructor(private val firebaseStorage: FirebaseStorage) {
    lateinit var profileImageBmp: Bitmap

    fun uploadProfileImage(userId: String, profileImageView: ImageView) {

        val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val profileImageRef = firebaseStorage.reference.child("$userId/$PROFILE_IMAGE_NAME")
        val uploadTask = profileImageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Log.d(ContentValues.TAG, "ERROR IN UPLOADING PHOTO")

        }.addOnSuccessListener {
            Log.d(ContentValues.TAG, "PHOTO UPLOAD SUCCESSFULLY")
        }
    }

    fun downloadProfileImage(userId: String,context: Context) {
        val profileImageRef = this.firebaseStorage.reference.child("$userId/$PROFILE_IMAGE_NAME")

        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {

        }.addOnFailureListener {
            Log.d(ContentValues.TAG, "ERROR IN DOWNLOADING PHOTO")
            this.profileImageBmp = BitmapFactory.decodeResource(context.resources,
                R.drawable.no_profile_picture)
        }.addOnSuccessListener {
            Log.d(ContentValues.TAG, "PHOTO DOWNLOAD SUCCESSFULLY")
            this.profileImageBmp = BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    }
}