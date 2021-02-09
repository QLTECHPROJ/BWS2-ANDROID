package com.brainwellnessspa.ReferralModule.Model;

import com.brainwellnessspa.ReferralModule.Model.ContactlistModel;

public class FavContactlistModel implements Comparable<ContactlistModel> {
    private String ContactImage;
    private String ContactName;
    private String ContactNumber;
    private boolean checked;

    public String getContactImage() {
        return ContactImage;
    }

    public void setContactImage(String contactImage) {
        ContactImage = contactImage;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "FavContactlistModel{" +
                "ContactImage='" + ContactImage + '\'' +
                ", ContactName='" + ContactName + '\'' +
                ", ContactNumber='" + ContactNumber + '\'' +
                ", checked=" + checked +
                '}';
    }

    @Override
    public int compareTo(ContactlistModel o) {
        return 0;
    }
}
