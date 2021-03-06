package com.github.quadflask.smartcrop;

import java.awt.image.BufferedImage;

public class Options {
    public static final Options DEFAULT = new Options();

    private float detailWeight = .2f;

    private float[] skinColor = { 0.7f, 0.57f, 0.44f };
    private float skinBias = .01f;
    private float skinBrightnessMin = 0.2f;
    private float skinBrightnessMax = 1.0f;
    private float skinThreshold = 0.8f;
    private float skinWeight = 1.8f;

    private float saturationBrightnessMin = 0.05f;
    private float saturationBrightnessMax = 0.9f;
    private float saturationThreshold = 0.4f;
    private float saturationBias = 0.2f;
    private float saturationWeight = 0.3f;
    // step * minscale rounded down to the next power of two should be good
    private int scoreDownSample = 8;
    //	private int step = 8;

    // Cropping
    private Integer cropWidth = null;
    private Integer cropHeight = null;

    // Scanning
    private float maxInputScale = 1.0f;
    private float minInputScale = 0.75f;
    private float inputScaleStep = 0.05f;
    private float minCropScale = 1f;

    private float edgeRadius = 0.4f;
    private float edgeWeight = -20f;
    private float outsideImportance = -.5f;
    private boolean ruleOfThirds = false;
    private int bufferedBitmapType = BufferedImage.TYPE_INT_ARGB;

    public Integer getCropWidth() {
        return cropWidth;
    }

    public Options cropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
        return this;
    }

    public Integer getCropHeight() {
        return cropHeight;
    }

    public Options cropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
        return this;
    }

    public float getDetailWeight() {
        return detailWeight;
    }

    public Options detailWeight(float detailWeight) {
        this.detailWeight = detailWeight;
        return this;
    }

    public float[] getSkinColor() {
        return skinColor;
    }

    public Options skinColor(float[] skinColor) {
        this.skinColor = skinColor;
        return this;
    }

    public float getSkinBias() {
        return skinBias;
    }

    public Options skinBias(float skinBias) {
        this.skinBias = skinBias;
        return this;
    }

    public float getSkinBrightnessMin() {
        return skinBrightnessMin;
    }

    public Options skinBrightnessMin(float skinBrightnessMin) {
        this.skinBrightnessMin = skinBrightnessMin;
        return this;
    }

    public float getSkinBrightnessMax() {
        return skinBrightnessMax;
    }

    public Options skinBrightnessMax(float skinBrightnessMax) {
        this.skinBrightnessMax = skinBrightnessMax;
        return this;
    }

    public float getSkinThreshold() {
        return skinThreshold;
    }

    public Options skinThreshold(float skinThreshold) {
        this.skinThreshold = skinThreshold;
        return this;
    }

    public float getSkinWeight() {
        return skinWeight;
    }

    public Options skinWeight(float skinWeight) {
        this.skinWeight = skinWeight;
        return this;
    }

    public float getSaturationBrightnessMin() {
        return saturationBrightnessMin;
    }

    public Options saturationBrightnessMin(float saturationBrightnessMin) {
        this.saturationBrightnessMin = saturationBrightnessMin;
        return this;
    }

    public float getSaturationBrightnessMax() {
        return saturationBrightnessMax;
    }

    public Options saturationBrightnessMax(float saturationBrightnessMax) {
        this.saturationBrightnessMax = saturationBrightnessMax;
        return this;
    }

    public float getSaturationThreshold() {
        return saturationThreshold;
    }

    public Options saturationThreshold(float saturationThreshold) {
        this.saturationThreshold = saturationThreshold;
        return this;
    }

    public float getSaturationBias() {
        return saturationBias;
    }

    public Options saturationBias(float saturationBias) {
        this.saturationBias = saturationBias;
        return this;
    }

    public float getSaturationWeight() {
        return saturationWeight;
    }

    public Options saturationWeight(float saturationWeight) {
        this.saturationWeight = saturationWeight;
        return this;
    }

    public int getScoreDownSample() {
        return scoreDownSample;
    }

    public Options scoreDownSample(int scoreDownSample) {
        this.scoreDownSample = scoreDownSample;
        return this;
    }

    public float getInputScaleStep() {
        return inputScaleStep;
    }

    public Options inputScaleStep(float scaleStep) {
        this.inputScaleStep = scaleStep;
        return this;
    }

    public float getMinInputScale() {
        return minInputScale;
    }

    public Options minInputScale(float minInputScale) {
        this.minInputScale = minInputScale;
        return this;
    }

    public float getMaxInputScale() {
        return maxInputScale;
    }

    public Options maxInputScale(float maxInputScale) {
        this.maxInputScale = maxInputScale;
        return this;
    }

    public float getMinCropScale() {
        return minCropScale;
    }

    public Options minCropScale(float minCropScale) {
        this.minCropScale = minCropScale;
        return this;
    }

    public float getEdgeRadius() {
        return edgeRadius;
    }

    public Options edgeRadius(float edgeRadius) {
        this.edgeRadius = edgeRadius;
        return this;
    }

    public float getEdgeWeight() {
        return edgeWeight;
    }

    public Options edgeWeight(float edgeWeight) {
        this.edgeWeight = edgeWeight;
        return this;
    }

    public float getOutsideImportance() {
        return outsideImportance;
    }

    public Options outsideImportance(float outsideImportance) {
        this.outsideImportance = outsideImportance;
        return this;
    }

    public boolean isRuleOfThirds() {
        return ruleOfThirds;
    }

    public Options ruleOfThirds(boolean ruleOfThirds) {
        this.ruleOfThirds = ruleOfThirds;
        return this;
    }

    public int getBufferedBitmapType() {
        return bufferedBitmapType;
    }

    public Options bufferedBitmapType(int bufferedBitmapType) {
        this.bufferedBitmapType = bufferedBitmapType;
        return this;
    }
}