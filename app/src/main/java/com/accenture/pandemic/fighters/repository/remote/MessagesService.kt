package com.accenture.pandemic.fighters.repository.remote

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.accenture.pandemic.fighters.Models.ButtonName
import com.accenture.pandemic.fighters.Models.WARSAW_LATITUDE
import com.accenture.pandemic.fighters.Models.WARSAW_LONGITUDE
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.repository.local.getDistance
import com.accenture.pandemic.fighters.repository.local.getNotificationsDistance
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.math.abs

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MessagesService : FirebaseMessagingService() {

    private var currLat : Double = WARSAW_LATITUDE
    private var currLon : Double = WARSAW_LONGITUDE
    lateinit var channel :NotificationChannel
    private val CHANNEL_ID = "channel_id"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun getLastKnownLocation() : Boolean{
        if(this.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        == PackageManager.PERMISSION_GRANTED
    ) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (loc != null) {
                currLat = loc.latitude
                currLon = loc.longitude
            }
            return true
        }
        return false
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if(getLastKnownLocation()) {
            val sharedPreferences = this.getSharedPreferences("pandemic_app", Context.MODE_PRIVATE)

            if (remoteMessage.data.isNotEmpty()) {
                Log.d(ContentValues.TAG, "${currLat} ${currLon}")

                Log.d(ContentValues.TAG, "Message data payload: " + remoteMessage.data)
                val lat = remoteMessage.data.get("latitude").orEmpty()
                val lon = remoteMessage.data.get("longitude").orEmpty()

                val currentAddr = Geocoder(this).getFromLocation(currLat, currLon, 1)[0]
                val virusAddr = Geocoder(this).getFromLocation(lat.toDouble(), lon.toDouble(), 1)[0]

                var currentName = ""
                var virusName = ""
                virusAddr.countryName?.let { virusName = it }

                when (sharedPreferences.getNotificationsDistance()) {
                    ButtonName.MYCOUNTRY -> {
                        currentName = currentAddr.countryName
                    }
                    ButtonName.MYCITY -> {
                        currentName = currentAddr.locality
                        virusName = virusAddr.locality
                    }
                    ButtonName.DISTANCE -> {
                        virusName = virusAddr.countryName
                        val distance: Double = sharedPreferences.getDistance() / 150.0

                        if (abs(lat.toDouble() - currLat) <= distance && abs(lon.toDouble() - currLon) <= distance)
                            currentName = virusName
                    }
                    else -> {
                        currentName = "none"
                    }
                }

                if (currentName == virusName || currentName == "none") {
                    sendNotification(virusName)
                    Log.d(ContentValues.TAG, "new virus case")
                }
            }
        }
    }

    private fun sendNotification(name: String){
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.alert))
            .setContentText(getString(R.string.new_report) + ": $name")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name ="channel_name"
            val descriptionText ="channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            channel =  NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
        }
    }


}