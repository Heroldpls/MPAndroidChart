package com.github.mikephil.charting.test;

import android.graphics.Canvas;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import java.util.List;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import static org.junit.Assert.assertTrue;

public class BarChartRendererTest {

    /**
     * Requirements (All are untested):
     * 1. The method doesn't draw/does draw something.
     * 2. Handling no data*/
    private TestBarChartRendererNotAllowed mBarChartRendererNotAllowed;
    private TestBarChartRendererAllowed mBarChartRendererAllowed;
    private BarDataProvider mChartMock;
    private BarData mBarDataMock;
    private IBarDataSet mDataSetMock;
    private ChartAnimator mAnimatorMock;
    private ViewPortHandler mViewPortHandlerMock;
    private ChartData mChartDataMock;
    private Canvas mCanvasMock;

    @Before
    public void setUp() {
        mChartMock = mock(BarDataProvider.class);
        mAnimatorMock = mock(ChartAnimator.class);
        mViewPortHandlerMock = mock(ViewPortHandler.class);
        mCanvasMock = mock(Canvas.class);
        mBarDataMock = mock(BarData.class);
        mDataSetMock = mock(IBarDataSet.class);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(mDataSetMock);
        when(mBarDataMock.getDataSets()).thenReturn(dataSets);
        when(mChartMock.getBarData()).thenReturn(mBarDataMock);

        // Use Mock classes
        mBarChartRendererNotAllowed = new TestBarChartRendererNotAllowed(mChartMock, mAnimatorMock, mViewPortHandlerMock);
        mBarChartRendererNotAllowed = spy(mBarChartRendererNotAllowed);

        mBarChartRendererAllowed = new TestBarChartRendererAllowed(mChartMock, mAnimatorMock, mViewPortHandlerMock);
        mBarChartRendererAllowed = spy(mBarChartRendererAllowed);
    }

    @Test
    public void testDrawValuesNotAllowed() {
        // Spy on the subclass


        // No need to mock isDrawingValuesAllowed() as it's overridden
        mBarChartRendererNotAllowed.drawValues(mCanvasMock);

        // Verify that drawValues() actually ran
        verify(mBarChartRendererNotAllowed, times(1)).drawValues(mCanvasMock);
        verify(mBarChartRendererNotAllowed, times(1)).isDrawingValuesAllowed(mChartMock);
        verify(mChartMock, times(0)).getBarData();
    }

    @Test
    public void testDrawValuesNoData() {
        when(mBarDataMock.getDataSetCount()).thenReturn(0);
        mBarChartRendererAllowed.drawValues(mCanvasMock);

        verify(mBarChartRendererAllowed, times(1)).drawValues(mCanvasMock);
        verify(mBarDataMock, times(1)).getDataSetCount();
        verify(mBarChartRendererAllowed, times(0)).shouldDrawValues(mDataSetMock);

    }

}

class TestBarChartRendererNotAllowed extends BarChartRenderer {
    public TestBarChartRendererNotAllowed(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return false; // Mock behavior
    }
}

class TestBarChartRendererAllowed extends BarChartRenderer {
    public TestBarChartRendererAllowed(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return true; // Mock behavior
    }
    @Override
    public boolean shouldDrawValues(IDataSet set){
        return true; // Mock behavior
    }
}

