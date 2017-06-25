package com.github.quadflask.smartcrop;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CropBuilder {
    private final Options options;

    public CropBuilder(Options options) {
        this.options = options;
    }

    public Set<Crop> build(Image input) {
        Map<Crop, Boolean> crops = new LinkedHashMap<>();

        int iw = input.getWidth();
        int ih = input.getHeight();

        // Use square crop if not set
        // TODO: scale by input
        Integer cw = options.getCropWidth();
        Integer ch = options.getCropHeight();
        if (cw == null && ch == null) {
            cw = Math.min(iw, ih);
            ch = cw;
            options.cropWidth(cw).cropHeight(ch);
        } else if (cw == null) {
            cw = ch;                // TODO: scale by input?
            options.cropWidth(cw);
        } else if (ch == null) {
            ch = cw;                // TODO: scale by input?
            options.cropHeight(ch);
        }

        float maxInputScale = options.getMaxInputScale();
        float minInputScale = options.getMinInputScale();
        float inputScaleStep = options.getInputScaleStep();

        int samplesCount = options.getScoreDownSample();

        for (float inputScale = maxInputScale; inputScale >= minInputScale; inputScale -= inputScaleStep) {
            float cropScale = inputScale * Math.min(iw / cw, ih / ch);

            if (cropScale < options.getMinCropScale())
                continue;

            int sw = (int) Math.ceil(cw * cropScale);
            int sh = (int) Math.ceil(ch * cropScale);

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
