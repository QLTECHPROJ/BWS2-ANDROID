package com.brainwellnessspa.resourceModule.Models;

public abstract class ListItem {
    public static final int TYPE_BANNER = 0;
    public static final int TYPE_GENERAL = 1;

    abstract public int getType();
}
