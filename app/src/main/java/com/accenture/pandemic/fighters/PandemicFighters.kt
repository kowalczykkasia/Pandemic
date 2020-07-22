package com.accenture.pandemic.fighters

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.accenture.pandemic.fighters.di.components.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class PandemicFighters : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication>
    = DaggerAppComponent.builder().create(this)
}