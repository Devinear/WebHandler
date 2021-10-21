package com.shining.webhandler.view

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.shining.webhandler.R
import com.shining.webhandler.databinding.ViewMarkerBinding

/**
 * DashboardMarkerView.kt
 * WebHandler
 */
class ChartMarkerView(context: Context, layoutRes: Int, private val binding: ViewMarkerBinding) : MarkerView(context, layoutRes) {

    private var tv : TextView = findViewById(R.id.tv_content)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e ?: return

        if (e is CandleEntry) {
            val entry = e as CandleEntry
            tv.text = Utils.formatNumber(entry.high, 0, true)
//            binding.tvContent.text = Utils.formatNumber(entry.high, 2, true)
        }
        else {
            tv.text = Utils.formatNumber(e.y, 0, true)
//            binding.tvContent.text = Utils.formatNumber(e.y, 2, true)
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}