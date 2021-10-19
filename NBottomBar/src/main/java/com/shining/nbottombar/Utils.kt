package com.shining.nbottombar

import android.content.Context
import android.util.TypedValue

/**
 * Utils.kt
 * WebHandler
 *
 * Created by Chahyung-Lee on 2021/10/19.
 * Copyright © 2021 NHN COMMERCE Corp. All rights reserved.
 */
object Utils {

    fun dp2px(context: Context, value: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics)

    fun sp2px(context: Context, value: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.resources.displayMetrics)

}