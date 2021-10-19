package com.shining.nbottombar

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.shining.nbottombar.SavedState.BarSavedState

/**
 * NBottomBar.kt
 * WebHandler
 */
class NBottomBar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    private val items = ArrayList<NBottomItem>()
    private var selectedListener: (prePosition: Int, position: Int) -> Unit = { _, _ -> }

    //    private var reselectedListener : (position: Int) -> Unit = {}
    private var pressedListener: (position: Int) -> Unit = {}

    private var position = -1
    private var prePosition = -1
    private var isInit = false

    companion object {
        const val TAG = "NBottomBar"
    }

    init {
        orientation = HORIZONTAL
    }

    fun setOnSelectedListener(onSelectedListener: (prePosition: Int, position: Int) -> Unit) {
        this.selectedListener = onSelectedListener
    }

//    fun setOnReSelectedListener(onReSelectedListener: (position: Int) -> Unit) {
//        this.reselectedListener = onReSelectedListener
//    }

    fun setOnPressedListener(onPressedListener: (position: Int) -> Unit) {
        this.pressedListener = onPressedListener
    }

    override fun onSaveInstanceState(): Parcelable? {
        return BarSavedState(super.onSaveInstanceState() ?: return null).apply {
            this.preIndex = prePosition
            this.curIndex = position
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state !is BarSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        restoreState(state)
    }

    private fun restoreState(barSavedState: BarSavedState) {
        prePosition = barSavedState.preIndex
        position = barSavedState.curIndex
        post {
            onSelected(prePosition, position, selectedListener)
        }
    }

    private fun onSelected(
        preIndex: Int,
        curIndex: Int,
        onSelectedListener: (prePosition: Int, position: Int) -> Unit
    ) {
        for (i in 0 until childCount) {
            getChildAt(i).isSelected = i == curIndex
        }
        onSelectedListener(preIndex, curIndex)
    }

    private fun applyView(view: NBottomItem) =
        view.apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                weight = 1f
            }
            if (id == View.NO_ID) {
                id = View.generateViewId()
            }
        }

    fun addItem(vararg item: NBottomItem) {
        items.addAll(item)

        for (i in items.indices) {
            val child = applyView(items[i])
            items[i]

            addViewInLayout(child, -1, child.layoutParams, true)
            child.setOnClickListener { selectItem(i) }
            child.setOnLongClickListener { pressItem(i) }
        }

        isInit = true
        position = 0

        selectedItem()?.isSelected = true
        requestLayout()
    }

    private fun pressItem(index: Int) : Boolean {

        return true
    }

    fun selectItem(index: Int) {
        if (childCount == 0) return

        onSelected(preIndex = position, curIndex = index, selectedListener)
        prePosition = position
        position = index
    }

    fun selectState(index: Int) {
        post {
            prePosition = position
            position = index

            for (i in 0 until childCount) {
                getItem(i)?.isSelected = i == index
            }
        }
    }

    fun updateItem(index: Int, item: NBottomItem) {
        val original = getChildAt(index) ?: return
        (original as NBottomItem).apply {

        }
        original.requestLayout()
    }

    fun selectedPosition() = position

    fun previousPosition() = prePosition

    fun selectedItem() : NBottomItem?
        = if(childCount >= position) null
        else getChildAt(position) as NBottomItem

    fun getItem(position: Int) : NBottomItem?
        = if(childCount >= position) null
        else getChildAt(position) as NBottomItem


}