package com.github.tachesimazzoca.imgconv.converter;

import javax.imageio.IIOImage;

import com.github.tachesimazzoca.imgconv.Converter;

public class AnimationConverter implements Converter {
    private int maxImages;

    public AnimationConverter(int maxImages) {
        if (maxImages < 1)
            throw new IllegalArgumentException(
                    "The parameter maxImages must be greater than 0.");
        this.maxImages = maxImages;
    }

    @Override
    public IIOImage[] convert(IIOImage[] images) {
        int n = images.length;
        int step = 1;
        if (n > maxImages) {
            n = maxImages;
            step = images.length / maxImages;
        }
        IIOImage[] imgs = new IIOImage[n];
        for (int i = 0; i < imgs.length; i++) {
            int j = i * step;
            if (j >= images.length)
                j = images.length - 1;
            imgs[i] = new IIOImage(images[j].getRenderedImage(), null, null);
        }
        return imgs;
    }
}
