package edu.ncar.cisl.sage.model;

public class EsScientificMetadata {

    private String dateScientificMetadataUpdated;
    private ScientificMetadata scientificMetadata;

    public String getDateScientificMetadataUpdated() {
        return dateScientificMetadataUpdated;
    }

    public void setDateScientificMetadataUpdated(String dateScientificMetadataUpdated) {
        this.dateScientificMetadataUpdated = dateScientificMetadataUpdated;
    }

    public ScientificMetadata getScientificMetadata() {
        return scientificMetadata;
    }

    public void setScientificMetadata(ScientificMetadata scientificMetadata) {
        this.scientificMetadata = scientificMetadata;
    }
}
