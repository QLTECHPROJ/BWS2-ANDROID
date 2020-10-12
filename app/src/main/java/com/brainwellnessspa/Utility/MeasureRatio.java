package com.brainwellnessspa.Utility;

public class MeasureRatio {
    float widthImg, height, innerMargin;
    float proportion, ratio;

    public MeasureRatio(float width, float height, float ratio, float proportion) {
        this.widthImg = width;
        this.height = height;
        this.ratio = ratio;
        this.proportion = proportion;
    }

    public float getWidthImg() {
        return widthImg;
    }

    public void setWidth(int width) {
        this.widthImg = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public float getProportion() {
        return proportion;
    }

    public void setProportion(float proportion) {
        this.proportion = proportion;
    }
}

