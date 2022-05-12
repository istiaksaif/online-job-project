package com.rtapps.moc.Model;

public class SenderModel {

    private String ConstantProvider;
    private String DateFrom;
    private String DateTo;
    private String NameFrom;
    private String NameTo;
    private String codeWordBefore;
    private String id;

    private SenderModel() {
    }


    public String getConstantProvider() {
        return ConstantProvider;
    }

    public String getDateFrom() {
        return DateFrom;
    }

    public String getDateTo() {
        return DateTo;
    }

    public String getNameFrom() {
        return NameFrom;
    }

    public String getNameTo() {
        return NameTo;
    }

    public String getCodeWordBefore() {
        return codeWordBefore;
    }

    public String getId() {
        return id;
    }

    public void setConstantProvider(String provider) {
        this.ConstantProvider = provider;
    }

    public void setDateFrom(String dateFrom) {
        this.DateFrom = dateFrom;
    }

    public void setDateTo(String dateTo) {
        this.DateTo = dateTo;
    }

    public void setNameFrom(String nameFrom) {
        this.NameFrom = nameFrom;
    }

    public void setNameTo(String nameTo) {
        this.NameTo = nameTo;
    }

    public void setCodeWordBefore(String codeWordBefore) {
        this.codeWordBefore = codeWordBefore;
    }

    public void setId(String id) {
        this.id = id;
    }
}
