package com.github.tachesimazzoca.imgconv.cli;

import static org.junit.Assert.*;

import org.junit.Test;

import java.awt.Dimension;

import com.github.tachesimazzoca.imgconv.ConvertOption;
import com.github.tachesimazzoca.imgconv.Geometry;
import com.github.tachesimazzoca.imgconv.cli.ConvertCommand.FormatOption;

public class ConvertCommandTest {
    @Test
    public void testConvertArgToGeometry() {
        Geometry geo;
        // EMPHATIC
        geo = ConvertCommand.convertArgToGeometry("320x240!");
        assertEquals(new Dimension(320, 240), geo.scale(1, 2));
        geo = ConvertCommand.convertArgToGeometry("320x!");
        assertEquals(new Dimension(320, 2), geo.scale(1, 2));
        geo = ConvertCommand.convertArgToGeometry("x240!");
        assertEquals(new Dimension(1, 240), geo.scale(1, 2));
        geo = ConvertCommand.convertArgToGeometry("x!");
        assertEquals(new Dimension(1, 2), geo.scale(1, 2));

        // MAXIMUM
        String[] opts = { "", ">" };
        for (int i = 0; i < opts.length; i++) {
            geo = ConvertCommand.convertArgToGeometry("320x240" + opts[i]);
            assertEquals(new Dimension(320, 100), geo.scale(640, 200));
            assertEquals(new Dimension(180, 240), geo.scale(480, 640));
            geo = ConvertCommand.convertArgToGeometry("320x" + opts[i]);
            assertEquals(new Dimension(320, 100), geo.scale(640, 200));
            assertEquals(new Dimension(320, 320), geo.scale(640, 640));
            geo = ConvertCommand.convertArgToGeometry("x240" + opts[i]);
            assertEquals(new Dimension(640, 200), geo.scale(640, 200));
            assertEquals(new Dimension(240, 240), geo.scale(640, 640));
            geo = ConvertCommand.convertArgToGeometry("x" + opts[i]);
            assertEquals(new Dimension(640, 480), geo.scale(640, 480));
            assertEquals(new Dimension(640, 480), geo.scale(640, 480));
        }

        // MINIMUM
        geo = ConvertCommand.convertArgToGeometry("320x240<");
        assertEquals(new Dimension(640, 240), geo.scale(320, 120));
        assertEquals(new Dimension(480, 240), geo.scale(160, 80));
        geo = ConvertCommand.convertArgToGeometry("320x<");
        assertEquals(new Dimension(320, 120), geo.scale(320, 120));
        assertEquals(new Dimension(320, 160), geo.scale(160, 80));
        geo = ConvertCommand.convertArgToGeometry("x240<");
        assertEquals(new Dimension(640, 240), geo.scale(320, 120));
        assertEquals(new Dimension(480, 240), geo.scale(160, 80));
        geo = ConvertCommand.convertArgToGeometry("x<");
        assertEquals(new Dimension(640, 480), geo.scale(640, 480));
        assertEquals(new Dimension(640, 480), geo.scale(640, 480));
    }

    @Test
    public void testFormatOption() {
        assertEquals(ConvertOption.Format.JPEG, FormatOption.fromPath("/path/to/a.jpg"));
        assertEquals(ConvertOption.Format.PNG, FormatOption.fromPath("/path/to/a.png"));
        assertEquals(ConvertOption.Format.GIF, FormatOption.fromPath("/path/to/a.gif"));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testInvalidFormatOption() {
        FormatOption.fromPath("/path/to/a.txt");
    }
}
