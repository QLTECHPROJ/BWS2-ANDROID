package com.brainwellnessspa.InvoiceModule.Models;

public class SegmentMembership {
    String invoiceId,
            invoiceAmount,
            invoiceDate,
            invoiceCurrency,
            plan,
            planStartDt,
            planExpiryDt;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceCurrency() {
        return invoiceCurrency;
    }

    public void setInvoiceCurrency(String invoiceCurrency) {
        this.invoiceCurrency = invoiceCurrency;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPlanStartDt() {
        return planStartDt;
    }

    public void setPlanStartDt(String planStartDt) {
        this.planStartDt = planStartDt;
    }

    public String getPlanExpiryDt() {
        return planExpiryDt;
    }

    public void setPlanExpiryDt(String planExpiryDt) {
        this.planExpiryDt = planExpiryDt;
    }
}
