package com.github.tachesimazzoca.imgconv.servlet.request;

import com.github.tachesimazzoca.imgconv.Geometry;

public class ConvertRequest {
    private final String backendName;
    private final String basename;
    private final String extension;
    private final Device device;
    private final Geometry geometry;
    private final boolean copyright;

    public ConvertRequest(
            String backendName, String basename, String extension,
            Device device, Geometry geometry, boolean copyright) {
        this.backendName = backendName;
        this.basename = basename;
        this.extension = extension;
        this.device = device;
        this.geometry = geometry;
        this.copyright = copyright;
    }

    public String getBackendName() {
        return backendName;
    }

    public String getBasename() {
        return basename;
    }

    public String getExtension() {
        return extension;
    }

    public Device getDevice() {
        return device;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public boolean getCopyright() {
        return copyright;
    }
}
