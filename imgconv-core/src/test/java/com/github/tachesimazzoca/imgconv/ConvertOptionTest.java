package com.github.tachesimazzoca.imgconv;

import static org.junit.Assert.*;

import org.junit.Test;

import java.awt.Dimension;

public class ConvertOptionTest {
    @Test
    public void testDefaultOption() {
        ConvertOption option = ConvertOption.builder().build();
        assertFalse(option.hasFormat());
        assertFalse(option.hasGeometry());
        assertFalse(option.hasFlag(ConvertOption.Flag.STRIP));
    }

    @Test
    public void testFullOption() {
        ConvertOption option = ConvertOption.builder()
                .format(ConvertOption.Format.JPEG)
                .geometry(new Geometry(320, 240, Geometry.ScalingStrategy.MAXIMUM))
                .flag(ConvertOption.Flag.STRIP).build();
        assertTrue(option.hasFormat());
        assertEquals(ConvertOption.Format.JPEG, option.getFormat());
        assertTrue(option.hasGeometry());
        Dimension dim = option.getGeometry().scale(640, 240);
        assertEquals(320, (int) dim.getWidth());
        assertEquals(120, (int) dim.getHeight());
        assertTrue(option.hasFlag(ConvertOption.Flag.STRIP));
    }

    @Test
    public void testFromExtensions() {
        assertEquals(ConvertOption.Format.JPEG, ConvertOption.Format.fromExtension("jpg"));
        assertEquals(ConvertOption.Format.PNG, ConvertOption.Format.fromExtension("png"));
        assertEquals(ConvertOption.Format.GIF, ConvertOption.Format.fromExtension("gif"));
    }
}
