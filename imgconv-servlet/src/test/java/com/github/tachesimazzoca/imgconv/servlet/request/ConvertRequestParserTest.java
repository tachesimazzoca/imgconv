package com.github.tachesimazzoca.imgconv.servlet.request;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

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
    public void testParseValidRequest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/foo/a.jpg");
        when(req.getParameter("copyright")).thenReturn(null);
        ConvertRequestParser parser = new ConvertRequestParser();
        ConvertRequest cr = parser.parse(req);

        assertEquals("foo", cr.getBackendName());
        assertEquals("/a", cr.getPath());
        assertFalse(cr.getCopyright());
    }

    @Test
    public void testParseValidSubdirRequest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/foo/img/a.jpg");
        when(req.getParameter("copyright")).thenReturn(null);
        ConvertRequestParser parser = new ConvertRequestParser();
        ConvertRequest cr = parser.parse(req);

        assertEquals("foo", cr.getBackendName());
        assertEquals("/img/a", cr.getPath());
        assertFalse(cr.getCopyright());
    }

    @Test
    public void testParseSizeWidith() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/foo/img/icon.jpg");
        when(req.getParameter("copyright")).thenReturn("yes");
        when(req.getParameter("size")).thenReturn("200w");
        ConvertRequestParser parser = new ConvertRequestParser();
        ConvertRequest cr = parser.parse(req);

        assertEquals("foo", cr.getBackendName());
        assertEquals("/img/icon", cr.getPath());
        assertEquals(new Geometry(200, Geometry.NO_VALUE), cr.getGeometry());
        assertTrue(cr.getCopyright());
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
        assertEquals("/img/icon", cr.getPath());
        assertEquals(new Geometry(Geometry.NO_VALUE, 120), cr.getGeometry());
        assertTrue(cr.getCopyright());
    }
}
