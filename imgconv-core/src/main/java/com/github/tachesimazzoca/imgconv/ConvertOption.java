package com.github.tachesimazzoca.imgconv;

import java.util.Set;
import java.util.TreeSet;

/**
 * Represents convert options for the {@link ImageUtils#convert
 * ImageUtils#convert} method.
 * 
 * <pre>
 * 
 * ConvertOption option = ConvertOption.builder()
 *         .format(ConvertOption.Format.JPEG)
 *         .geometry(new Geometry(240, Geometry.NO_VALUE, Geometry.ScalingStrategy.MAXIMUM))
 *         .flag(ConvertOption.Flag.STRIP)
 *         .build();
 * </pre>
 * 
 * @see ImageUtils#convert
 */
public class ConvertOption {
    private final Format format;
    private final Geometry geometry;
    private final Set<Flag> flags;

    /**
     * Represents the output file format.
     */
    public enum Format {
        /**
         * Represents the JPEG format.
         */
        JPEG("jpeg"),

        /**
         * Represents the PNG (Portable Network Graphics) format.
         */
        PNG("png"),

        /**
         * Represents the GIF (Graphics Interchange Format) format.
         */
        GIF("gif");

        private String formatName;

        private Format(String formatName) {
            this.formatName = formatName;
        }

        /**
         * Returns the corresponding ImageIO format name.
         */
        public String getFormatName() {
            return formatName;
        }
    }

    /**
     * Represents a boolean option.
     */
    public enum Flag {
        /**
         * Represents a flag to strip all profiles and comments.
         */
        STRIP;
    }

    private ConvertOption(Format format, Geometry geometry, Set<Flag> flags) {
        this.format = format;
        this.geometry = geometry;
        this.flags = flags;
    }

    /**
     * Creates a new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the output format.
     * 
     * @throws java.lang.NullPointerException if it's null.
     */
    public Format getFormat() {
        if (format == null)
            throw new NullPointerException("The property format is null.");
        return format;
    }

    /**
     * Returns <code>true</code> if the output format is specified.
     */
    public boolean hasFormat() {
        return format != null;
    }

    /**
     * Returns the geometry option.
     * 
     * @throws java.lang.NullPointerException if it's null.
     */
    public Geometry getGeometry() {
        if (geometry == null)
            throw new NullPointerException("The property geometry is null.");
        return geometry;
    }

    /**
     * Returns <code>true</code> if the geometry option is specified.
     */
    public boolean hasGeometry() {
        return geometry != null;
    }

    /**
     * Returns <code>true</code> if the flags is enabled.
     */
    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }

    /**
     * A class used to build {@link ConvertOption} instances.
     */
    public static class Builder {
        private Format format = null;
        private Geometry geometry = null;
        private Set<Flag> flags = new TreeSet<Flag>();

        private Builder() {
        }

        /**
         * Returns a newly-created <code>ConvertOption</code> object.
         */
        public ConvertOption build() {
            return new ConvertOption(format, geometry, flags);
        }

        /**
         * Set the output format on the builder.
         * 
         * @return the updated builder.
         */
        public Builder format(Format format) {
            this.format = format;
            return this;
        }

        /**
         * Set the geometry option on the builder.
         * 
         * @return the updated builder.
         */
        public Builder geometry(Geometry geometry) {
            this.geometry = geometry;
            return this;
        }

        /**
         * Add the flag on the builder.
         * 
         * @return the updated builder.
         */
        public Builder flag(Flag flag) {
            flags.add(flag);
            return this;
        }

        /**
         * Replace all flags on the builder.
         * 
         * @return the updated builder.
         */
        public Builder flags(Set<Flag> flags) {
            this.flags = flags;
            return this;
        }
    }
}
