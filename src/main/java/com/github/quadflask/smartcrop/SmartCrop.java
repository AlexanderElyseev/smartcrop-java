package com.github.quadflask.smartcrop;

import java.awt.image.BufferedImage;
import java.io.IOException;
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
        CropBuilder cropBuilder = new CropBuilder(options);

        Set<Crop> crops = cropBuilder.build(inputImage);

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
}
