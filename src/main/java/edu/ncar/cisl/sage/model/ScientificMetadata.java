package edu.ncar.cisl.sage.model;

import java.util.List;

public class ScientificMetadata {

    private List<ScientificMetadataVariable> variables;
    private String contact;
    private String author;

    public ScientificMetadata() {}

    public List<ScientificMetadataVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<ScientificMetadataVariable> variables) {
        this.variables = variables;
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
