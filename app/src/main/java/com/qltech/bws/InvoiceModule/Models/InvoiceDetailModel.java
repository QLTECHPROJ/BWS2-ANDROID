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
        @SerializedName("Qty")
        @Expose
        private String qty;
        @SerializedName("Session")
        @Expose
        private String session;
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
        @SerializedName("InvoiceDate")
        @Expose
        private String invoiceDate;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("CardBrand")
        @Expose
        private String cardBrand;
        @SerializedName("CardDigit")
        @Expose
        private String cardDigit;
        @SerializedName("GSTAmount")
        @Expose
        private String gstAmount;
        @SerializedName("Amount")
        @Expose
        private String amount;
        @SerializedName("InvoiceFrom")
        @Expose
        private String invoiceFrom;

        public String getCardBrand() {
            return cardBrand;
        }

        public void setCardBrand(String cardBrand) {
            this.cardBrand = cardBrand;
        }

        public String getCardDigit() {
            return cardDigit;
        }

        public void setCardDigit(String cardDigit) {
            this.cardDigit = cardDigit;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }

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

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getGstAmount() {
            return gstAmount;
        }

        public void setGstAmount(String gstAmount) {
            this.gstAmount = gstAmount;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getInvoiceFrom() {
            return invoiceFrom;
        }

        public void setInvoiceFrom(String invoiceFrom) {
            this.invoiceFrom = invoiceFrom;
        }

        public String getInvoiceTo() {
            return invoiceTo;
        }

        public void setInvoiceTo(String invoiceTo) {
            this.invoiceTo = invoiceTo;
        }
    }
}
