package com.github.tachesimazzoca.imgconv;

public class Image {
    private final Format format;
    private final int width;
    private final int height;

    /**
     * Creates a new <code>Image</code> object.
     * 
     * @param format the specified format
     * @param width the specified width
     * @param height the specified height
     */
    public Image(Format format, int width, int height) {
        this.format = format;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the format of this <code>Image</code> object.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Returns the width of this <code>Image</code> object.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of this <code>Image</code> object.
     */
    public int getHeight() {
        return height;
    }

    public enum Format {
        /**
         * JPEG: image/jpeg
         */
        JPEG("javax_imageio_jpeg_image_1.0"),

        /**
         * PNG: image/png
         */
        PNG("javax_imageio_png_1.0"),

        /**
         * GIF: image/gif
         */
        GIF("javax_imageio_gif_image_1.0");

        private String nativeMetadataFormatName;

        private Format(String formatName) {
            this.nativeMetadataFormatName = formatName;
        }

        /**
         * Creates a new <code>Format</code> object with a name of native
         * metadata format.
         * 
         * @param name name of native metadata format
         * @see javax.imageio.metadata.IIOMetadata#getNativeMetadataFormatName
         */
        public static Format fromNativeMetadataFormatName(String name) {
            for (Format t : Format.values()) {
                if (t.nativeMetadataFormatName.equals(name)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("unknown format: " + name);
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %d x %d", format, width, height);
    }
}
