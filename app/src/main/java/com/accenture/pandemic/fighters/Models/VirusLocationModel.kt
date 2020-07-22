package com.accenture.pandemic.fighters.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class VirusLocationListResponse(
    @SerializedName("documents") val documents: List<VirusLocationResponse>
)

data class VirusLocationResponse(

    @SerializedName("name") val name: String,
    @SerializedName("fields") val fields: Fields,
    @SerializedName("createTime") val createTime: String,
    @SerializedName("updateTime") val updateTime: String
)

data class VirusLocationBody(
    @SerializedName("fields") val fields: FieldsToWrite
)

data class FieldsToWrite(
    @SerializedName("description") val description: Description,
    @SerializedName("latitude") val latitude: Latitude,
    @SerializedName("tested") val tested: Tested,
    @SerializedName("symptoms") val symptoms: SymptomsList,
    @SerializedName("longitude") val longitude: Longitude,
    @SerializedName("timeStamp") val timeStamp: TimeStamp,
    @SerializedName("selfReported") val selfReported: SelfReported
)

@Parcelize
data class SymptomsList(
    @SerializedName("arrayValue") val arrayValue: Values
) : Parcelable

@Parcelize
data class Values(
    @SerializedName("values") val values: List<Description>?
) : Parcelable

@Parcelize
data class Fields(
    @SerializedName("description") val description: Description,
    @SerializedName("latitude") val latitude: Latitude,
    @SerializedName("tested") val tested: Tested,
    @SerializedName("symptoms") val symptoms: SymptomsList,
    @SerializedName("longitude") val longitude: Longitude,
    @SerializedName("timeStamp") val timeStamp: TimeStamp,
    @SerializedName("selfReported") val selfReported: SelfReported
) : Parcelable

@Parcelize
data class Tested(
    @SerializedName("stringValue") val stringValue: String
) : Parcelable

@Parcelize
data class TimeStamp(
    @SerializedName("stringValue") val stringValue: String
) : Parcelable

@Parcelize
data class Description(
    @SerializedName("stringValue") val stringValue: String
) : Parcelable

@Parcelize
data class SelfReported(
    @SerializedName("booleanValue") val booleanValue: Boolean
) : Parcelable

@Parcelize
data class Longitude(
    @SerializedName("doubleValue") val doubleValue: Double
) : Parcelable

@Parcelize
data class Latitude(
    @SerializedName("doubleValue") val doubleValue: Double
) : Parcelable

enum class Notifications {
    ON,
    OFF
}

@Parcelize
data class Filters(
    val type: String,
    val timeStampFrom: Int
):Parcelable