package com.video.chat.v.love.chatroulette.presentation.utils

import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

fun TextView.setDrawableStart(id: Int) {
    val drawableStart = ResourcesCompat.getDrawable(resources, id, null)
    this.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
}