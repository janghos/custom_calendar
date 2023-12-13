package com.jangho.myapplication.calendarData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class getRestDeInfo(
    @SerialName("response")
    var response: Response?
)