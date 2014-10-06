package com.github.tachesimazzoca.imgconv.converter;

import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import java.util.Map;
import java.util.HashMap;

import com.github.tachesimazzoca.imgconv.Converter;
import com.github.tachesimazzoca.imgconv.metadata.MetadataNodeHelper;
import com.github.tachesimazzoca.imgconv.metadata.JPEGMetadataNodeHelper;
import com.github.tachesimazzoca.imgconv.metadata.PNGMetadataNodeHelper;
import com.github.tachesimazzoca.imgconv.metadata.GIFMetadataNodeHelper;

/**
 * Sets the copyright comment for Japanese (DoCoMo|au) feature phones.
 * 
 * @see com.github.tachesimazzoca.imgconv.metadata.MetadataNodeHelper
 * @see com.github.tachesimazzoca.imgconv.metadata.JPEGMetadataNodeHelper
 * @see com.github.tachesimazzoca.imgconv.metadata.PNGMetadataNodeHelper
 * @see com.github.tachesimazzoca.imgconv.metadata.GIFMetadataNodeHelper
 */
public class KtaiCopyguardConverter implements Converter {
    private final static String COPYGUARD_COMMENT = "kddi_copyright=on,copy=\"NO\"";

    private final Map<String, MetadataNodeHelper> helpers;

    public KtaiCopyguardConverter() {
        helpers = new HashMap<String, MetadataNodeHelper>();
        helpers.put("javax_imageio_jpeg_image_1.0", new JPEGMetadataNodeHelper());
        helpers.put("javax_imageio_png_1.0", new PNGMetadataNodeHelper("Copyright"));
        helpers.put("javax_imageio_gif_image_1.0", new GIFMetadataNodeHelper());
    }

    @Override
    public IIOImage[] convert(IIOImage[] images) {
        final int N = images.length;
        IIOImage[] imgs = new IIOImage[N];
        for (int i = 0; i < N; i++) {
            imgs[i] = images[i];
        }
        IIOMetadata metadata = imgs[0].getMetadata();
        if (metadata.isReadOnly())
            return imgs;
        String fn = metadata.getNativeMetadataFormatName();
        if (!helpers.containsKey(fn))
            return imgs;
        MetadataNodeHelper helper = helpers.get(fn);
        IIOMetadataNode node = (IIOMetadataNode) metadata.getAsTree(fn);
        helper.setComment(node, COPYGUARD_COMMENT);
        try {
            metadata.setFromTree(fn, node);
        } catch (IIOInvalidTreeException e) {
            throw new IllegalArgumentException(e);
        }
        imgs[0].setMetadata(metadata);
        return imgs;
    }
}
