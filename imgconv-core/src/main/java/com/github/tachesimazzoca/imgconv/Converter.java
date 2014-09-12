package com.github.tachesimazzoca.imgconv;

import javax.imageio.IIOImage;

/**
 * Instances of classes that implement this interface are used to convert
 * images.
 * 
 * @see javax.imageio.IIOImage
 * @see ImageUtils#convert
 */
public interface Converter {
    /**
     * Returns an array of converted images.
     * 
     * @param images an array of source images
     * @return an array of converted images.
     */
    public IIOImage[] convert(IIOImage[] images);
}
