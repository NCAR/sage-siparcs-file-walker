package edu.ncar.cisl.sage.model;

import java.util.List;

public class ScientificMetadata {

    List<String> standard_name;
    String contact;
    String author;

    public ScientificMetadata() {}

    public List<String> getStandard_name() {
        return standard_name;
    }

    public void setStandard_name(List<String> standard_name) {
        this.standard_name = standard_name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
