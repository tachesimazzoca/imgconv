package com.github.tachesimazzoca.imgconv;

import javax.imageio.IIOImage;

public interface Converter {
    /**
     * Converts an array of IIOImage.
     * 
     * @param images
     * @return a converted array of IIOImage
     */
    public IIOImage[] convert(IIOImage[] images);
}
