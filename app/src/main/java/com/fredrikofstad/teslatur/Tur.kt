package com.fredrikofstad.teslatur

import com.google.firebase.Timestamp

data class Tur(
    val displayName: String = "",
    val timestamp: Timestamp = Timestamp.now(),
)