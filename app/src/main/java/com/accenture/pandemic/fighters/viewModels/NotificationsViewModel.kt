package com.accenture.pandemic.fighters.viewModels


import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.accenture.pandemic.fighters.MainActivity
import com.accenture.pandemic.fighters.Models.ButtonName
import com.accenture.pandemic.fighters.Models.Notifications
import com.accenture.pandemic.fighters.Models.ReportButtonModel
import com.accenture.pandemic.fighters.R
import com.accenture.pandemic.fighters.repository.local.*

import javax.inject.Inject

class NotificationsViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel(){

    fun initNotifyList(context: Context) : List<ReportButtonModel>{
        return mutableListOf(ReportButtonModel(context.getString(R.string.All), ButtonName.ALL),
            ReportButtonModel(context.getString(R.string.Confirmed), ButtonName.CONFIRMED),
            ReportButtonModel(context.getString(R.string.Self_reported), ButtonName.SELFREPORTED)
        )
    }

    fun initAddedAroundList(context: Context) : List<ReportButtonModel>{
        return mutableListOf(ReportButtonModel(context.getString(R.string.Distance), ButtonName.DISTANCE),
            ReportButtonModel(context.getString(R.string.my_city), ButtonName.MYCITY),
            ReportButtonModel(context.getString(R.string.my_country), ButtonName.MYCOUNTRY)
        )
    }

    fun updateNotificationTopic(activity: MainActivity){
        activity.updateTopicNotifications()
    }

    fun updateNotifications(){
        val currentState = sharedPreferences.getNotifications()
        val newState = if (currentState == Notifications.OFF) Notifications.ON
        else    Notifications.OFF
        sharedPreferences.setNotifications(newState)
    }
    fun getNotificationState() : Notifications = sharedPreferences.getNotifications()

    fun getNotificationType() : ButtonName = sharedPreferences.getNotificationsType()

    fun updateNotificationsType(name: ButtonName){
        sharedPreferences.setNotificationsType(name)
    }

    fun getNotificationDistance() : ButtonName = sharedPreferences.getNotificationsDistance()

    fun updateNotificationsDistance(name: ButtonName){
        sharedPreferences.setNotificationsDistance(name)
    }

    fun getDistance() : Int = sharedPreferences.getDistance()

    fun updateDistance(distance: Int){
        sharedPreferences.setDistance(distance)
    }

}
