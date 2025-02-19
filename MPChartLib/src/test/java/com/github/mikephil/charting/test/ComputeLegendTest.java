package com.github.mikephil.charting.test;

import com.github.mikephil.charting.utils.ViewPortHandler;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.renderer.LegendRenderer;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

import org.junit.AfterClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;


/**
 * Created by jannikhoesch on 18/02/25.
 */
public class ComputeLegendTest {

    static boolean[] branches = new boolean[17];

    @Test
    public void testComputeLegend() {

        // Create sample datasets
        List<Entry> entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(10, 10));
        entries1.add(new Entry(15, -2));
        entries1.add(new Entry(21, 50));
        ScatterDataSet set1 = new ScatterDataSet(entries1, "Dataset 1");

        List<Entry> entries2 = new ArrayList<Entry>();
        entries2.add(new Entry(-1, 10));
        entries2.add(new Entry(10, 2));
        entries2.add(new Entry(20, 5));
        ScatterDataSet set2 = new ScatterDataSet(entries2, "Dataset 2");

        ScatterData data = new ScatterData(set1, set2);

        // Create the legend renderer
        ViewPortHandler viewPortHandler = new ViewPortHandler();
        Legend legend = new Legend();
        LegendRenderer renderer = new LegendRenderer(viewPortHandler, legend);


        // Requirement: Ensure computeLegend() runs without throwing an exception
        try {
            renderer.computeLegend(data);
        } catch (Exception e) {
            fail("computeLegend() should not throw an exception: " + e.getMessage());
        }

        // Requirement: The legend should contain one entry per dataset
        assertEquals("Legend should have the same number of entries as datasets",
                2, legend.getEntries().length);

        // Requirement: The legend should not be null
        assertNotNull("Legend entries should not be null", legend.getEntries());

        // Requirement: The legend should correctly reflect dataset labels
        assertEquals("First legend entry label should match dataset 1",
                "Dataset 1", legend.getEntries()[0].label);
        assertEquals("Second legend entry label should match dataset 2",
                "Dataset 2", legend.getEntries()[1].label);

        // Requirement: Legend entries should have valid labels
        assertFalse("Legend labels should not be empty",
                legend.getEntries()[0].label.isEmpty());
        assertFalse("Legend labels should not be empty",
                legend.getEntries()[1].label.isEmpty());

        // Track branches
        boolean[] b = renderer.getBranches();
        for (int i = 0; i < b.length; i++) {
            branches[i] = b[i] || branches[i];
        }
    }

    @Test
    public void testComputeLegendWithBarDataSet() {
        // Create a BarDataset
        List<BarEntry> entries = new ArrayList<>();
        float[] stack1 = {10f, 20f};
        float[] stack2 = {30f, 40f};
        entries.add(new BarEntry(0f, stack1));
        entries.add(new BarEntry(1f, stack2));
        IBarDataSet barDataSet = new BarDataSet(entries, "Bar Dataset");

        // Create a ScatterData (or whatever is required for the test)
        BarData data = new BarData(barDataSet);

        // Create the legend renderer
        ViewPortHandler viewPortHandler = new ViewPortHandler();
        Legend legend = new Legend();
        LegendRenderer renderer = new LegendRenderer(viewPortHandler, legend);


        // Requirement: Ensure computeLegend() runs without throwing an exception
        try {
            renderer.computeLegend(data);
        } catch (Exception e) {
            fail("computeLegend() should not throw an exception: " + e.getMessage());
        }

        // Requirement: The legend should contain one entry per dataset
        assertEquals("Legend should have the same number of entries as datasets",
                2, legend.getEntries().length);

        // Requirement: The legend should not be null
        assertNotNull("Legend entries should not be null", legend.getEntries());

        // Track branches
        boolean[] b = renderer.getBranches();
        for (int i = 0; i < b.length; i++) {
            branches[i] = b[i] || branches[i];
        }
    }

    @Test
    public void testComputeLegendWithPieDataSet() {

        // Create sample Pie datasets
        List<PieEntry> entries1 = new ArrayList<>();
        entries1.add(new PieEntry(10f, "Category 1"));
        entries1.add(new PieEntry(20f, "Category 2"));
        IPieDataSet set1 = new PieDataSet(entries1, "Dataset 1");

        List<PieEntry> entries2 = new ArrayList<>();
        entries2.add(new PieEntry(30f, "Category 3"));
        entries2.add(new PieEntry(40f, "Category 4"));
        IPieDataSet set2 = new PieDataSet(entries2, "Dataset 2");

        // Create PieData object with the datasets
        PieData data = new PieData(set1);

        // Create the legend renderer
        ViewPortHandler viewPortHandler = new ViewPortHandler();
        Legend legend = new Legend();
        LegendRenderer renderer = new LegendRenderer(viewPortHandler, legend);

        // Requirement: Ensure computeLegend() runs without throwing an exception
            try {
            renderer.computeLegend(data);
        } catch (Exception e) {
            fail("computeLegend() should not throw an exception: " + e.getMessage());
        }

        // Requirement: The legend should contain one entry per dataset
        assertEquals("Legend should have the same number of entries as datasets",
                2, legend.getEntries().length);

        // Requirement: The legend should not be null
        assertNotNull("Legend entries should not be null", legend.getEntries());

        // Track branches
        boolean[] b = renderer.getBranches();
        for (int i = 0; i < b.length; i++) {
            branches[i] = b[i] || branches[i];
        }
    }

    @Test
    public void testComputeLegendWithCandleDataSet() {

        // Create sample Candle datasets
        List<CandleEntry> entries1 = new ArrayList<>();
            entries1.add(new CandleEntry(0f, 10f, 5f, 8f, 6f));
            entries1.add(new CandleEntry(1f, 20f, 15f, 18f, 14f));
        ICandleDataSet set1 = new CandleDataSet(entries1, "Dataset 1");

        List<CandleEntry> entries2 = new ArrayList<>();
            entries2.add(new CandleEntry(0f, 25f, 15f, 20f, 18f));
            entries2.add(new CandleEntry(1f, 30f, 25f, 28f, 24f));
        ICandleDataSet set2 = new CandleDataSet(entries2, "Dataset 2");

        // Create CandleData object with the datasets
        CandleData data = new CandleData(set1);

        // Create the legend renderer
        ViewPortHandler viewPortHandler = new ViewPortHandler();
        Legend legend = new Legend();
        LegendRenderer renderer = new LegendRenderer(viewPortHandler, legend);

        // Requirement: Ensure computeLegend() runs without throwing an exception
        try {
            renderer.computeLegend(data);
        } catch (Exception e) {
            fail("computeLegend() should not throw an exception: " + e.getMessage());
        }

        // Requirement: The legend should contain one entry per dataset
        assertEquals("Legend should have the same number of entries as datasets",
                             2, legend.getEntries().length);

        // Requirement: The legend should not be null
        assertNotNull("Legend entries should not be null", legend.getEntries());

        // Track branches
        boolean[] b = renderer.getBranches();
        for (int i = 0; i < b.length; i++) {
            branches[i] = b[i] || branches[i];
        }
    }

    @AfterClass
    public static void printCoverage() {
        // Track branches
        int c = 0;
        for (int i = 0; i < branches.length; i++) {
            if (branches[i]) c++;
            System.out.println("Branch " + i + ": " + branches[i]);
        }

        double coverage = (double) c / (double) branches.length;
        System.out.println("------------------\nBranch coverage: " + coverage);
    }
}