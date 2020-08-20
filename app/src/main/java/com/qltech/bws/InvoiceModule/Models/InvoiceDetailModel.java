package com.qltech.bws.InvoiceModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvoiceDetailModel {
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
    public class ResponseData {
        @SerializedName("InvoiceNumber")
        @Expose
        private String invoiceNumber;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("TotalAmount")
        @Expose
        private String totalAmount;
        @SerializedName("TaxAmount")
        @Expose
        private String taxAmount;
        @SerializedName("NetAmount")
        @Expose
        private String netAmount;
        @SerializedName("DiscountedAmount")
        @Expose
        private String discountedAmount;
        @SerializedName("InvoiceTo")
        @Expose
        private String invoiceTo;

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getTaxAmount() {
            return taxAmount;
        }

        public void setTaxAmount(String taxAmount) {
            this.taxAmount = taxAmount;
        }

        public String getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(String netAmount) {
            this.netAmount = netAmount;
        }

        public String getDiscountedAmount() {
            return discountedAmount;
        }

        public void setDiscountedAmount(String discountedAmount) {
            this.discountedAmount = discountedAmount;
        }

        public String getInvoiceTo() {
            return invoiceTo;
        }

        public void setInvoiceTo(String invoiceTo) {
            this.invoiceTo = invoiceTo;
        }
    }
}
