package com.shining.nbottombar

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.shining.nbottombar.SavedState.BarSavedState

/**
 * NBottomBar.kt
 * WebHandler
 */
class BBar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    private var selectedListener: (prePosition: Int, position: Int) -> Unit = { _, _ -> }
    private var reselectedListener : (position: Int) -> Unit = {}
    private var pressedListener: (position: Int) -> Unit = {}

    private var position = -1
    private var prePosition = -1

    companion object {
        const val TAG = "NBottomBar"
    }

    init {
        orientation = HORIZONTAL
    }

    fun setOnSelectedListener(onSelectedListener: (prePosition: Int, position: Int) -> Unit) {
        this.selectedListener = onSelectedListener
    }

    fun setOnReSelectedListener(onReSelectedListener: (position: Int) -> Unit) {
        this.reselectedListener = onReSelectedListener
    }

    fun setOnPressedListener(onPressedListener: (position: Int) -> Unit) {
        this.pressedListener = onPressedListener
    }

    override fun onSaveInstanceState(): Parcelable {
        Log.d(TAG, "onSaveInstanceState")
        return BarSavedState(super.onSaveInstanceState()).apply {
            prePosition = this@BBar.prePosition
            position = this@BBar.position
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d(TAG, "onRestoreInstanceState")
        if (state == null || state !is BarSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        restoreState(state)
    }

    private fun restoreState(barSavedState: BarSavedState) {
        prePosition = barSavedState.prePosition
        position = barSavedState.position
        post {
            onSelected(prePosition, position, selectedListener)
        }
    }

    fun addItem(vararg items: BItem) {
        Log.d(TAG, "addItem Count[${items.size}]")
        for (i in items.indices) {
            val itemView = items[i].apply {
                layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
                    .apply { weight = 1f }

                if (id == View.NO_ID)
                    id = View.generateViewId()
            }
            addViewInLayout(itemView, -1, itemView.layoutParams, true)
            itemView.setOnClickListener { selectItem(i) }
            itemView.setOnLongClickListener { pressItem(i) }
        }

        position = 0

        getSelectedItem()?.isSelected = true
        requestLayout()
    }

    private fun onSelected(
        prePosition: Int,
        position: Int,
        onSelectedListener: (prePosition: Int, position: Int) -> Unit
    ) {
        for (i in 0 until childCount) {
            getChildAt(i).isSelected = i == position
        }
        onSelectedListener(prePosition, position)
    }

    fun selectItem(position: Int) {
        Log.d(TAG, "selectItem Position[$position]")
        if (childCount == 0) return

        if (this.position == position && prePosition != -1) {
            reselectedListener(position)
            return
        }

        onSelected(prePosition = this.position, position = position, selectedListener)
        prePosition = this.position
        this.position = position
    }

    fun selectState(position: Int) {
        Log.d(TAG, "selectState Position[$position]")
        post {
            prePosition = this.position
            this.position = position

            for (i in 0 until childCount) {
                getItem(i)?.isSelected = i == position
            }
        }
    }

    private fun pressItem(position: Int) : Boolean {
        Log.d(TAG, "pressItem Position[$position]")
        pressedListener(position)
        return true
    }

    fun updateItem(position: Int, item: BItem) {
        Log.d(TAG, "updateItem Position[$position]")
        val original = getChildAt(position) ?: return
        (original as BItem).apply {
            iconNormal = item.iconNormal
            iconSelected = item.iconSelected
            iconNormalBt = item.iconNormalBt
            iconSelectedBt = item.iconSelectedBt
            text = item.text
            padding = item.padding
            textSize = item.textSize
            textColorNormal = item.textColorNormal
            textColorSelected = item.textColorSelected
            badgeBackgroundColor = item.badgeBackgroundColor
            badgeNumber = item.badgeNumber
            isShowPoint = item.isShowPoint
            updateTextBounds()
        }
        original.requestLayout()
    }

    fun getSelectedPosition() = position

    fun getPreviousPosition() = prePosition

    fun getSelectedItem() : BItem?
        = if(childCount >= position) null
        else getChildAt(position) as BItem

    fun getItem(position: Int) : BItem?
        = if(childCount >= position) null
        else getChildAt(position) as BItem

}