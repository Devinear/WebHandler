package com.shining.nbottombar

import android.content.Context
import android.util.TypedValue

/**
 * Utils.kt
 * WebHandler
 */
object Utils {

    fun dp2px(context: Context, value: Int) =
        dp2px(context, value.toFloat())

    fun sp2px(context: Context, value: Int) =
        sp2px(context, value.toFloat())

    fun dp2px(context: Context, value: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics)

    fun sp2px(context: Context, value: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.resources.displayMetrics)

}