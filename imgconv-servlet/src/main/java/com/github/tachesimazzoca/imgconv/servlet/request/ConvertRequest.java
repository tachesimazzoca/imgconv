package com.github.tachesimazzoca.imgconv.servlet.request;

import com.github.tachesimazzoca.imgconv.ConvertOption;

public class ConvertRequest {
    private final String backendName;
    private final String path;
    private final Device device;
    private final ConvertOption convertOption;
    private final boolean noTransfer;

    public ConvertRequest(String backendName, String path, Device device,
            ConvertOption convertOption, boolean noTransfer) {
        this.backendName = backendName;
        this.path = path;
        this.device = device;
        this.convertOption = convertOption;
        this.noTransfer = noTransfer;
    }

    public String getBackendName() {
        return backendName;
    }

    public String getPath() {
        return path;
    }

    public Device getDevice() {
        return device;
    }

    public ConvertOption getConvertOption() {
        return convertOption;
    }

    public boolean isNoTransfer() {
        return noTransfer;
    }
}
