package com.ruifen9.uicomponents

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes


fun ViewGroup.newAdapterItemView(@LayoutRes itemRes: Int): View {
    return LayoutInflater.from(this.context).inflate(itemRes, this, false)
}

fun Int.dpToPx(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Float.dpToPx(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Context.getScreenWidth(): Int {
    val resources: Resources = resources
    val dm: DisplayMetrics = resources.displayMetrics
    return dm.widthPixels
}

fun Context.getScreenHeight(): Int {
    val resources: Resources = resources
    val dm: DisplayMetrics = resources.displayMetrics
    return dm.heightPixels
}