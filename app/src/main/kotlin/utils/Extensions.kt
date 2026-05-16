package com.pocketstudios.utils

import android.content.Context

fun Context.getScreenDensity(): Float {
    return resources.displayMetrics.density
}