package com.github.quadflask.smartcrop;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
    private final int width, height;
    private final int[] sourceRgb;
    private final int[] scoreRgb;
    private final int[] sourceCie;
    private final BufferedImage bufferedImage;
    private final Options options;

    public Image(BufferedImage source, Options options) {
        this.width = source.getWidth();
        this.height = source.getHeight();
        this.bufferedImage = source;
        this.options = options;

        this.sourceRgb = source.getRGB(0, 0, width, height, null, 0, width);

        this.scoreRgb = new int[this.sourceRgb.length];
        for (int i = 0; i < this.scoreRgb.length; i++)
            this.scoreRgb[i] = 0xff000000;

        this.sourceCie = new int[this.sourceRgb.length];
        prepareCie();
        edgeDetect();
        skinDetect();
        saturationDetect();
    }

    public BufferedImage getCroppedImage(Crop crop) {
        int tw = options.getCropWidth();
        int th = options.getCropHeight();

        BufferedImage image = new BufferedImage(tw, th, options.getBufferedBitmapType());
        image.getGraphics().drawImage(bufferedImage, 0, 0, tw, th, crop.x, crop.y, crop.x + crop.width, crop.y + crop.height, null);
        return image;
    }

    public BufferedImage getScoreImage(Crop crop) {
        BufferedImage output = new BufferedImage(this.width, this.height, options.getBufferedBitmapType());
        output.setRGB(0, 0, this.width, this.height, scoreRgb, 0, this.width);

        // Draw cropped frame
        Graphics graphics = output.getGraphics();
        graphics.setColor(Color.cyan);
        graphics.drawRect(crop.x, crop.y, crop.width, crop.height);

        return output;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void prepareCie() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = y * width + x;
                int v = sourceRgb[p];

                int r = v >> 16 & 0xff;
                int g = v >> 8 & 0xff;
                int b = v & 0xff;
                int cie = Math.min(0xff, (int) (0.2126f * b + 0.7152f * g + 0.0722f * r + .5f));

                sourceCie[p] = cie;
            }
        }
    }

    private void edgeDetect() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = y * width + x;
                int lightness;
                if (x == 0 || x >= width - 1 || y == 0 || y >= height - 1) {
                    lightness = 0;
                } else {
                    lightness = sourceCie[p] * 8
                        - sourceCie[p - width - 1]
                        - sourceCie[p - width]
                        - sourceCie[p - width + 1]
                        - sourceCie[p - 1]
                        - sourceCie[p + 1]
                        - sourceCie[p + width - 1]
                        - sourceCie[p + width]
                        - sourceCie[p + width + 1]
                    ;
                }

                scoreRgb[p] = clamp(lightness) << 8 | (scoreRgb[p] & 0xffff00ff);
            }
        }
    }

    public Score score(Crop crop) {
        Score score = new Score();

        for (int y = 0; y < crop.height; y++) {
            for (int x = 0; x < crop.width; x++) {
                int p = (y + crop.y) * width + x + crop.x;

                float importance = importance(crop, x, y);
                float detail = (scoreRgb[p] >> 8 & 0xff) / 255f;

                score.skin += (scoreRgb[p] >> 16 & 0xff) / 255f * (detail + options.getSkinBias()) * importance;
                score.detail += detail * importance;
                score.saturation += (scoreRgb[p] & 0xff) / 255f * (detail + options.getSaturationBias()) * importance;
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

    private void skinDetect() {
        int[] od = this.scoreRgb;
        float invSkinThreshold = 255f / (1 - options.getSkinThreshold());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = y * width + x;
                float lightness = sourceCie[p] / 255f;
                float skin = calcSkinColor(sourceRgb[p]);
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

    private void saturationDetect() {
        float invSaturationThreshold = 255f / (1 - options.getSaturationThreshold());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = y * width + x;
                float lightness = sourceCie[p] / 255f;
                float sat = saturation(sourceRgb[p]);
                if (sat > options.getSaturationThreshold() && lightness >= options.getSaturationBrightnessMin() && lightness <= options.getSaturationBrightnessMax()) {
                    this.scoreRgb[p] = (Math.round((sat - options.getSaturationThreshold()) * invSaturationThreshold) & 0xff) | (this.scoreRgb[p] & 0xffffff00);
                } else {
                    this.scoreRgb[p] &= 0xffffff00;
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

    private static float saturation(int rgb) {
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
