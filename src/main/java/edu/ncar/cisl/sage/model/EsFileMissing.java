package edu.ncar.cisl.sage.model;

public class EsFileMissing {

    private Boolean missing;
    private String dateMissing;

    public EsFileMissing() {}

    public Boolean getMissing() {
        return missing;
    }

    public void setMissing(Boolean missing) {
        this.missing = missing;
    }

    public String getDateMissing() {
        return dateMissing;
    }

    public void setDateMissing(String dateMissing) {
        this.dateMissing = dateMissing;
    }
}
