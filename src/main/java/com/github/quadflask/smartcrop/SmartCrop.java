package com.github.quadflask.smartcrop;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<Crop> crops = crops(inputImage);

        Stream<Map.Entry<Crop, Score>> results = crops
            .stream()
            .parallel()
            .collect(Collectors.toMap(crop -> crop, inputImage::score))
            .entrySet()
            .stream()
            .sorted((f1, f2) -> Float.compare(f2.getValue().total, f1.getValue().total));

        Optional<Map.Entry<Crop, Score>> first = results.findFirst();
        Crop topCrop = first.get().getKey();
        Score topScore = first.get().getValue();

        System.out.printf("TOP (%s): %-20s\n", topCrop.toString(), topScore.toString());

        return CropResult.newInstance(topCrop, crops, inputImage.getScoreImage(topCrop), inputImage.getCroppedImage(topCrop));
    }

    private List<Crop> crops(Image input) {
        List<Crop> crops = new ArrayList<>();

        int cw = options.getCropWidth();
        int ch = options.getCropHeight();

        float maxScale = Math.min(
            options.getMaxScale(),
            cw > ch ? input.getWidth() / cw : input.getHeight() / ch);

        float minScale = options.getMinScale();
        float scaleStep = options.getScaleStep();

        int samplesCount = options.getScoreDownSample();

        for (float scale = maxScale; scale >= minScale; scale -= scaleStep) {
            int w = (int) (cw * scale);
            int h = (int) (ch * scale);

            int wstep = (input.getWidth() - w) / samplesCount;
            int hstep = (input.getHeight() - h) / samplesCount;

            if (wstep < 0 || hstep < 0)
                continue;

            int y = 0;
            do {
                int x = 0;
                do {
                    crops.add(new Crop(x, y, w, h));
                    x += wstep;
                } while (x + w < input.getWidth());
                y += hstep;
            } while (y + h < input.getHeight());
        }

        return crops;
    }
}
