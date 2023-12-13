package com.jangho.myapplication.calendarData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName("body")
    var body: Body?,
    @SerialName("header")
    var header: Header?
)