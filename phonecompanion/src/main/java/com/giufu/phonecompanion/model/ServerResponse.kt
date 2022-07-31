package com.giufu.phonecompanion.model

import java.sql.Timestamp

data class ServerResponse(
    var cod: Int,
    var latitude: Float,
    var longitude: Float,
    var timestamp: Timestamp
)
