package com.brainwellnessspa.invoiceModule.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class InvoiceListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = null

    open class ResponseData protected constructor(`in`: Parcel) : Parcelable {
        @SerializedName("Appointment")
        @Expose
        var appointment: ArrayList<Appointment?>? = null

        @SerializedName("MemberShip")
        @Expose
        var memberShip: ArrayList<MemberShip?>? = null
        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, i: Int) {
            dest.writeTypedList(appointment)
            dest.writeTypedList(memberShip)
        }

        companion object {
            @JvmField val CREATOR: Creator<ResponseData?> = object : Creator<ResponseData?> {
                override fun createFromParcel(`in`: Parcel): ResponseData {
                    return ResponseData(`in`)
                }

                override fun newArray(size: Int): Array<ResponseData?> {
                    return arrayOfNulls(size)
                }
            }
        }

        init {
            appointment = `in`.createTypedArrayList(Appointment.CREATOR)
            memberShip = `in`.createTypedArrayList(MemberShip.CREATOR)
        }
    }

    open class Appointment protected constructor(`in`: Parcel) : Parcelable {
        @SerializedName("InvoiceId")
        @Expose
        var invoiceId: String? = `in`.readString()

        @SerializedName("InvoiceNumber")
        @Expose
        var invoiceNumber: String? = `in`.readString()

        @SerializedName("Email")
        @Expose
        var email: String? = `in`.readString()

        @SerializedName("Name")
        @Expose
        var name: String? = `in`.readString()

        @SerializedName("NetAmount")
        @Expose
        var netAmount: String? = `in`.readString()

        @SerializedName("Interval")
        @Expose
        var interval: String? = `in`.readString()

        @SerializedName("Status")
        @Expose
        var status: String? = `in`.readString()

        @SerializedName("InvoiceUrl")
        @Expose
        var invoiceUrl: String? = `in`.readString()

        @SerializedName("Amount")
        @Expose
        var amount: String? = `in`.readString()

        @SerializedName("Date")
        @Expose
        var date: String? = `in`.readString()

        @SerializedName("InvoicePdf")
        @Expose
        var invoicePdf: String? = `in`.readString()

        @SerializedName("InvoicePdfV")
        @Expose
        var invoicePdfV: String? = `in`.readString()

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, i: Int) {
            dest.writeString(invoiceId)
            dest.writeString(invoiceNumber)
            dest.writeString(email)
            dest.writeString(name)
            dest.writeString(interval)
            dest.writeString(status)
            dest.writeString(invoiceUrl)
            dest.writeString(amount)
            dest.writeString(date)
            dest.writeString(invoicePdf)
            dest.writeString(invoicePdfV)
            dest.writeString(netAmount)
        }

        companion object {
            @JvmField val CREATOR: Creator<Appointment?> = object : Creator<Appointment?> {
                override fun createFromParcel(`in`: Parcel): Appointment {
                    return Appointment(`in`)
                }

                override fun newArray(size: Int): Array<Appointment?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }

    open class MemberShip protected constructor(`in`: Parcel) : Parcelable {
        @SerializedName("InvoiceId")
        @Expose
        var invoiceId: String? = `in`.readString()

        @SerializedName("Email")
        @Expose
        var email: String? = `in`.readString()

        @SerializedName("Name")
        @Expose
        var name: String? = `in`.readString()

        @SerializedName("Interval")
        @Expose
        var interval: String? = `in`.readString()

        @SerializedName("Status")
        @Expose
        var status: String? = `in`.readString()

        @SerializedName("InvoiceUrl")
        @Expose
        var invoiceUrl: String? = `in`.readString()

        @SerializedName("Amount")
        @Expose
        var amount: String? = `in`.readString()

        @SerializedName("Date")
        @Expose
        var date: String? = `in`.readString()

        @SerializedName("InvoicePdf")
        @Expose
        var invoicePdf: String? = `in`.readString()

        @SerializedName("InvoicePdfV")
        @Expose
        var invoicePdfV: String? = `in`.readString()

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, i: Int) {
            dest.writeString(invoiceId)
            dest.writeString(email)
            dest.writeString(name)
            dest.writeString(interval)
            dest.writeString(status)
            dest.writeString(invoiceUrl)
            dest.writeString(amount)
            dest.writeString(date)
            dest.writeString(invoicePdf)
            dest.writeString(invoicePdfV)
        }

        companion object {
            @JvmField val CREATOR: Creator<MemberShip?> = object : Creator<MemberShip?> {
                override fun createFromParcel(`in`: Parcel): MemberShip {
                    return MemberShip(`in`)
                }

                override fun newArray(size: Int): Array<MemberShip?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }
}