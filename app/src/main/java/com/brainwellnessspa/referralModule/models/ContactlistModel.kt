package com.brainwellnessspa.referralModule.models;

public class ContactlistModel implements Comparable<ContactlistModel> {
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
        return "ContactlistModel{" +
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

/*public class ContactlistModel implements Comparable<ContactlistModel>, Parcelable {
    private String ContactImage;
    private String ContactName;
    private String ContactNumber;
    private boolean checked;

    public static final Creator<ContactlistModel> CREATOR = new Creator<ContactlistModel>() {
        @Override
        public ContactlistModel createFromParcel(Parcel in) {
            return new ContactlistModel(in);
        }

        @Override
        public ContactlistModel[] newArray(int size) {
            return new ContactlistModel[size];
        }
    };

    protected ContactlistModel(Parcel in) {
        ContactImage = in.readString();
        ContactName = in.readString();
        ContactNumber = in.readString();
        checked = in.readBoolean();
    }

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
        return "ContactlistModel{" +
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ContactImage);
        dest.writeString(ContactName);
        dest.writeString(ContactNumber);
        dest.writeBoolean(checked);
    }
}*/

