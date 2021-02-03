package com.brainwellnessspa.ResourceModule.Models;

public class SegmentResource {
    String resourceId,
            resourceName,
            author,
            masterCategory;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMasterCategory() {
        return masterCategory;
    }

    public void setMasterCategory(String masterCategory) {
        this.masterCategory = masterCategory;
    }
}
