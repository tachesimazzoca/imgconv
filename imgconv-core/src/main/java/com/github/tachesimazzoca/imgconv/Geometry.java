package com.github.tachesimazzoca.imgconv;

import java.awt.Dimension;

/**
 * Represents the preferred size of the image.
 */
public class Geometry {
    /**
     * The no-value.
     */
    public static final int NO_VALUE = 0;

    private final int width;
    private final int height;
    private final ScalingStrategy scalingStrategy;

    /**
     * Creates a new {@link Geometry} object with the specified width and
     * height.
     */
    public Geometry(int width, int height) {
        this(width, height, ScalingStrategy.MAXIMUM);
    }

    /**
     * Creates a new {@link Geometry} object with the specified width, height
     * and {@link ScalingStrategy}.
     */
    public Geometry(int width, int height, ScalingStrategy scalingStrategy) {
        if (width != NO_VALUE && width < 1)
            throw new IllegalArgumentException(
                    "The parameter width must be equal or greater than 0.");
        if (height != NO_VALUE && height < 1)
            throw new IllegalArgumentException(
                    "The parameter height must be equals or greater than 0.");
        this.width = width;
        this.height = height;
        this.scalingStrategy = scalingStrategy;
    }

    /**
     * Available strategies to scale an image to the desired width and height.
     */
    public enum ScalingStrategy {
        /**
         * Width and height emphatically given, original aspect ratio ignored.
         */
        EMPHATIC(new Scalable() {
            public Dimension scale(int boundaryW, int boundaryH, int sourceW, int sourceH) {
                int w = (boundaryW == NO_VALUE) ? sourceW : boundaryW;
                int h = (boundaryH == NO_VALUE) ? sourceH : boundaryH;
                return new Dimension(w, h);
            }
        }),

        /**
         * Maximum values of height and width given, aspect ratio preserved.
         */
        MAXIMUM(new Scalable() {
            public Dimension scale(int boundaryW, int boundaryH, int sourceW, int sourceH) {
                int gw = (boundaryW == NO_VALUE) ? sourceW : boundaryW;
                int gh = (boundaryH == NO_VALUE) ? (sourceH * gw / sourceW) : boundaryH;
                int w = sourceW;
                int h = sourceH;
                if (w > gw) {
                    w = gw;
                    h = sourceH * w / sourceW;
                }
                if (h > gh) {
                    h = gh;
                    w = sourceW * h / sourceH;
                }
                return new Dimension(w, h);
            }
        }),

        /**
         * Minimum values of width and height given, aspect ratio preserved.
         */
        MINIMUM(new Scalable() {
            public Dimension scale(int boundaryW, int boundaryH, int sourceW, int sourceH) {
                int gw = (boundaryW == NO_VALUE) ? sourceW : boundaryW;
                int gh = (boundaryH == NO_VALUE) ? (sourceH * gw / sourceW) : boundaryH;
                int w = sourceW;
                int h = sourceH;
                if (w < gw) {
                    w = gw;
                    h = sourceH * w / sourceW;
                }
                if (h < gh) {
                    h = gh;
                    w = sourceW * h / sourceH;
                }
                return new Dimension(w, h);
            }
        });

        private Scalable strategy;

        private ScalingStrategy(Scalable strategy) {
            this.strategy = strategy;
        }

        private Dimension scale(int boundaryW, int boundaryH, int sourceW, int sourceH) {
            return strategy.scale(boundaryW, boundaryH, sourceW, sourceH);
        }
    }

    private interface Scalable {
        public Dimension scale(int boundaryW, int boundaryH, int sourceW, int sourceH);
    }

    /**
     * Returns an {@link Dimension}.
     * 
     * @param w width of the image
     * @param h height of the image
     * @return resized {@link Dimension}
     * @throws IllegalArgumentException if invalid argument supplied.
     */
    public Dimension scale(int w, int h) {
        if (w < 1)
            throw new IllegalArgumentException(
                    "The parameter sourceW must be greater than 0.");
        if (h < 1)
            throw new IllegalArgumentException(
                    "The parameter sourceH must be greater than 0.");
        return scalingStrategy.scale(width, height, w, h);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + ((scalingStrategy == null) ? 0 : scalingStrategy.hashCode());
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Geometry other = (Geometry) obj;
        if (height != other.height)
            return false;
        if (scalingStrategy != other.scalingStrategy)
            return false;
        if (width != other.width)
            return false;
        return true;
    }
}
