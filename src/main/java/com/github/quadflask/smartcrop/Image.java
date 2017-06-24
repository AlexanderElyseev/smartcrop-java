package com.github.quadflask.smartcrop;

import java.awt.image.BufferedImage;

class Image {
    private final int width, height;
    private final int[] data;
    private final Options options;
    private BufferedImage output;
    private Image scoreImage;
    private int[] cd;

    Image(BufferedImage bufferedImage, Options options) {
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();

        this.data = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
        this.options = options;

        scoreImage = new Image(this.width, this.height, options);

        prepareCie();
        edgeDetect(scoreImage);
        skinDetect(scoreImage);
        saturationDetect(scoreImage);


        output = new BufferedImage(this.width, this.height, options.getBufferedBitmapType());
        output.setRGB(0, 0, this.width, this.height, scoreImage.data, 0, this.width);

    }

    Image(int width, int height, Options options) {
        this.width = width;
        this.height = height;
        this.options = options;

        this.data = new int[width * height];
        for (int i = 0; i < this.data.length; i++)
            data[i] = 0xff000000;
    }

    public BufferedImage getScoreImage() {
        return output;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void prepareCie() {
        int[] id = data;
        cd = new int[id.length];
        int w = width;
        int h = height;

        int p;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                p = y * w + x;
                cd[p] = cie(id[p]);
            }
        }
    }

    private int cie(int rgb) {
        int r = rgb >> 16 & 0xff;
        int g = rgb >> 8 & 0xff;
        int b = rgb & 0xff;
        return Math.min(0xff, (int) (0.2126f * b + 0.7152f * g + 0.0722f * r + .5f));
    }

    private void edgeDetect(Image o) {
        int[] od = o.data;
        int w = width;
        int h = height;
        int p;
        int lightness;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                p = y * w + x;
                if (x == 0 || x >= w - 1 || y == 0 || y >= h - 1) {
                    lightness = 0;
                } else {
                    lightness = cd[p] * 8
                        - cd[p - w - 1]
                        - cd[p - w]
                        - cd[p - w + 1]
                        - cd[p - 1]
                        - cd[p + 1]
                        - cd[p + w - 1]
                        - cd[p + w]
                        - cd[p + w + 1]
                    ;
                }

                od[p] = clamp(lightness) << 8 | (od[p] & 0xffff00ff);
            }
        }
    }

    public Score score(Crop crop) {
        Score score = new Score();
        int[] rgb = scoreImage.data;
        int width = scoreImage.width;
        int height = scoreImage.height;

        for (int y = 0; y < crop.height; y++) {
            for (int x = 0; x < crop.width; x++) {
                int p = (y + crop.y) * width + x + crop.x;

                float importance = importance(crop, x, y);
                float detail = (rgb[p] >> 8 & 0xff) / 255f;
                score.skin += (rgb[p] >> 16 & 0xff) / 255f * (detail + options.getSkinBias()) * importance;
                score.detail += detail * importance;
                score.saturation += (rgb[p] & 0xff) / 255f * (detail + options.getSaturationBias()) * importance;
            }
        }

        score.total = (score.detail * options.getDetailWeight()
            + score.skin * options.getSkinWeight()
            + score.saturation * options.getSaturationWeight()) / crop.width / crop.height;

        return score;
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(v, 0xff));
    }

    private void skinDetect(Image o) {
        int[] od = o.data;
        int w = width;
        int h = height;
        float invSkinThreshold = 255f / (1 - options.getSkinThreshold());

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = y * w + x;
                float lightness = cd[p] / 255f;
                float skin = calcSkinColor(data[p]);
                if (skin > options.getSkinThreshold() && lightness >= options.getSkinBrightnessMin() && lightness <= options.getSkinBrightnessMax()) {
                    od[p] = ((Math.round((skin - options.getSkinThreshold()) * invSkinThreshold)) & 0xff) << 16 | (od[p] & 0xff00ffff);
                } else {
                    od[p] &= 0xff00ffff;
                }
            }
        }
    }

    private float importance(Crop crop, int x, int y) {
        if (crop.x > x || x >= crop.x + crop.width || crop.y > y || y >= crop.y + crop.height)
            return options.getOutsideImportance();

        float fx = (float) (x - crop.x) / crop.width;
        float fy = (float) (y - crop.y) / crop.height;
        float px = Math.abs(0.5f - fx) * 2;
        float py = Math.abs(0.5f - fy) * 2;

        // distance from edg;
        float dx = Math.max(px - 1.0f + options.getEdgeRadius(), 0);
        float dy = Math.max(py - 1.0f + options.getEdgeRadius(), 0);
        float d = (dx * dx + dy * dy) * options.getEdgeWeight();
        d += (float) (1.4142135f - Math.sqrt(px * px + py * py));
        if (options.isRuleOfThirds()) {
            d += (Math.max(0, d + 0.5f) * 1.2f) * (thirds(px) + thirds(py));
        }
        return d;
    }

    /**
     * Gets value in the range of [0, 1] where 0 is the center of the pictures.
     *
     * @param x
     * @return weight of rule of thirds [0, 1]
     */
    private float thirds(float x) {
        x = ((x - (1 / 3f) + 1.0f) % 2.0f * 0.5f - 0.5f) * 16f;
        return Math.max(1.0f - x * x, 0);
    }

    private void saturationDetect(Image o) {
        int[] od = o.data;
        int w = width;
        int h = height;
        float invSaturationThreshold = 255f / (1 - options.getSaturationThreshold());

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = y * w + x;
                float lightness = cd[p] / 255f;
                float sat = saturation(data[p]);
                if (sat > options.getSaturationThreshold() && lightness >= options.getSaturationBrightnessMin() && lightness <= options.getSaturationBrightnessMax()) {
                    od[p] = (Math.round((sat - options.getSaturationThreshold()) * invSaturationThreshold) & 0xff) | (od[p] & 0xffffff00);
                } else {
                    od[p] &= 0xffffff00;
                }
            }
        }
    }

    private float calcSkinColor(int rgb) {
        int r = rgb >> 16 & 0xff;
        int g = rgb >> 8 & 0xff;
        int b = rgb & 0xff;

        float mag = (float) Math.sqrt(r * r + g * g + b * b);
        float rd = (r / mag - options.getSkinColor()[0]);
        float gd = (g / mag - options.getSkinColor()[1]);
        float bd = (b / mag - options.getSkinColor()[2]);
        return 1f - (float) Math.sqrt(rd * rd + gd * gd + bd * bd);
    }

    private float saturation(int rgb) {
        float r = (rgb >> 16 & 0xff) / 255f;
        float g = (rgb >> 8 & 0xff) / 255f;
        float b = (rgb & 0xff) / 255f;

        float maximum = Math.max(r, Math.max(g, b));
        float minimum = Math.min(r, Math.min(g, b));
        if (maximum == minimum) {
            return 0;
        }
        float l = (maximum + minimum) / 2f;
        float d = maximum - minimum;
        return l > 0.5f ? d / (2f - maximum - minimum) : d / (maximum + minimum);
    }
}
