
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.buffer.HorizontalBarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Fill;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Renderer for the HorizontalBarChart.
 *
 * @author Philipp Jahoda
 */
public class HorizontalBarChartRenderer extends BarChartRenderer {
    private static Dictionary<String, Boolean> branches = new Hashtable<>();

    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator,
                                      ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);

        mValuePaint.setTextAlign(Align.LEFT);
    }

    @Override
    public void initBuffers() {

        BarData barData = mChart.getBarData();
        mBarBuffers = new HorizontalBarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new HorizontalBarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    private RectF mBarShadowRectBuffer = new RectF();

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();

            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int)(Math.ceil((float)(dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                 i < count;
                 i++) {

                BarEntry e = dataSet.getEntryForIndex(i);

                x = e.getX();

                mBarShadowRectBuffer.top = x - barWidthHalf;
                mBarShadowRectBuffer.bottom = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsTop(mBarShadowRectBuffer.bottom))
                    continue;

                if (!mViewPortHandler.isInBoundsBottom(mBarShadowRectBuffer.top))
                    break;

                mBarShadowRectBuffer.left = mViewPortHandler.contentLeft();
                mBarShadowRectBuffer.right = mViewPortHandler.contentRight();

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        final boolean isCustomFill = dataSet.getFills() != null && !dataSet.getFills().isEmpty();
        final boolean isSingleColor = dataSet.getColors().size() == 1;
        final boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        for (int j = 0, pos = 0; j < buffer.size(); j += 4, pos++) {

            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3]))
                break;

            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1]))
                continue;

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }

            if (isCustomFill) {
                dataSet.getFill(pos)
                        .fillRect(
                                c, mRenderPaint,
                                buffer.buffer[j],
                                buffer.buffer[j + 1],
                                buffer.buffer[j + 2],
                                buffer.buffer[j + 3],
                                isInverted ? Fill.Direction.LEFT : Fill.Direction.RIGHT);
            }
            else {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint);
            }

            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
    }

    public static Dictionary<String, Boolean> getBranches() {
        return branches;
    }

    @Override
    public void drawValues(Canvas c) {
        // Branch 0
        branches.put("0", true);
        
        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) { 
            // Branch 1
            branches.put("1", true);

            List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.convertDpToPixel(5f);
            float posOffset = 0f;
            float negOffset = 0f;
            final boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

            for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {
                // Branch 2
                branches.put("2", true);

                IBarDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet)) {
                    // Branch 3
                    branches.put("3", true);
                    continue;
                }

                boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);
                final float halfTextHeight = Utils.calcTextHeight(mValuePaint, "10") / 2f;

                IValueFormatter formatter = dataSet.getValueFormatter();

                // get the buffer
                BarBuffer buffer = mBarBuffers[i];

                final float phaseY = mAnimator.getPhaseY();

                MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {
                    // Branch 4
                    branches.put("4", true);

                    for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {
                        // Branch 5
                        branches.put("5", true);

                        float y = (buffer.buffer[j + 1] + buffer.buffer[j + 3]) / 2f;

                        if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1])) {
                            // Branch 6
                            branches.put("6", true);
                            break;
                        }

                        if (!mViewPortHandler.isInBoundsX(buffer.buffer[j])) {
                            // Branch 7
                            branches.put("7", true);
                            continue;
                        }

                        if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
                            // Branch 8
                            branches.put("8", true);
                            continue;
                        }

                        BarEntry entry = dataSet.getEntryForIndex(j / 4);
                        float val = entry.getY();
                        String formattedValue = formatter.getFormattedValue(val, entry, i, mViewPortHandler);

                        // calculate the correct offset depending on the draw position of the value
                        float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                        posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                        if (drawValueAboveBar) {
                            // Branch 9
                            branches.put("9", true);
                        }

                        negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus)
                                - (buffer.buffer[j + 2] - buffer.buffer[j]);

                        if (isInverted) {
                            // Branch 10
                            branches.put("10", true);
                            posOffset = -posOffset - valueTextWidth;
                            negOffset = -negOffset - valueTextWidth;
                        }

                        if (dataSet.isDrawValuesEnabled()) {
                            // Branch 11
                            branches.put("11", true);
                            drawValue(c,
                                    formattedValue,
                                    buffer.buffer[j + 2] + (val >= 0 ? posOffset : negOffset),
                                    y + halfTextHeight,
                                    dataSet.getValueTextColor(j / 2));
                            
                            if (val >= 0) {
                                // Branch 12
                                branches.put("12", true);
                            } 
                        }

                        if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                            // Branch 13
                            branches.put("13", true);

                            Drawable icon = entry.getIcon();

                            float px = buffer.buffer[j + 2] + (val >= 0 ? posOffset : negOffset);

                            if (val >= 0) {
                                // Branch 14
                                branches.put("14", true);
                            }

                            float py = y;

                            px += iconsOffset.x;
                            py += iconsOffset.y;

                            Utils.drawImage(
                                    c,
                                    icon,
                                    (int)px,
                                    (int)py,
                                    icon.getIntrinsicWidth(),
                                    icon.getIntrinsicHeight());
                        }
                    }

                    // if each value of a potential stack should be drawn
                } else {
                    Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                    int bufferIndex = 0;
                    int index = 0;

                    while (index < dataSet.getEntryCount() * mAnimator.getPhaseX()) {
                        // Branch 15
                        branches.put("15", true);

                        BarEntry entry = dataSet.getEntryForIndex(index);

                        int color = dataSet.getValueTextColor(index);
                        float[] vals = entry.getYVals();

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {
                            // Branch 16
                            branches.put("16", true);

                            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[bufferIndex + 1])) {
                                // Branch 17
                                branches.put("17", true);
                                break;
                            }

                            if (!mViewPortHandler.isInBoundsX(buffer.buffer[bufferIndex])) {
                                // Branch 18
                                branches.put("18", true);
                                continue;
                            }

                            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[bufferIndex + 1])) {
                                // Branch 19
                                branches.put("19", true);
                                continue;
                            }

                            float val = entry.getY();
                            String formattedValue = formatter.getFormattedValue(val,
                                    entry, i, mViewPortHandler);

                            // calculate the correct offset depending on the draw position of the value
                            float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                            posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));

                            if (drawValueAboveBar) {
                                // Branch 20
                                branches.put("20", true);
                            } 

                            negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                            if (isInverted) {
                                // Branch 21
                                branches.put("21", true);
                                posOffset = -posOffset - valueTextWidth;
                                negOffset = -negOffset - valueTextWidth;
                            }

                            if (dataSet.isDrawValuesEnabled()) {
                                // Branch 22
                                branches.put("22", true);
                                drawValue(c, formattedValue,
                                        buffer.buffer[bufferIndex + 2]
                                                + (entry.getY() >= 0 ? posOffset : negOffset),
                                        buffer.buffer[bufferIndex + 1] + halfTextHeight, color);

                                if (entry.getY() >= 0) {
                                    // Branch 23
                                    branches.put("23", true);
                                } 
                            }

                            if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                // Branch 24
                                branches.put("24", true);

                                Drawable icon = entry.getIcon();

                                float px = buffer.buffer[bufferIndex + 2]
                                        + (entry.getY() >= 0 ? posOffset : negOffset);

                                if (entry.getY() >= 0) {
                                    // Branch 25
                                    branches.put("25", true);
                                }
                                
                                float py = buffer.buffer[bufferIndex + 1];

                                px += iconsOffset.x;
                                py += iconsOffset.y;

                                Utils.drawImage(
                                        c,
                                        icon,
                                        (int)px,
                                        (int)py,
                                        icon.getIntrinsicWidth(),
                                        icon.getIntrinsicHeight());
                            }

                        } else {
                            float[] transformed = new float[vals.length * 2];

                            float posY = 0f;
                            float negY = -entry.getNegativeSum();

                            for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {
                                // Branch 26
                                branches.put("26", true);
                                float value = vals[idx];
                                float y;

                                if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                                    // Branch 27
                                    branches.put("27", true);

                                    // Take care of the situation of a 0.0 value, which overlaps a non-zero bar
                                    y = value;
                                } else if (value >= 0.0f) {
                                    // Branch 28
                                    branches.put("28", true);
                                    posY += value;
                                    y = posY;
                                } else {
                                    y = negY;
                                    negY -= value;
                                }

                                transformed[k] = y * phaseY;
                            }

                            trans.pointValuesToPixel(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {
                                // Branch 29
                                branches.put("29", true);
                                final float val = vals[k / 2];
                                String formattedValue = formatter.getFormattedValue(val,
                                        entry, i, mViewPortHandler);

                                // calculate the correct offset depending on the draw position of the value
                                float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                                posOffset = (drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus));
                                negOffset = (drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus);

                                if (drawValueAboveBar) {
                                    // Branch 30
                                    branches.put("30", true);
                                }

                                if (isInverted) {
                                    // Branch 31
                                    branches.put("31", true);

                                    posOffset = -posOffset - valueTextWidth;
                                    negOffset = -negOffset - valueTextWidth;
                                }

                                final boolean drawBelow =
                                        (val == 0.0f && negY == 0.0f && posY > 0.0f) ||
                                                val < 0.0f;
                                        
                                        
                                        if (drawBelow == true) {
                                            // Branch 32
                                            branches.put("32", true);
                                        }

                                float x = transformed[k]
                                        + (drawBelow ? negOffset : posOffset);
                                        
                                if (drawBelow) {
                                    // Branch 33
                                    branches.put("33", true);
                                }

                                float y = (buffer.buffer[bufferIndex + 1] + buffer.buffer[bufferIndex + 3]) / 2f;

                                if (!mViewPortHandler.isInBoundsTop(y)) {
                                    // Branch 34
                                    branches.put("34", true);
                                    break;
                                }

                                if (!mViewPortHandler.isInBoundsX(x)) {
                                    // Branch 35
                                    branches.put("35", true);
                                    continue;
                                }

                                if (!mViewPortHandler.isInBoundsBottom(y)) {
                                    // Branch 36
                                    branches.put("36", true);
                                    continue;
                                }

                                if (dataSet.isDrawValuesEnabled()) {
                                    // Branch 37
                                    branches.put("37", true);
                                    drawValue(c, formattedValue, x, y + halfTextHeight, color);
                                }

                                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                                    // Branch 38
                                    branches.put("38", true);

                                    Drawable icon = entry.getIcon();

                                    Utils.drawImage(
                                            c,
                                            icon,
                                            (int)(x + iconsOffset.x),
                                            (int)(y + iconsOffset.y),
                                            icon.getIntrinsicWidth(),
                                            icon.getIntrinsicHeight());
                                }
                            }
                        }

                        bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + 4 * vals.length;
                        if (vals == null) {
                            // Branch 39
                            branches.put("39", true);
                        }
                        index++;
                    }
                }

                MPPointF.recycleInstance(iconsOffset);
            }
        }
    }

    protected void drawValue(Canvas c, String valueText, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(valueText, x, y, mValuePaint);
    }

    @Override
    protected void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {

        float top = x - barWidthHalf;
        float bottom = x + barWidthHalf;
        float left = y1;
        float right = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectToPixelPhaseHorizontal(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    protected void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerY(), bar.right);
    }

    @Override
    protected boolean isDrawingValuesAllowed(ChartInterface chart) {
        return chart.getData().getEntryCount() < chart.getMaxVisibleCount()
                * mViewPortHandler.getScaleY();
    }
}
