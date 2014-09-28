package com.github.tachesimazzoca.imgconv.servlet;

import javax.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.tachesimazzoca.imgconv.Geometry;

public class ConvertRequestParser {
    public ConvertRequest parse(HttpServletRequest request) {
        Pattern ptn = Pattern.compile("^/([^/]+)(/.+)\\.(jpz|jpg|png|pnz|gif)$");
        Matcher m = ptn.matcher(request.getPathInfo());
        if (!m.matches())
            throw new IllegalArgumentException("Unsupported URL pattern");

        String[] exts = { m.group(3) };
        Geometry geo = new Geometry(Geometry.NO_VALUE, Geometry.NO_VALUE);
        boolean cr = "yes".equals(request.getParameter("copyright"));
        return new ConvertRequest(m.group(1), m.group(2), exts, geo, cr);
    }
}
