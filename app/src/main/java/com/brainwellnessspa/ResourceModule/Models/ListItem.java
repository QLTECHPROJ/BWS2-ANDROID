package com.brainwellnessspa.ResourceModule.Models;

public abstract class ListItem {
    public static final int TYPE_BANNER = 0;
    public static final int TYPE_GENERAL = 1;

    abstract public int getType();
}