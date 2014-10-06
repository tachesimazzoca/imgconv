package com.github.tachesimazzoca.imgconv.servlet.request;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.tachesimazzoca.imgconv.Geometry;
import com.github.tachesimazzoca.imgconv.ConvertOption;
import com.github.tachesimazzoca.imgconv.ConvertOption.Format;

/**
 * A {@code ConvertRequestParser} object is used to parse into a
 * {@link ConvertRequest} object.
 */
public class ConvertRequestParser {
    /**
     * Parse a {@code HttpServletRequest} object.
     * 
     * @param request A {@code HttpServletRequest} object
     * @return A {@code ConvertRequest} object to parse
     */
    public ConvertRequest parse(HttpServletRequest request) {
        Pattern urlPattern = Pattern.compile("^/([^/]+)(/.+)\\.(jpz|jpg|png|pnz|gif)$");
        Matcher urlMatcher = urlPattern.matcher(request.getPathInfo());
        if (!urlMatcher.matches())
            throw new IllegalArgumentException("Unsupported URL pattern");

        String backendName = urlMatcher.group(1);
        String basename = urlMatcher.group(2);
        String extension = urlMatcher.group(3);

        // device
        Device device = DeviceFactory.create(request);
        // copyright
        boolean cg;
        if (extension.endsWith("z")) {
            cg = true;
            extension = extension.substring(0, extension.length() - 1) + "g";
        } else {
            cg = "yes".equals(request.getParameter("copyright"));
        }

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

        // format
        Format ft = Format.fromExtension(extension);
        List<Format> formats = Arrays.asList(device.getAcceptFormats());
        Format outputFormat;
        if (!formats.isEmpty() && !formats.contains(ft))
            outputFormat = formats.get(0);
        else
            outputFormat = ft;
        ConvertOption cvOpt = ConvertOption.builder()
                .format(outputFormat)
                .geometry(new Geometry(w, h, Geometry.ScalingStrategy.MAXIMUM))
                .build();

        return new ConvertRequest(backendName, basename + "." + extension, device, cvOpt, cg);
    }
}
