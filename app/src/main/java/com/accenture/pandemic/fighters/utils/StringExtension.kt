package com.accenture.pandemic.fighters.utils

import android.text.Editable

fun String.toEditable(): Editable? =  Editable.Factory.getInstance().newEditable(this)?:null