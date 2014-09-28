package com.github.tachesimazzoca.imgconv.servlet;

import com.github.tachesimazzoca.imgconv.Geometry;

public class ConvertRequest {
    private final String backendName;
    private final String path;
    private final String[] extensions;
    private final Geometry geometry;
    private final boolean copyright;

    public ConvertRequest(
            String backendName, String path, String[] extensions,
            Geometry geometry, boolean copyright) {
        this.backendName = backendName;
        this.path = path;
        this.extensions = extensions;
        this.geometry = geometry;
        this.copyright = copyright;
    }

    public String getBackendName() {
        return backendName;
    }

    public String getPath() {
        return path;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public boolean getCopyright() {
        return copyright;
    }
}
