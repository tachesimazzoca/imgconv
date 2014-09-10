package com.github.tachesimazzoca.imgconv.converter;

import javax.imageio.IIOImage;

import com.github.tachesimazzoca.imgconv.Converter;

public class StripConverter implements Converter {
    @Override
    public IIOImage[] convert(IIOImage[] images) {
        IIOImage[] imgs = new IIOImage[images.length];
        for (int i = 0; i < images.length; i++) {
            imgs[i] = new IIOImage(images[i].getRenderedImage(), null, null);
        }
        return imgs;
    }
}
