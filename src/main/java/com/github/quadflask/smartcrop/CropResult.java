package com.github.quadflask.smartcrop;

import java.awt.image.BufferedImage;
import java.util.Set;

public class CropResult {
    public final Crop topCrop;
    public final Score topScore;
    public final Set<Crop> crops;
    public final BufferedImage debugImage;
    public final BufferedImage resultImage;

    CropResult(Crop topCrop, Score topScore, Set<Crop> crops, BufferedImage debugImage, BufferedImage resultImage) {
        this.topCrop = topCrop;
        this.topScore = topScore;
        this.crops = crops;
        this.debugImage = debugImage;
        this.resultImage = resultImage;
    }
}
