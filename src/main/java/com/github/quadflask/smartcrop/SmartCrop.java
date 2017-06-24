package com.github.quadflask.smartcrop;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SmartCrop {
    private Options options;

    public SmartCrop() {
        this(Options.DEFAULT);
    }

    public SmartCrop(Options options) {
        this.options = options;
    }

    public CropResult analyze(BufferedImage input) throws IOException {
        Image inputImage = new Image(input, options);

        float topScore = Float.NEGATIVE_INFINITY;
        Crop topCrop = null;
        List<Crop> crops = crops(inputImage);

        List<Float> scores = crops.stream()
            .parallel()
            .map(crop -> inputImage.score(crop).total)
            .sorted((f1, f2) -> Float.compare(f2, f1))
            .collect(Collectors.toList());

        int i = 0;
        for (Crop crop : crops) {
            crop.score = inputImage.score(crop);
            if (crop.score.total > topScore) {
                topCrop = crop;
                topScore = crop.score.total;
            }
//
////            BufferedImage img = new BufferedImage(crop.width, crop.height, options.getBufferedBitmapType());
////            img.getGraphics().drawImage(input, 0, 0, crop.width, crop.height, crop.x, crop.y, crop.x + crop.width, crop.y + crop.height, null);
////            ImageIO.write(img, "png", new File("/Users/alexandereliseev/Desktop/1/debug" + i++ + ".png"));
            System.out.printf("%d / %d : %f\t%f\t%f\t%f\n", i, crops.size(), crop.score.detail, crop.score.saturation, crop.score.skin, crop.score.total);
            i++;
////            crop.x *= options.getScoreDownSample();
////            crop.y *= options.getScoreDownSample();
////            crop.width *= options.getScoreDownSample();
////            crop.height *= options.getScoreDownSample();
        }

        CropResult result = CropResult.newInstance(topCrop, crops, inputImage.getScoreImage(), createCrop(input, topCrop));

        Graphics graphics = inputImage.getScoreImage().getGraphics();
        graphics.setColor(Color.cyan);
        if (topCrop != null)
            graphics.drawRect(topCrop.x, topCrop.y, topCrop.width, topCrop.height);

        return result;
    }

    private BufferedImage createCrop(BufferedImage input, Crop crop) {
        int tw = options.getCropWidth();
        int th = options.getCropHeight();

        BufferedImage image = new BufferedImage(tw, th, options.getBufferedBitmapType());
        image.getGraphics().drawImage(input, 0, 0, tw, th, crop.x, crop.y, crop.x + crop.width, crop.y + crop.height, null);
        return image;
    }

    private List<Crop> crops(Image input) {
        List<Crop> crops = new ArrayList<>();

        for (float scale = options.getMaxScale(); scale >= options.getMinScale(); scale -= options.getScaleStep()) {
            int w = (int) (options.getCropWidth() * scale);
            int h = (int) (options.getCropHeight() * scale);

            int wstep = (input.getWidth() - w) / options.getScoreDownSample();
            int hstep = (input.getHeight() - h) / options.getScoreDownSample();

            if (wstep <= 0 || hstep <= 0)
                continue;

            for (int y = 0; y + h <= input.getHeight(); y += hstep) {
                for (int x = 0; x + w <= input.getWidth(); x += wstep) {
                    crops.add(new Crop(x, y, w, h));
                }
            }
        }

        return crops;
    }
}
