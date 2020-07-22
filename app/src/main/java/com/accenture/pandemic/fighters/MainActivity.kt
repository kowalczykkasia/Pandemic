package com.accenture.pandemic.fighters


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.core.content.ContextCompat
import com.accenture.pandemic.fighters.Models.Notifications
import com.accenture.pandemic.fighters.repository.local.getNotifications
import com.accenture.pandemic.fighters.repository.local.getNotificationsType
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun block() : PopupWindow{
        val view = layoutInflater.inflate(R.layout.waiting_background, null)
        val popup = PopupWindow(view, container.width , container.height)
        popup.contentView = view
        popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup.showAtLocation(view, Gravity.CENTER, 0, 0)
        return popup
    }

    fun updateTopicNotifications(){
        val sharedPreferences = this.getSharedPreferences("pandemic_app", Context.MODE_PRIVATE)

        if(sharedPreferences.getNotifications() == Notifications.ON){
        when {
            sharedPreferences.getNotificationsType().name.toLowerCase() == "all" -> {
                sub("selfreported")
                sub("confirmed")
            }
            sharedPreferences.getNotificationsType().name.toLowerCase() == "confirmed" -> {
                unsub("selfreported")
                sub("confirmed")
            }
            sharedPreferences.getNotificationsType().name.toLowerCase() == "selfreported" -> {
                sub("selfreported")
                unsub("confirmed")
            }
            else -> {
                unsub("selfreported")
                unsub("confirmed")
            }
        }
        }else{
            unsub("selfreported")
            unsub("confirmed")
        }


    }

    private fun unsub(name : String){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(name)
    }
    private fun sub(name : String){
        FirebaseMessaging.getInstance().subscribeToTopic(name)
    }

    fun checkPermission() : Boolean{
        if(this.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }
}

