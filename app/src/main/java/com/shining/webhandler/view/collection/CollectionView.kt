package com.shining.webhandler.view.collection

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

/**
 * CollectionView.kt
 * WebHandler
 */
class CollectionView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
: RecyclerView(context, attrs, defStyle) {

    companion object {
        const val TAG = "[DE][VI] Collection"
    }

    private var isCheckMode = false

    fun showCheckMode(show: Boolean = true) : Boolean {
        Log.d(TAG, "showCheckMode show[$show] ChildCount[$childCount]")
        if(childCount == 0 || isCheckMode == show)
            return false

        isCheckMode = show

        try {
            for(i in 0 until childCount) {
                (getChildAt(i) as CardView).apply {
                    getChildAt(1).visibility = if(show) View.VISIBLE else View.INVISIBLE
                    invalidate()
                }
            }
        }
        catch (e: Exception) {
            return false
        }
        return true
    }

    fun visibleItemsChecked(check: Boolean) {
        if (childCount == 0 || !isCheckMode) return

        try {
            for(i in 0 until childCount) {
                (getChildAt(i) as CardView).apply {
                    (getChildAt(1) as CheckBox).isChecked = check
                    invalidate()
                }
            }
        }
        catch (e: Exception) {
        }
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        (child as CardView).getChildAt(1).visibility = if(isCheckMode) View.VISIBLE else View.INVISIBLE
        return super.drawChild(canvas, child, drawingTime)
    }
}