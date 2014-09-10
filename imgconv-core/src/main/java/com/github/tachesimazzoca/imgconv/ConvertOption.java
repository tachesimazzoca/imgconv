package com.github.tachesimazzoca.imgconv;

import java.util.Set;
import java.util.TreeSet;

public class ConvertOption {
    private final Format format;
    private final Geometry geometry;
    private final Set<Flag> flags;

    public enum Format {
        ANY(""), JPEG("jpeg"), PNG("png"), GIF("gif");

        private String formatName;

        private Format(String formatName) {
            this.formatName = formatName;
        }

        public String getFormatName() {
            return formatName;
        }
    }

    public enum Flag {
        STRIP;
    }

    private ConvertOption(Format format, Geometry geometry, Set<Flag> flags) {
        this.format = format;
        this.geometry = geometry;
        this.flags = flags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Format getFormat() {
        return format;
    }

    public Geometry getGeometry() {
        if (geometry == null)
            throw new NullPointerException("The property geometry is null.");
        return geometry;
    }

    public boolean hasGeometry() {
        return geometry != null;
    }

    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }

    public static class Builder {
        private Format format = null;
        private Geometry geometry = null;
        private Set<Flag> flags = new TreeSet<Flag>();

        private Builder() {
        }

        public ConvertOption build() {
            if (format == null)
                format = Format.ANY;
            return new ConvertOption(format, geometry, flags);
        }

        public Builder format(Format format) {
            this.format = format;
            return this;
        }

        public Builder geometry(Geometry geometry) {
            this.geometry = geometry;
            return this;
        }

        public Builder flag(Flag flag) {
            flags.add(flag);
            return this;
        }

        public Builder flags(Set<Flag> flags) {
            this.flags = flags;
            return this;
        }
    }
}
