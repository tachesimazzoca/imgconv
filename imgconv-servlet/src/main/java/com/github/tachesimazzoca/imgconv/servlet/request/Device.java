package com.github.tachesimazzoca.imgconv.servlet.request;

public class Device {
    private final Group group;
    private final int displayWidth;
    private final int displayHeight;
    private final String[] extensions;

    public enum Group {
        OTHER, DOCOMO, AU, SOFTBANK, WILLCOM
    }

    public Device(Group group, int displayWidth, int displayHeight, String[] extensions) {
        this.group = group;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.extensions = extensions;
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

    public String[] getExtensions() {
        return extensions;
    }
}
