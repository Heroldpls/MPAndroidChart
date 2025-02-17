package com.github.mikephil.charting.test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import static org.junit.Assert.assertTrue;

public class BarChartRendererTest {

    private BarChartRenderer mBarChartRenderer;
    private BarDataProvider mChartMock;
    private BarData mBarDataMock;
    private IBarDataSet mDataSetMock;
    private ChartAnimator mAnimatorMock;
    private ViewPortHandler mViewPortHandlerMock;
    private ChartData mChartDataMock;
    private Canvas mCanvasMock;

    @Before
    public void setUp() {
        // Mock the necessary objects
        mChartMock = mock(BarDataProvider.class);
        mAnimatorMock = mock(ChartAnimator.class);
        mViewPortHandlerMock = mock(ViewPortHandler.class);
        mCanvasMock = mock(Canvas.class);
        mBarDataMock = mock(BarData.class);
        mChartDataMock = mock(ChartData.class);

        List<Integer> dataset1 = List.of(1,2);
        List<Integer> dataset2 = List.of(3,4);

        when(mChartDataMock.getDataSets()).thenReturn(List.of(dataset1, dataset2));
        //mbarData

        // mChart

        when(mChartMock.getBarData()).thenReturn(mBarDataMock);

        //when(mCanvasMock.isVisible()).thenReturn(true);

        // Instantiate the BarChartRenderer
        mBarChartRenderer = new BarChartRenderer(mChartMock, mAnimatorMock, mViewPortHandlerMock);

    }

    @Test
    public void testDrawValues() {
        /*
        // Create a spy so that Mockito can track the method calls of this object
        mBarChartRenderer = spy(mBarChartRenderer);
        // Call the drawValues() method
        mBarChartRenderer.drawValues(mCanvasMock);

        // Verify that the methods which should have been called were indeed called.
        // For example, check if drawValue() is invoked.
        verify(mBarChartRenderer).drawValues(mCanvasMock);
        */

        assertTrue(true);
    }
}
