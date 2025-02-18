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
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.buffer.BarBuffer;


import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BarChartRendererTest {

    /**
     * Requirements (All are untested):
     * 1. The method doesn't draw/does draw something.
     * 2. Handling no data
     * 3. The outer for loop iterates the correct number of times.
     * 4. The method handles valid data.
     * */
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
        mChartDataMock = mock(ChartData.class);


        List<IBarDataSet> dataSets = new ArrayList<>();

        // Add two data entries (that are not a valid format):
        for(int i = 0; i < 2; i++) dataSets.add(mDataSetMock);

        when(mBarDataMock.getDataSets()).thenReturn(dataSets);
        when(mChartMock.getBarData()).thenReturn(mBarDataMock);

        // Use Mock classes
        mBarChartRendererNotAllowed = new TestBarChartRendererNotAllowed(mChartMock, mAnimatorMock, mViewPortHandlerMock);
        mBarChartRendererNotAllowed = spy(mBarChartRendererNotAllowed);

        mBarChartRendererAllowed = new TestBarChartRendererAllowed(mChartMock, mAnimatorMock, mViewPortHandlerMock);
        mBarChartRendererAllowed = spy(mBarChartRendererAllowed);
    }

    /*Variables that are used for the valid data*/
    BarDataProvider mChartValidData;
    BarData mBarDataValid;
    IBarDataSet dataSetMock;
    TestBarChartRendererValidData mRenderer;
    MPPointF iconOffsetMock;



    @Before
    public void setUpValidData(){
        mChartValidData = mock(BarDataProvider.class);
        mBarDataValid = mock(BarData.class);
        // line 209
        dataSetMock = mock(IBarDataSet.class);
        when(mBarDataValid.getDataSets()).thenReturn(new ArrayList<>(Arrays.asList(dataSetMock,dataSetMock,dataSetMock,dataSetMock)));
        when(mChartValidData.getBarData()).thenReturn(mBarDataValid);

        // line 214
        when(mChartValidData.isDrawValueAboveBarEnabled()).thenReturn(true);

        // line 216
        when(mBarDataValid.getDataSetCount()).thenReturn(4); // has to match number of datapoints.


        // line 220 and 224 handled in mock class definition.

        // line 226, OBS: The import could be wrong, please check if it doesn't work.
        when(dataSetMock.getAxisDependency()).thenReturn(AxisDependency.LEFT);
        when(mChartValidData.isInverted(AxisDependency.LEFT)).thenReturn(false);

        // line 230, do I have to handle the Utils call? Don't think so.

        // line 242
        when(mAnimatorMock.getPhaseY()).thenReturn(1f);

        // line 244, maybe also need to handle the attributes?
        iconOffsetMock = mock(MPPointF.class);
        when(dataSetMock.getIconsOffset()).thenReturn(iconOffsetMock);

        // line 251 I want to exit, just want to test if it gets through the outer for loop.
        when(mAnimatorMock.getPhaseX()).thenReturn(0f); // This should make the inner loop not execute.

        mRenderer = new TestBarChartRendererValidData(mChartValidData, mAnimatorMock, mViewPortHandlerMock); //Potentially update the ones that are old.

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

    @Test
    public void testDrawValuesForLoopIterations() {
        when(mBarDataMock.getDataSetCount()).thenReturn(2);
        mBarChartRendererAllowed.drawValues(mCanvasMock);

        verify(mBarChartRendererAllowed, times(1)).drawValues(mCanvasMock);
        // The following is called once per loop, meaning it should be called two times if there are two datapoints.
        verify(mBarChartRendererAllowed, times(2)).shouldDrawValues(mDataSetMock);

    }

    @Test
    public void testDrawValuesValidData() {
        mRenderer.drawValues(mCanvasMock);
        //verify(mRenderer, times(1)).drawValues(mCanvasMock);
    }
}

/*
Below are mock classes meant to override certain methods that are protected and can therefore not be
mocked using Mockito.
*/

// A mock class where drawing values is not allowed.
class TestBarChartRendererNotAllowed extends BarChartRenderer {
    public TestBarChartRendererNotAllowed(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return false; // Mock behavior
    }
}

// A mock class where drawing values is allowed.
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
        return false; // Mock behavior, if this is false we don't have to construct an actual data object.
    }
}

class TestBarChartRendererValidData extends BarChartRenderer {
    public TestBarChartRendererValidData(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        this.mBarBuffers = new BarBuffer[] {mock(BarBuffer.class)}; // OBS: This has to include an attribute .buffer.length, set it to 0?
    }

    @Override
    public boolean isDrawingValuesAllowed(ChartInterface chart) {
        return true; // Mock behavior
    }
    @Override
    public boolean shouldDrawValues(IDataSet set){
        return true; // Mock behavior
    }
    @Override
    public void applyValueTextStyle(IDataSet set){
        return;
    }
}

