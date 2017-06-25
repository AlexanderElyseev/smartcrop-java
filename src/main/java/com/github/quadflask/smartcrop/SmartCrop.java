package com.github.quadflask.smartcrop;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

        Set<Crop> crops = crops(inputImage);

        Stream<Map.Entry<Crop, Score>> results = crops
            .stream()
            .parallel()
            .collect(Collectors.toMap(crop -> crop, inputImage::score))
            .entrySet()
            .stream()
            .sorted((f1, f2) -> Float.compare(f2.getValue().total, f1.getValue().total));

        Optional<Map.Entry<Crop, Score>> first = results.findFirst();
        if (!first.isPresent())
            return null;

        Crop topCrop = first.get().getKey();
        Score topScore = first.get().getValue();

        return new CropResult(topCrop, topScore, crops, inputImage.getScoreImage(topCrop), inputImage.getCroppedImage(topCrop));
    }

    private Set<Crop> crops(Image input) {
        Map<Crop, Boolean> crops = new LinkedHashMap<>();

        int cw = options.getCropWidth();
        int ch = options.getCropHeight();

        int iw = input.getWidth();
        int ih = input.getHeight();

        float maxScale = options.getMaxScale();
        float minScale = options.getMinScale();
        float scaleStep = options.getScaleStep();

        int samplesCount = options.getScoreDownSample();

        for (float scale = maxScale; scale >= minScale; scale -= scaleStep) {
            float s = cw > ch ? scale * iw / cw : scale * ih / ch;

            int sw = (int) Math.ceil(cw * s);
            int sh = (int) Math.ceil(ch * s);

            int wstep = (iw - sw) / samplesCount;
            int hstep = (ih - sh) / samplesCount;

            if (wstep < 0 || hstep < 0)
                continue;

            for (int i = 0; i < samplesCount; i++) {
                int y = i * hstep;
                for (int j = 0; j < samplesCount; j++) {
                    int x = j * wstep;
                    crops.putIfAbsent(new Crop(x, y, sw, sh), true);
                }
            }
        }

        return crops.keySet();
    }
}
