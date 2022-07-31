package com.giufu.musicplayer.model

data class Weather(
    val currentTemperature: String,
    val minimumTemperature: String,
    val maximumTemperature: String,
    val humidity: String,//percent
    val timestamp: Long,//milliseconds
    val location: String,//city name
    val description: String
)
