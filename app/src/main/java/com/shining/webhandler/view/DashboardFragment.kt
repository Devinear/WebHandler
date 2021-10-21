package com.shining.webhandler.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.shining.webhandler.databinding.LayoutDashboardBinding
import com.github.mikephil.charting.components.YAxis

import com.github.mikephil.charting.components.XAxis

import com.github.mikephil.charting.data.LineData

import com.github.mikephil.charting.data.LineDataSet
import com.shining.webhandler.R
import com.shining.webhandler.databinding.ViewMarkerBinding

/**
 * DashboardFragment.kt
 * WebHandler
 */
class DashboardFragment : BaseFragment() {

    private lateinit var binding : LayoutDashboardBinding
    private lateinit var bindingMarker : ViewMarkerBinding
    private lateinit var viewModel: DashboardViewModel

    companion object {
        private const val TAG = "[DE][FR] Dashboard"
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DashboardFragment() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutDashboardBinding.inflate(layoutInflater, container, false)
        bindingMarker = ViewMarkerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun initUi() {
        super.initUi()
        Log.d(TAG, "initUi")

        val entries = ArrayList<Entry>()
        for (i in 0 .. 9) {
            val value = (Math.random() * 10).toFloat()
            entries.add(Entry(i.toFloat(), value))
        }

        val lineDataSet = LineDataSet(entries, "속성명1")
        lineDataSet.lineWidth = 2f
        lineDataSet.circleRadius = 6f
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"))
        lineDataSet.circleHoleColor = Color.BLUE
        lineDataSet.color = Color.parseColor("#FFA1B4DC")
        lineDataSet.setDrawCircleHole(true)
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.setDrawValues(false)

        val lineData = LineData(lineDataSet)
        binding.chart.data = lineData

        val xAxis: XAxis = binding.chart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis: YAxis = binding.chart.getAxisLeft()
        yLAxis.textColor = Color.BLACK

        val yRAxis: YAxis = binding.chart.getAxisRight()
        yRAxis.setDrawLabels(false)
        yRAxis.setDrawAxisLine(false)
        yRAxis.setDrawGridLines(false)

        val description = Description()
        description.text = "Description"

        val marker = ChartMarkerView(requireContext(), R.layout.view_marker, bindingMarker)
        marker.chartView = binding.chart
        binding.chart.marker = marker

        binding.chart.invalidate()



//        LineDataSet lineDataSet = new LineDataSet(values, getString(R.string.weight)); //LineDataSet 선언
//        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.purple)); //LineChart에서 Line Color 설정
//        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.purple)); // LineChart에서 Line Circle Color 설정
//        lineDataSet.setCircleHoleColor(ContextCompat.getColor(getContext(), R.color.purple)); // LineChart에서 Line Hole Circle Color 설정
//
//        LineData lineData = new LineData(); //LineDataSet을 담는 그릇 여러개의 라인 데이터가 들어갈 수 있습니다.
//        lineData.addDataSet(lineDataSet);
//
//        lineData.setValueTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); //라인 데이터의 텍스트 컬러 설정
//        lineData.setValueTextSize(9);
//
//        XAxis xAxis = lineChart.getXAxis(); // x 축 설정
//        xAxis.setPosition(XAxis.XAxisPosition.TOP); //x 축 표시에 대한 위치 설정
//        xAxis.setValueFormatter(new ChartXValueFormatter()); //X축의 데이터를 제 가공함. new ChartXValueFormatter은 Custom한 소스
//        xAxis.setLabelCount(5, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
//        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); // X축 텍스트컬러설정
//        xAxis.setGridColor(ContextCompat.getColor(getContext(), R.color.textColor)); // X축 줄의 컬러 설정
//
//        YAxis yAxisLeft = lineChart.getAxisLeft(); //Y축의 왼쪽면 설정
//        yAxisLeft.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); //Y축 텍스트 컬러 설정
//        yAxisLeft.setGridColor(ContextCompat.getColor(getContext(), R.color.textColor)); // Y축 줄의 컬러 설정
//
//        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
//        yAxisRight.setDrawLabels(false);
//        yAxisRight.setDrawAxisLine(false);
//        yAxisRight.setDrawGridLines(false);
//        //y축의 활성화를 제거함
//
//        lineChart.setVisibleXRangeMinimum(60 * 60 * 24 * 1000 * 5); //라인차트에서 최대로 보여질 X축의 데이터 설정
//        lineChart.setDescription(null); //차트에서 Description 설정 저는 따로 안했습니다.
//
//        Legend legend = lineChart.getLegend(); //레전드 설정 (차트 밑에 색과 라벨을 나타내는 설정)
//        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);//하단 왼쪽에 설정
//        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); // 레전드 컬러 설정
//


//        chart.setBackgroundColor(Color.RED); // 그래프 배경 색 설정
//        set1.setColor(Color.BLACK); // 차트의 선 색 설정
//        set1.setCircleColor(Color.BLACK); // 차트의 points 점 색 설정
//
//        set1.setDrawFilled(true); // 차트 아래 fill(채우기) 설정
//        set1.setFillColor(Color.BLACK); // 차트 아래 채우기 색 설정


//        binding.chart.setDoubleTapToZoomEnabled(false)
//        binding.chart.setDrawGridBackground(false)
//        binding.chart.setDescription(description)
//        binding.chart.animateY(2000, Easing.EaseInCubic)
//        binding.chart.invalidate()
    }

    private fun entryIntValue(x: Int, y: Int) : Entry = Entry(x.toFloat(), y.toFloat())
}