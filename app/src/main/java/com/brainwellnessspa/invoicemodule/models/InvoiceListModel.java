package com.brainwellnessspa.invoicemodule.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InvoiceListModel {
    @SerializedName("ResponseData")
    @Expose
    private ResponseData responseData;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("ResponseStatus")
    @Expose
    private String responseStatus;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public static class ResponseData implements Parcelable {
        @SerializedName("Appointment")
        @Expose
        private ArrayList<Appointment> appointment = null;
        @SerializedName("MemberShip")
        @Expose
        private ArrayList<MemberShip> memberShip = null;

        public static final Creator<ResponseData> CREATOR = new Creator<ResponseData>() {
            @Override
            public ResponseData createFromParcel(Parcel in) {
                return new ResponseData(in);
            }

            @Override
            public ResponseData[] newArray(int size) {
                return new ResponseData[size];
            }
        };

        public ArrayList<Appointment> getAppointment() {
            return appointment;
        }

        public void setAppointment(ArrayList<Appointment> appointment) {
            this.appointment = appointment;
        }

        public ArrayList<MemberShip> getMemberShip() {
            return memberShip;
        }

        public void setMemberShip(ArrayList<MemberShip> memberShip) {
            this.memberShip = memberShip;
        }

        protected ResponseData(Parcel in) {
            appointment = in.createTypedArrayList(Appointment.CREATOR);
            memberShip = in.createTypedArrayList(MemberShip.CREATOR);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeTypedList(appointment);
            dest.writeTypedList(memberShip);
        }
    }

    public static class Appointment implements Parcelable {
        @SerializedName("InvoiceId")
        @Expose
        private String invoiceId;
        @SerializedName("InvoiceNumber")
        @Expose
        private String invoiceNumber;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("NetAmount")
        @Expose
        private String netAmount;
        @SerializedName("Interval")
        @Expose
        private String interval;
        @SerializedName("Status")
        @Expose
        private String status;
        @SerializedName("InvoiceUrl")
        @Expose
        private String invoiceUrl;
        @SerializedName("Amount")
        @Expose
        private String amount;
        @SerializedName("Date")
        @Expose
        private String date;
        @SerializedName("InvoicePdf")
        @Expose
        private String invoicePdf;
        @SerializedName("InvoicePdfV")
        @Expose
        private String invoicePdfV;

        protected Appointment(Parcel in) {
            invoiceId = in.readString();
            invoiceNumber = in.readString();
            email = in.readString();
            name = in.readString();
            interval = in.readString();
            status = in.readString();
            invoiceUrl = in.readString();
            amount = in.readString();
            date = in.readString();
            invoicePdf = in.readString();
            invoicePdfV = in.readString();
            netAmount = in.readString();
        }

        public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
            @Override
            public Appointment createFromParcel(Parcel in) {
                return new Appointment(in);
            }

            @Override
            public Appointment[] newArray(int size) {
                return new Appointment[size];
            }
        };

        public String getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public String getInvoicePdf() {
            return invoicePdf;
        }

        public void setInvoicePdf(String invoicePdf) {
            this.invoicePdf = invoicePdf;
        }

        public String getInvoicePdfV() {
            return invoicePdfV;
        }

        public void setInvoicePdfV(String invoicePdfV) {
            this.invoicePdfV = invoicePdfV;
        }

        public static Creator<Appointment> getCREATOR() {
            return CREATOR;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInvoiceUrl() {
            return invoiceUrl;
        }

        public void setInvoiceUrl(String invoiceUrl) {
            this.invoiceUrl = invoiceUrl;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(String netAmount) {
            this.netAmount = netAmount;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeString(invoiceId);
            dest.writeString(invoiceNumber);
            dest.writeString(email);
            dest.writeString(name);
            dest.writeString(interval);
            dest.writeString(status);
            dest.writeString(invoiceUrl);
            dest.writeString(amount);
            dest.writeString(date);
            dest.writeString(invoicePdf);
            dest.writeString(invoicePdfV);
            dest.writeString(netAmount);
        }
    }

    public static class MemberShip implements Parcelable {
        @SerializedName("InvoiceId")
        @Expose
        private String invoiceId;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Interval")
        @Expose
        private String interval;
        @SerializedName("Status")
        @Expose
        private String status;
        @SerializedName("InvoiceUrl")
        @Expose
        private String invoiceUrl;
        @SerializedName("Amount")
        @Expose
        private String amount;
        @SerializedName("Date")
        @Expose
        private String date;
        @SerializedName("InvoicePdf")
        @Expose
        private String invoicePdf;
        @SerializedName("InvoicePdfV")
        @Expose
        private String invoicePdfV;

        protected MemberShip(Parcel in) {
            invoiceId = in.readString();
            email = in.readString();
            name = in.readString();
            interval = in.readString();
            status = in.readString();
            invoiceUrl = in.readString();
            amount = in.readString();
            date = in.readString();
            invoicePdf = in.readString();
            invoicePdfV = in.readString();
        }

        public static final Creator<MemberShip> CREATOR = new Creator<MemberShip>() {
            @Override
            public MemberShip createFromParcel(Parcel in) {
                return new MemberShip(in);
            }

            @Override
            public MemberShip[] newArray(int size) {
                return new MemberShip[size];
            }
        };

        public String getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
        }

        public static Creator<MemberShip> getCREATOR() {
            return CREATOR;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getInvoicePdf() {
            return invoicePdf;
        }

        public void setInvoicePdf(String invoicePdf) {
            this.invoicePdf = invoicePdf;
        }

        public String getInvoicePdfV() {
            return invoicePdfV;
        }

        public void setInvoicePdfV(String invoicePdfV) {
            this.invoicePdfV = invoicePdfV;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInvoiceUrl() {
            return invoiceUrl;
        }

        public void setInvoiceUrl(String invoiceUrl) {
            this.invoiceUrl = invoiceUrl;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int i) {
            dest.writeString(invoiceId);
            dest.writeString(email);
            dest.writeString(name);
            dest.writeString(interval);
            dest.writeString(status);
            dest.writeString(invoiceUrl);
            dest.writeString(amount);
            dest.writeString(date);
            dest.writeString(invoicePdf);
            dest.writeString(invoicePdfV);
        }
    }
}