package com.github.tachesimazzoca.imgconv.servlet.request;

import com.github.tachesimazzoca.imgconv.ConvertOption;

/**
 * The class represents a request to convert an image resource.
 */
public class ConvertRequest {
    private final String backendName;
    private final String path;
    private final Device device;
    private final ConvertOption convertOption;
    private final boolean noTransfer;

    /**
     * Creates a new {@code ConvertRequest} object.
     * 
     * @param backendName The name of the contents provider
     * @param path The relative URL to the image resource
     * @param device The client device
     * @param convertOption The convert option
     * @param noTransfer Whether the resource is provided as a copyrighted
     *        content or not.
     */
    public ConvertRequest(String backendName, String path, Device device,
            ConvertOption convertOption, boolean noTransfer) {
        this.backendName = backendName;
        this.path = path;
        this.device = device;
        this.convertOption = convertOption;
        this.noTransfer = noTransfer;
    }

    /**
     * Returns the name of the contents provider.
     */
    public String getBackendName() {
        return backendName;
    }

    /**
     * Returns the relative URL to the image resource.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the client device.
     */
    public Device getDevice() {
        return device;
    }

    /**
     * Returns the convert option.
     */
    public ConvertOption getConvertOption() {
        return convertOption;
    }

    /**
     * Returns whether the resource is provided as a copyrighted content or not.
     */
    public boolean isNoTransfer() {
        return noTransfer;
    }
}
