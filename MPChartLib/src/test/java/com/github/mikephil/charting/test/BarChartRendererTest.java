package com.github.mikephil.charting.test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;

import java.sql.Array;
import java.util.List;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import static org.junit.Assert.assertTrue;

public class BarChartRendererTest {

    private TestBarChartRenderer mBarChartRenderer;
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

        // Use the subclass that allows testing
        mBarChartRenderer = new TestBarChartRenderer(mChartMock, mAnimatorMock, mViewPortHandlerMock);
    }

    @Test
    public void testDrawValuesNotAllowed() {
        // Spy on the subclass
        mBarChartRenderer = spy(mBarChartRenderer);

        // No need to mock isDrawingValuesAllowed() as it's overridden
        mBarChartRenderer.drawValues(mCanvasMock);

        // Verify that drawValues() actually ran
        verify(mBarChartRenderer, times(1)).drawValues(mCanvasMock);
        verify(mBarChartRenderer, times(1)).isDrawingValuesAllowed(mChartMock);
        verify(mChartMock, times(0)).getBarData();
    }

}

class TestBarChartRenderer extends BarChartRenderer {
    public TestBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return false; // Mock behavior
    }
}

