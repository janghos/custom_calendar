package com.jangho.myapplication.calendarData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Header(
    @SerialName("resultCode")
    var resultCode: String?,
    @SerialName("resultMsg")
    var resultMsg: String?
)