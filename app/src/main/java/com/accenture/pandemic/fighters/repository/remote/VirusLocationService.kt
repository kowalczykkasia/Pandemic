package com.accenture.pandemic.fighters.repository.remote

import com.accenture.pandemic.fighters.Models.*
import retrofit2.http.*

interface VirusLocationService {

    @GET("https://firestore.googleapis.com/v1/projects/pandemicfighters/databases/(default)/documents/VirusLocations")
    suspend fun getAllLocations(
        @Query("pageSize") pageSize: Long
    ): VirusLocationListResponse

    @POST("https://firestore.googleapis.com/v1/projects/pandemicfighters/databases/(default)/documents/VirusLocations")
    suspend fun addNewVirusLocation(
        @Body virusLocationBody: VirusLocationBody
    )

}
