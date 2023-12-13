package com.jangho.myapplication.calendarData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("dateKind")
    var dateKind: String?,
    @SerialName("dateName")
    var dateName: String?,
    @SerialName("isHoliday")
    var isHoliday: String?,
    @SerialName("locdate")
    var locdate: Int?,
    @SerialName("seq")
    var seq: Int?
)