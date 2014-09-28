package com.github.tachesimazzoca.imgconv.servlet.request;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import com.github.tachesimazzoca.imgconv.Geometry;

public class DeviceFactoryTest {
    @Test
    public void testNoUserAgent() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        Device device = DeviceFactory.create(req);
        assertEquals(Device.Group.OTHER, device.getGroup());
        assertEquals(Geometry.NO_VALUE, device.getDisplayWidth());
        assertEquals(Geometry.NO_VALUE, device.getDisplayHeight());
        assertEquals(0, device.getExtensions().length);
    }

    @Test
    public void testUnknownDevice() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("user-agent")).thenReturn("");
        Device device = DeviceFactory.create(req);
        assertEquals(Device.Group.OTHER, device.getGroup());
        assertEquals(Geometry.NO_VALUE, device.getDisplayWidth());
        assertEquals(Geometry.NO_VALUE, device.getDisplayHeight());
        assertEquals(0, device.getExtensions().length);
    }

    @Test
    public void testDocomoDevice() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("user-agent")).thenReturn(
                "DoCoMo/2.0 P903i(c100;TB;W24H12)");
        Device device = DeviceFactory.create(req);
        assertEquals(Device.Group.DOCOMO, device.getGroup());
        assertEquals(240, device.getDisplayWidth());
        assertEquals(320, device.getDisplayHeight());
        assertArrayEquals(new String[] { "jpg", "gif" }, device.getExtensions());
    }

    @Test
    public void testAuDevice() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("user-agent")).thenReturn(
                "KDDI-CA39 UP.Browser/6.2.0.13.1.5 (GUI) MMP/2.0");
        Device device = DeviceFactory.create(req);
        assertEquals(Device.Group.AU, device.getGroup());
        assertEquals(240, device.getDisplayWidth());
        assertEquals(320, device.getDisplayHeight());
        assertArrayEquals(new String[] { "jpg", "gif", "png" }, device.getExtensions());
    }

    @Test
    public void testSoftbankDevice() {
        final String ua = "SoftBank/1.0/930SH/SHJ001 Browser/NetFront/3.4" +
                " Profile/MIDP-2.0 Configuration/CLDC-1.1";
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("user-agent")).thenReturn(ua);
        when(req.getHeader("x-jphone-display")).thenReturn("480*640");
        Device device = DeviceFactory.create(req);
        assertEquals(Device.Group.SOFTBANK, device.getGroup());
        assertEquals(480, device.getDisplayWidth());
        assertEquals(640, device.getDisplayHeight());
        assertArrayEquals(new String[] { "jpg", "png" }, device.getExtensions());
    }

    @Test
    public void testWillcomDevice() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("user-agent")).thenReturn(
                "Mozilla/3.0(WILLCOM;KYOCERA/WX340K/2;3.0.3.11.000000/1/C256) NetFront/3.4");
        Device device = DeviceFactory.create(req);
        assertEquals(Device.Group.WILLCOM, device.getGroup());
        assertEquals(240, device.getDisplayWidth());
        assertEquals(320, device.getDisplayHeight());
        assertArrayEquals(new String[] { "jpg", "gif", "png" }, device.getExtensions());
    }
}
