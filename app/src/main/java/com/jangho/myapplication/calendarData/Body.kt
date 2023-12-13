package com.jangho.myapplication.calendarData


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Body(
    @SerialName("items")
    var items: Items?,
    @SerialName("numOfRows")
    var numOfRows: Int?,
    @SerialName("pageNo")
    var pageNo: Int?,
    @SerialName("totalCount")
    var totalCount: Int?
)