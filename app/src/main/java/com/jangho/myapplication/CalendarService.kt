package com.jangho.myapplication

import com.jangho.myapplication.calendarData.getRestDeInfo
import retrofit2.http.*

interface CalendarService {
    @GET("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
    suspend fun getRest(
        @Query("serviceKey") serviceKey: String,
        @Query("solYear") solYear: String,
        @Query("_type") _type: String,
        @Query("pageNo") pageNo: String,
        @Query("numOfRows") numOfRows: String
    ): retrofit2.Response<getRestDeInfo>
}