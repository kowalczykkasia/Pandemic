package com.accenture.pandemic.fighters.utils

import android.content.Context
import com.accenture.pandemic.fighters.Models.Values
import com.accenture.pandemic.fighters.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

const val DAY_IN_SECONDS = 86400
const val TWO_WEEKS_IN_SECONDS = 1209600
const val SHORTNESS_OF_BREATH = "Shortness of Breath"
const val COUGH = "Cough"
const val FEVER = "Fever"

fun getTimeStampFromDate(dateString: String): Int {
    val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("d.M.yyyy"))
    return  date.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond.toInt()
}


fun getDayMonthYearHourMinutes(timeStamp: Int): String =
    SimpleDateFormat("d.MM.YYYY HH:mm ").format(Date(timeStamp.toLong() * 1000))

fun setSymptomsText(symptoms: Values, context: Context): String {
    val sb = StringBuilder()
    symptoms.values?.forEach {
        when(it.stringValue){
           SHORTNESS_OF_BREATH -> sb.append(", ${context.getString(R.string.breathlessness)}")
           COUGH -> sb.append(", ${context.getString(R.string.cough)}")
           FEVER -> sb.append(", ${context.getString(R.string.fever)}")
        }
    }
    sb.let {
        return if (it.isEmpty())
            "${context.getString(R.string.symptoms)}: ${context.getString(R.string.no)} $it"
        else
            "${context.getString(R.string.symptoms)}: ${context.getString(R.string.yes)} $it"
    }
}

fun setCoronaTestText(test: String, context: Context): String =
    when (test) {
        "${context.getString(R.string.tested)}" ->
            "${context.getString(R.string.result)} ${context.getString(R.string.yes)}"
        "${context.getString(R.string.pending)}" ->
            "${context.getString(R.string.result)} ${context.getString(R.string.pending)}"
        else -> "${context.getString(R.string.result)} ${context.getString(R.string.dontknow)}"

    }
