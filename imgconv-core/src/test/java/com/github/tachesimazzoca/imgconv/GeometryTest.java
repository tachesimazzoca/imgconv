package com.github.tachesimazzoca.imgconv;

import static org.junit.Assert.*;

import org.junit.Test;

import java.awt.Dimension;

public class GeometryTest {
    @Test
    public void testConstructor() {
        // valid arguments
        new Geometry(Geometry.NO_VALUE, Geometry.NO_VALUE);
        new Geometry(1, 2);

        // invalid arguments
        int[][] whs = { { -1, -1 }, { -1, Geometry.NO_VALUE }, { Geometry.NO_VALUE, -1 } };
        for (int i = 0; i < whs.length; i++) {
            try {
                new Geometry(whs[i][0], whs[i][1]);
                fail("It should throw IllegalArgumentException.");
            } catch (IllegalArgumentException e) {
                // OK
            }
        }
    }

    @Test
    public void testScaleEmphatic() {
        int[][] wh = {
                // 150x100
                { 320, 240, 150, 100, 320, 240 },
                { Geometry.NO_VALUE, Geometry.NO_VALUE, 150, 100, 150, 100 },
                { Geometry.NO_VALUE, 240, 150, 100, 150, 240 },
                { 320, Geometry.NO_VALUE, 150, 100, 320, 100 } };

        for (int i = 0; i < wh.length; i++) {
            Geometry geo = new Geometry(wh[i][0], wh[i][1], Geometry.ScalingStrategy.EMPHATIC);
            Dimension dim = geo.scale(wh[i][2], wh[i][3]);
            assertEquals(wh[i][4], (int) dim.getWidth());
            assertEquals(wh[i][5], (int) dim.getHeight());
        }
    }

    @Test
    public void testScaleMaximum() {
        int[][] wh = {
                // 150x100
                { 320, 240, 150, 100, 150, 100 },
                { Geometry.NO_VALUE, Geometry.NO_VALUE, 150, 100, 150, 100 },
                { Geometry.NO_VALUE, 240, 150, 100, 150, 100 },
                { 320, Geometry.NO_VALUE, 150, 100, 150, 100 },
                // 400x100
                { 320, 240, 400, 100, 320, 80 },
                { Geometry.NO_VALUE, 50, 400, 100, 200, 50 },
                { 320, Geometry.NO_VALUE, 400, 100, 320, 80 },
                // 300x480
                { 320, 240, 300, 480, 150, 240 },
                { Geometry.NO_VALUE, 240, 300, 480, 150, 240 },
                { 320, Geometry.NO_VALUE, 300, 480, 300, 480 } };

        for (int i = 0; i < wh.length; i++) {
            Geometry geo = new Geometry(wh[i][0], wh[i][1], Geometry.ScalingStrategy.MAXIMUM);
            Dimension dim = geo.scale(wh[i][2], wh[i][3]);
            assertEquals(wh[i][4], (int) dim.getWidth());
            assertEquals(wh[i][5], (int) dim.getHeight());
        }
    }

    @Test
    public void testMinimumAdjustment() {
        int[][] wh = {
                // 640x480
                { 320, 240, 640, 480, 640, 480 },
                { Geometry.NO_VALUE, 240, 640, 480, 640, 480 },
                { 320, Geometry.NO_VALUE, 640, 480, 640, 480 },
                // 200x120
                { 320, 240, 200, 120, 400, 240 },
                { Geometry.NO_VALUE, 240, 200, 120, 400, 240 },
                { 320, Geometry.NO_VALUE, 200, 120, 320, 192 },
                // 160x200
                { 320, 240, 160, 200, 320, 400 },
                { Geometry.NO_VALUE, 240, 160, 200, 192, 240 },
                { 320, Geometry.NO_VALUE, 160, 200, 320, 400 } };

        for (int i = 0; i < wh.length; i++) {
            Geometry geo = new Geometry(wh[i][0], wh[i][1], Geometry.ScalingStrategy.MINIMUM);
            Dimension dim = geo.scale(wh[i][2], wh[i][3]);
            assertEquals(wh[i][4], (int) dim.getWidth());
            assertEquals(wh[i][5], (int) dim.getHeight());
        }
    }
}
