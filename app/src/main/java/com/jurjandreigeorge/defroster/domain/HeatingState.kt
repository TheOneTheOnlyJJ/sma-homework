package com.jurjandreigeorge.defroster.domain

enum class HeatingState {
    NOT_HEATING,
    STARTING_HEATING,
    HEATING,
    STOPPING_HEATING
}

const val dateTimePattern = "d MMM yy hh:mm:ss a"
