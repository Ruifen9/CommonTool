package com.ruifen9.uicomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.newAdapterView(@LayoutRes itemRes: Int): View {
    return LayoutInflater.from(this.context).inflate(itemRes, this, false)
}