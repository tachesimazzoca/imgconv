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
import com.github.tachesimazzoca.imgconv.converter.KtaiCopyguardConverter;
import com.github.tachesimazzoca.imgconv.fetcher.Fetcher;
import com.github.tachesimazzoca.imgconv.fetcher.FetcherFactory;
import com.github.tachesimazzoca.imgconv.servlet.request.ConvertRequest;
import com.github.tachesimazzoca.imgconv.servlet.request.ConvertRequestParser;
import com.github.tachesimazzoca.imgconv.servlet.request.Device;

public class ImgconvServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Map<ConvertOption.Format, String> CONTENT_TYPE_MAP =
            ImmutableMap.of(
                    ConvertOption.Format.JPEG, "image/jpeg",
                    ConvertOption.Format.GIF, "image/gif",
                    ConvertOption.Format.PNG, "image/png");

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

        Optional<InputStream> img = fetcher.fetch(cr.getPath());
        if (!img.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // device
        Device device = cr.getDevice();

        // contentType
        ConvertOption cvOpt = cr.getConvertOption();
        String contentType;
        if (cvOpt.hasFormat()) {
            contentType = CONTENT_TYPE_MAP.get(cvOpt.getFormat());
        } else {
            contentType = null;
        }

        // output
        OutputStream out = response.getOutputStream();
        try {
            if (contentType != null)
                response.setContentType(contentType);
            else
                response.setContentType("application/octet-stream");

            if (cr.isNoTransfer()) {
                if (device.getGroup() == Device.Group.SOFTBANK) {
                    response.addHeader("x-jphone-copyright", "no-transfer");
                    response.addHeader("x-jphone-copyright", "no-peripheral");
                }
                ImageUtils.convert(img.get(), out, cvOpt, new KtaiCopyguardConverter());
            } else {
                ImageUtils.convert(img.get(), out, cvOpt);
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
