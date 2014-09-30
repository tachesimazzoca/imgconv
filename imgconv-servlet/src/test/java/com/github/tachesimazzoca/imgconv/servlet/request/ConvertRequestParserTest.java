package com.github.tachesimazzoca.imgconv.servlet.request;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import com.github.tachesimazzoca.imgconv.ConvertOption.Format;
import com.github.tachesimazzoca.imgconv.Geometry;

public class ConvertRequestParserTest {
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testParseInvalidURL() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/a.jpg");
        ConvertRequestParser parser = new ConvertRequestParser();
        parser.parse(req);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testParseInvalidFormat() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/foo/a.txt");
        ConvertRequestParser parser = new ConvertRequestParser();
        parser.parse(req);
    }

    @Test
    public void testParseValidJPZRequest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/foo/a.jpz");
        ConvertRequestParser parser = new ConvertRequestParser();
        ConvertRequest cr = parser.parse(req);

        assertEquals("foo", cr.getBackendName());
        assertEquals("/a.jpg", cr.getPath());
        assertEquals(Format.JPEG, cr.getConvertOption().getFormat());
        assertTrue(cr.isNoTransfer());
    }

    @Test
    public void testParseValidSubdirPNGRequest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/foo/img/a.png");
        ConvertRequestParser parser = new ConvertRequestParser();
        ConvertRequest cr = parser.parse(req);

        assertEquals("foo", cr.getBackendName());
        assertEquals("/img/a.png", cr.getPath());
        assertEquals(Format.PNG, cr.getConvertOption().getFormat());
        assertFalse(cr.isNoTransfer());
    }

    @Test
    public void testParseSizeWidith() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/foo/img/icon.gif");
        when(req.getParameter("copyright")).thenReturn("yes");
        when(req.getParameter("size")).thenReturn("200w");
        ConvertRequestParser parser = new ConvertRequestParser();
        ConvertRequest cr = parser.parse(req);

        assertEquals("foo", cr.getBackendName());
        assertEquals("/img/icon.gif", cr.getPath());
        assertEquals(Format.GIF, cr.getConvertOption().getFormat());
        assertEquals(new Geometry(200, Geometry.NO_VALUE),
                cr.getConvertOption().getGeometry());
        assertTrue(cr.isNoTransfer());
    }

    @Test
    public void testParseSizeHeight() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/foo/img/icon.jpg");
        when(req.getParameter("copyright")).thenReturn("yes");
        when(req.getParameter("size")).thenReturn("120h");
        ConvertRequestParser parser = new ConvertRequestParser();
        ConvertRequest cr = parser.parse(req);

        assertEquals("foo", cr.getBackendName());
        assertEquals("/img/icon.jpg", cr.getPath());
        assertEquals(Format.JPEG, cr.getConvertOption().getFormat());
        assertEquals(new Geometry(Geometry.NO_VALUE, 120),
                cr.getConvertOption().getGeometry());
        assertTrue(cr.isNoTransfer());
    }
}
