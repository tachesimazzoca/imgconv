package com.github.tachesimazzoca.imgconv.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.IOUtils;

import com.github.tachesimazzoca.imgconv.ConvertOption;
import com.github.tachesimazzoca.imgconv.ImageUtils;
import com.github.tachesimazzoca.imgconv.converter.KetaiCopyguardConverter;
import com.github.tachesimazzoca.imgconv.fetcher.Fetcher;
import com.github.tachesimazzoca.imgconv.fetcher.FetcherFactory;
import com.github.tachesimazzoca.imgconv.servlet.request.ConvertRequest;
import com.github.tachesimazzoca.imgconv.servlet.request.ConvertRequestParser;
import com.github.tachesimazzoca.imgconv.servlet.request.Device;

public class ImgconvServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Map<String, String> CONTENT_TYPE_MAP = ImmutableMap.of(
            "jpg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif");

    private Map<String, Fetcher> fetcherMap;

    @Override
    public void init() throws ServletException {
        try {
            ServletConfig config = getServletConfig();
            String path = config.getInitParameter("conf.backend");
            if (path == null)
                throw new ServletException("The init-param conf.backend is null.");
            // The field "configuration" is a read-only map. Never
            // modify it because it's not thread-safe.
            fetcherMap = FetcherFactory.createFetcherMap(new File(path));
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ConvertRequestParser parser = new ConvertRequestParser();
        ConvertRequest cr = null;
        try {
            cr = parser.parse(request);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Fetcher fetcher = fetcherMap.get(cr.getBackendName());
        if (fetcher == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String ext = cr.getExtension();
        Optional<InputStream> img = fetcher.fetch(cr.getBasename() + "." + ext);
        if (!img.isPresent()) {
            String[] exts = { "jpg", "gif", "png" };
            for (int i = 0; i < exts.length; i++) {
                ext = null;
                if (cr.getExtension().equals(exts[i]))
                    continue;
                img = fetcher.fetch(cr.getBasename() + "." + exts[i]);
                if (img.isPresent()) {
                    ext = exts[i];
                    break;
                }
            }
        }
        if (!img.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // device
        Device device = cr.getDevice();
        // detect output format
        ConvertOption.Format format = detectFormat(ext, device.getExtensions());
        // convertOption
        ConvertOption.Builder builder = ConvertOption.builder();
        if (format != null)
            builder.format(format);
        builder.geometry(cr.getGeometry());
        ConvertOption cvOpt = builder.build();

        // output
        OutputStream out = response.getOutputStream();
        try {
            if (CONTENT_TYPE_MAP.containsKey(ext))
                response.setContentType(CONTENT_TYPE_MAP.get(ext));
            if (cr.getCopyright()) {
                if (device.getGroup() == Device.Group.SOFTBANK) {
                    response.addHeader("x-jphone-copyright", "no-transfer");
                    response.addHeader("x-jphone-copyright", "no-peripheral");
                }
                ImageUtils.convert(img.get(), out, cvOpt, new KetaiCopyguardConverter());
            } else {
                ImageUtils.convert(img.get(), out, cvOpt);
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private static ConvertOption.Format detectFormat(String extension, String[] supported) {
        String outExt = null;
        if (supported.length == 0) {
            outExt = extension;
        } else {
            for (int i = 0; i < supported.length; i++) {
                if (supported[i].equals(extension)) {
                    outExt = extension;
                    break;
                }
            }
            if (outExt == null)
                outExt = supported[0];
        }
        if (!outExt.equals(extension))
            return ConvertOption.Format.fromExtension(outExt);
        else
            return null;
    }
}
