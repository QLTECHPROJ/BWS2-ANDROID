package com.brainwellnessspa.resourceModule.Models;

public class MultiViewModel {
    public static final int TYPE_BANNER =0;
    public static final int TYPE_IMAGE_WITH_TEXT =1;

    public int type;
    public int data;
    public String text;

    public MultiViewModel(int type, String text, int data) {
        this.type=type;
        this.data=data;
        this.text=text;
    }
}
