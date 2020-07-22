package com.accenture.pandemic.fighters.repository.local

import android.content.SharedPreferences
import com.accenture.pandemic.fighters.Models.ButtonName
import com.accenture.pandemic.fighters.Models.FilterButton
import com.accenture.pandemic.fighters.Models.Notifications

fun SharedPreferences.getNotifications(): Notifications =
    Notifications.valueOf(
        getString(Notifications::class.java.simpleName, Notifications.OFF.name)
            ?: Notifications.OFF.name
    )

fun SharedPreferences.setNotifications(notifications: Notifications) =
    edit()
        .putString(Notifications::class.java.simpleName, notifications.name)
        .apply()

fun SharedPreferences.getNotificationsType(): ButtonName =
    ButtonName.valueOf(
        getString("NotificationType", ButtonName.ALL.name)
            ?: ButtonName.ALL.name
    )

fun SharedPreferences.setNotificationsType(notifications: ButtonName) =
    edit()
        .putString("NotificationType", notifications.name)
        .apply()

fun SharedPreferences.getNotificationsDistance(): ButtonName =
    ButtonName.valueOf(
        getString("NotificationDistance", ButtonName.NONE.name)
            ?: ButtonName.NONE.name
    )

fun SharedPreferences.setNotificationsDistance(notifications: ButtonName) =
    edit()
        .putString("NotificationDistance", notifications.name)
        .apply()

fun SharedPreferences.getDistance(): Int = getInt("Distance", 150)

fun SharedPreferences.setDistance(distance: Int) =
    edit()
        .putInt("Distance", distance)
        .apply()

fun SharedPreferences.getTypeFilter(): FilterButton =
    FilterButton.valueOf(
        getString("typeFilter", FilterButton.ALL.name)
            ?: ButtonName.ALL.name
    )

fun SharedPreferences.setTypeFilter(type: FilterButton) =
    edit()
        .putString("typeFilter", type.name)
        .apply()

fun SharedPreferences.getTimeFilterButton(): FilterButton =
    FilterButton.valueOf(
        getString("timeFilterButton", FilterButton.NONE.name)
            ?: ButtonName.NONE.name
    )

fun SharedPreferences.setTimeFilterButton(timeFilterButton: FilterButton) =
    edit()
        .putString("timeFilterButton", timeFilterButton.name)
        .apply()

fun SharedPreferences.getTimeFilter(): String = getString("timeFilter", "...")?:"..."

fun SharedPreferences.setTimeFilter(dateFrom: String?) =
    edit()
        .putString("timeFilter", dateFrom)
        .apply()

