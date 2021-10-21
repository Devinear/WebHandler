package com.shining.nbottombar

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.Px
import androidx.core.content.ContextCompat.getDrawable
import com.shining.nbottombar.state.ItemSavedState

/**
 * NBottomItem.kt
 * WebHandler
 */
class BItem
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0,
    var text: String = "",
    var iconNormal: Int = 0,
    var iconSelected: Int = 0,
    var padding: Float = 0f,
    var textSize: Float = 0f,
    var textColorNormal: Int = Color.BLACK,
    var textColorSelected: Int = Color.RED,
    var badgeBackgroundColor: Int = Color.RED,
    var badgeNumber: Int = 0,
    var isShowPoint: Boolean = false,
    var iconNormalBt: Bitmap? = null,
    var iconSelectedBt: Bitmap? = null,
    var tabPaddingTop: Int = 0,
    var tabPaddingBottom: Int = 0,
    var badgeTextColor: Int = Color.WHITE) : View(context, attrs, defStyle) {

    private var alpha = 0

    private lateinit var textPaint: Paint
    private lateinit var textBound: Rect

    private lateinit var iconPaint: Paint
    private lateinit var iconAvailableRect: Rect
    private lateinit var iconDrawRect: Rect

    private lateinit var badgeBgPaint: Paint
    private lateinit var badgeTextPaint: Paint
    private lateinit var badgeCanvas: Canvas
    private lateinit var badgeRF: RectF

    companion object {
        const val TAG = "BottomItem"
    }

    init {
        if (iconNormalBt == null)
            iconNormalBt = if (iconNormal == 0) null else getDrawable(context, iconNormal)?.toBitmap()
        if (iconSelectedBt == null)
            iconSelectedBt = if (iconSelected == 0) null else getDrawable(context, iconSelected)?.toBitmap()
        if (padding == 0f)
            padding = Utils.dp2px(context, 5f)
        if (textSize == 0f)
            textSize = Utils.sp2px(context, 12f)

        initValue()
        setPadding(0,tabPaddingTop,0,tabPaddingBottom)

        val typeValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typeValue, true)
        val attribute = intArrayOf(android.R.attr.selectableItemBackground)
        val typedArray = context.theme.obtainStyledAttributes(typeValue.resourceId, attribute);
        background = typedArray.getDrawable(0)
    }

    private fun initValue() {
        iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                isFilterBitmap = true
                alpha = this@BItem.alpha
            }
        iconAvailableRect = Rect()
        iconDrawRect = Rect()
        textBound = Rect()
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                textSize = this@BItem.textSize
                color = textColorNormal
                isDither = true
                getTextBounds(text, 0, text.length, textBound)
            }

        if (TextUtils.isEmpty(text)) {
            padding = 0f
        }
        badgeBgPaint = Paint().apply {
            color = badgeBackgroundColor
            isAntiAlias = true
        }
        badgeTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = Utils.dp2px(context, textSize)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        badgeCanvas = Canvas()
        badgeRF = RectF()
    }

    fun updateTextBounds()
        = textPaint.getTextBounds(text, 0, text.length, textBound)

    override fun onSaveInstanceState(): Parcelable {
        Log.d(TAG, "onSaveInstanceState")
        return ItemSavedState(super.onSaveInstanceState()).apply {
            text = this@BItem.text
            padding = this@BItem.padding
            textSize = this@BItem.textSize
            iconNormal = this@BItem.iconNormal
            iconSelected = this@BItem.iconSelected
            isSelected = this@BItem.isSelected
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d(TAG, "onRestoreInstanceState")
        if (state == null || state !is ItemSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        restoreState(state)
    }

    private fun restoreState(state: ItemSavedState) {
        Log.d(TAG, state.text)
        this.text = state.text
        this.padding = state.padding
        this.iconNormal = state.iconNormal
        this.iconSelected = state.iconSelected
        this.textSize = state.textSize
        requestLayout()
        isSelected = state.isSelected
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure")

        val availableWidth = measuredWidth - paddingLeft - paddingRight
        val availableHeight: Int = (measuredHeight - paddingTop - paddingBottom - textBound.height() - padding).toInt()

        iconAvailableRect.set(paddingLeft, paddingTop, paddingLeft + availableWidth,
            paddingTop + availableHeight)

        val textLeft = paddingLeft + (availableWidth - textBound.width()) / 2
        val textTop = iconAvailableRect.bottom + padding.toInt()
        textBound.set(textLeft, textTop, textLeft + textBound.width(),textTop + textBound.height())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(TAG, "onLayout")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d(TAG, "onDraw")

        // draw icon
        iconAvailableRect.availableToDrawRect(
            iconNormalBt ?: throw IllegalArgumentException("you must set iconNormal"))
        iconPaint.alpha = 255 - alpha
        canvas.drawBitmap(iconNormalBt!!, null, iconAvailableRect, iconPaint)

        iconAvailableRect.availableToDrawRect(
            iconSelectedBt ?: throw IllegalArgumentException("you must set iconSelected"))
        iconPaint.alpha = alpha
        canvas.drawBitmap(iconSelectedBt!!, null, iconAvailableRect, iconPaint)

        // draw text
        if (!TextUtils.isEmpty(text)) {
            textPaint.apply {
                color = textColorNormal
                alpha = 255 - alpha
            }
            canvas.drawText(text, textBound.left.toFloat(),
                (textBound.bottom - textPaint.fontMetricsInt.bottom / 2).toFloat(), textPaint)

            textPaint.apply {
                color = textColorSelected
                alpha = alpha
            }
            canvas.drawText(text, textBound.left.toFloat(),
                (textBound.bottom - textPaint.fontMetricsInt.bottom / 2).toFloat(), textPaint)
        }

        // draw badge
        drawBadge(canvas)
    }

    private fun drawBadge(canvas: Canvas) {
        var i = measuredWidth / 14
        val j = measuredHeight / 9
        i = if (i >= j) j else i

        val left = measuredWidth / 10 * 6f
        val top = tabPaddingTop.toFloat()

        // if showPoint, don't show number
        if (isShowPoint) {
            i = if (i > 10) 10 else i
            val width = Utils.dp2px(context, i.toFloat())
            badgeRF.set(left, top, left + width, top + width)
            canvas.drawOval(badgeRF, badgeBgPaint)
            return
        }

        if (badgeNumber > 0) {
            badgeTextPaint.apply {
                val value = if (i / 1.5f == 0f) 5f else i / 1.5f
                textSize = Utils.dp2px(context, value)
                color = badgeTextColor
            }
            val number = if (badgeNumber > 99) "99+" else badgeNumber.toString()
            val width: Int
            val height = Utils.dp2px(context, i).toInt()
            val bitmap: Bitmap
            when (number.length) {
                1 -> {
                    width = Utils.dp2px(context, i).toInt()
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                }
                2 -> {
                    width = Utils.dp2px(context, (i + 5)).toInt()
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                }
                else -> {
                    width = Utils.dp2px(context, (i + 8)).toInt()
                    bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
                }
            }

            badgeRF.set(0f, 0f, width.toFloat(), height.toFloat())
            badgeCanvas.apply {
                setBitmap(bitmap)
                drawRoundRect(badgeRF, 50f, 50f, badgeBgPaint)
            }

            val fontMetrics = badgeTextPaint.fontMetrics
            val x = width / 2f
            val y = height / 2f - fontMetrics.descent + (fontMetrics.descent - fontMetrics.ascent) / 2

            badgeCanvas.drawText(number, x, y, badgeTextPaint)
            canvas.drawBitmap(bitmap, left, top, null)
            bitmap.recycle()
        }
    }

    fun showBadgePoint(show: Boolean) {
        Log.d(TAG, "showBadgePoint Show[$show]")
        if (show == isShowPoint) {
            return
        }
        isShowPoint = show
        invalidate()
    }

    fun clearBadge() {
        Log.d(TAG, "clearBadge")
        if (!isShowPoint) return
        isShowPoint = false
        badgeNumber = -1
        postInvalidate()
    }

    fun showBadgeNumber(num: Int) {
        Log.d(TAG, "showBadgeNumber Num[$num]")
        if (num <= 0) {
            throw IllegalArgumentException("num must > 0")
        }
        badgeNumber = num
        isShowPoint = false
        postInvalidate()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        alpha = if (selected) 255 else 0
        invalidate()
    }


    private fun Rect.availableToDrawRect(bitmap: Bitmap) {
        var dx = 0f
        var dy = 0f
        val wRatio = width() * 1.0f / bitmap.width
        val hRatio = height() * 1.0f / bitmap.height

        if (wRatio > hRatio)
            dx = (width() - hRatio * bitmap.width) / 2
        else
            dy = (height() - wRatio * bitmap.height) / 2

        val left = (left.toFloat() + dx + 0.5f).toInt()
        val top = (top.toFloat() + dy + 0.5f).toInt()
        val right = (right - dx + 0.5f).toInt()
        val bottom = (bottom - dy + 0.5f).toInt()
        set(left, top, right, bottom)
    }

    private fun Drawable.toBitmap(
        @Px width: Int = intrinsicWidth,
        @Px height: Int = intrinsicHeight,
        config: Bitmap.Config? = null)
    : Bitmap {

        if (this is BitmapDrawable) {
            if (config == null || bitmap.config == config) {

                // size check
                if (width == intrinsicWidth && height == intrinsicHeight)
                    return bitmap

                return Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
        }

        val oldLeft = bounds.left
        val oldTop = bounds.top
        val oldRight = bounds.right
        val oldBottom = bounds.bottom

        val bitmap = Bitmap.createBitmap(width, height, config ?: Bitmap.Config.ARGB_8888)
        setBounds(0, 0, width, height)
        draw(Canvas(bitmap))

        setBounds(oldLeft, oldTop, oldRight, oldBottom)
        return bitmap
    }
}