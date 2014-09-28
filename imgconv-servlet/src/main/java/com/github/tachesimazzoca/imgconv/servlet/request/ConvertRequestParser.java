package com.github.tachesimazzoca.imgconv.servlet.request;

import javax.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.tachesimazzoca.imgconv.Geometry;

public class ConvertRequestParser {
    public ConvertRequest parse(HttpServletRequest request) {
        final Pattern urlPattern = Pattern.compile("^/([^/]+)(/.+)\\.(jpz|jpg|png|pnz|gif)$");
        final Matcher urlMatcher = urlPattern.matcher(request.getPathInfo());
        if (!urlMatcher.matches())
            throw new IllegalArgumentException("Unsupported URL pattern");
        // device
        Device device = DeviceFactory.create(request);
        // copyright
        final boolean cr = "yes".equals(request.getParameter("copyright"));
        // geometry
        int w = Geometry.NO_VALUE;
        int h = Geometry.NO_VALUE;
        String sizeParam = request.getParameter("size");
        if (sizeParam != null) {
            final Pattern sizePattern = Pattern.compile("^([0-9]+)(p)?(w|h)$");
            final Matcher sizeMatcher = sizePattern.matcher(sizeParam);
            if (sizeMatcher.matches()) {
                if (sizeMatcher.group(3).equals("w")) {
                    w = Integer.valueOf(sizeMatcher.group(1));
                    int dw = device.getDisplayWidth();
                    if (sizeMatcher.group(2) != null && dw != Geometry.NO_VALUE) {
                        w = (dw * w / 100);
                    }
                } else {
                    h = Integer.valueOf(sizeMatcher.group(1));
                    int dh = device.getDisplayHeight();
                    if (sizeMatcher.group(2) != null && dh != Geometry.NO_VALUE) {
                        h = (dh * h / 100);
                    }
                }
            }
        }

        return new ConvertRequest(urlMatcher.group(1), urlMatcher.group(2),
                device.getExtensions(), new Geometry(w, h), cr);
    }
}
