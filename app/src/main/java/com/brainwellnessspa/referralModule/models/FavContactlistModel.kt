package com.brainwellnessspa.referralModule.models

class FavContactlistModel : Comparable<ContactlistModel?> {
    var contactImage: String? = null
    var contactName: String? = null
    var contactNumber: String? = null
    var isChecked = false
    override fun toString(): String {
        return "FavContactlistModel{" +
                "ContactImage='" + contactImage + '\'' +
                ", ContactName='" + contactName + '\'' +
                ", ContactNumber='" + contactNumber + '\'' +
                ", checked=" + isChecked +
                '}'
    }

    override fun compareTo(other: ContactlistModel?): Int {
        return 0
    }
}