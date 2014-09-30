package com.github.tachesimazzoca.imgconv.servlet.request;

import com.github.tachesimazzoca.imgconv.ConvertOption.Format;

public class Device {
    private final Group group;
    private final int displayWidth;
    private final int displayHeight;
    private final Format[] acceptFormats;

    public enum Group {
        OTHER, DOCOMO, AU, SOFTBANK, WILLCOM
    }

    public Device(Group group, int displayWidth, int displayHeight, Format[] acceptFormats) {
        this.group = group;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.acceptFormats = acceptFormats;
    }

    public Group getGroup() {
        return group;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public Format[] getAcceptFormats() {
        return acceptFormats;
    }
}
