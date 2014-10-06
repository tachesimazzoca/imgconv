package com.github.tachesimazzoca.imgconv.servlet.request;

import com.github.tachesimazzoca.imgconv.ConvertOption.Format;

/**
 * The class represents a client device.
 */
public class Device {
    private final Group group;
    private final int displayWidth;
    private final int displayHeight;
    private final Format[] acceptFormats;

    public enum Group {
        /**
         * Unknown device
         */
        OTHER,

        /**
         * DoCoMo
         */
        DOCOMO,

        /**
         * au (KDDI-* UP.Browser)
         */
        AU,

        /**
         * SoftBank / Vodafone / J-Phone
         */
        SOFTBANK,

        /**
         * WILLCOM / DDI-Pocket
         */
        WILLCOM
    }

    /**
     * Creates a new {@code Device} object.
     * 
     * @param group The group of the device
     * @param displayWidth The display width of the device
     * @param displayHeight The display height of the device
     * @param acceptFormats The formats the device can accept
     */
    public Device(Group group, int displayWidth, int displayHeight, Format[] acceptFormats) {
        this.group = group;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.acceptFormats = acceptFormats;
    }

    /**
     * Returns the group.
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Returns the display width.
     */
    public int getDisplayWidth() {
        return displayWidth;
    }

    /**
     * Returns the display height.
     */
    public int getDisplayHeight() {
        return displayHeight;
    }

    /**
     * Returns the formats this device can accept.
     */
    public Format[] getAcceptFormats() {
        return acceptFormats;
    }
}
