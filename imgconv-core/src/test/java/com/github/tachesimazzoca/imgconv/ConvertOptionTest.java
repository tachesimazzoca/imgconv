package com.github.tachesimazzoca.imgconv;

import static org.junit.Assert.*;

import org.junit.Test;

import java.awt.Dimension;

public class ConvertOptionTest {
    @Test
    public void testDefaultOption() {
        ConvertOption option = ConvertOption.builder().build();
        assertEquals(ConvertOption.Format.ANY, option.getFormat());
        assertFalse(option.hasFlag(ConvertOption.Flag.STRIP));
    }

    @Test
    public void testFullOption() {
        ConvertOption option = ConvertOption.builder()
                .format(ConvertOption.Format.JPEG)
                .geometry(new Geometry(320, 240, Geometry.ScalingStrategy.MAXIMUM))
                .flag(ConvertOption.Flag.STRIP).build();
        assertEquals(ConvertOption.Format.JPEG, option.getFormat());
        Dimension dim = option.getGeometry().scale(640, 240);
        assertEquals(320, (int) dim.getWidth());
        assertEquals(120, (int) dim.getHeight());
        assertTrue(option.hasFlag(ConvertOption.Flag.STRIP));
    }
}
