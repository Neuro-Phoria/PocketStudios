package com.pocketstudios.core.common

import java.util.Locale

fun Long.toTimecode(): String {
    val hours = this / 3_600_000
    val minutes = (this % 3_600_000) / 60_000
    val seconds = (this % 60_000) / 1_000
    val millis = this % 1_000
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d.%03d", hours, minutes, seconds, millis)
    } else {
        String.format(Locale.US, "%d:%02d.%03d", minutes, seconds, millis)
    }
}

fun Long.toReadableDuration(): String {
    val totalSecs = this / 1000
    val minutes = totalSecs / 60
    val seconds = totalSecs % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

fun Float.toFormattedSpeed(): String = when {
    this < 1f -> String.format(Locale.US, "%.1fx", this)
    else -> String.format(Locale.US, "%.0fx", this)
}
