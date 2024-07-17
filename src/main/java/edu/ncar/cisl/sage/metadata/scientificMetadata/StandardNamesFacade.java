package edu.ncar.cisl.sage.metadata.scientificMetadata;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;

public class StandardNamesFacade {

    public StandardNamesFacade() {}

    public List<String> getStandardNames(String filePath) throws NoSuchFileException {

        List<String> standardNames = new ArrayList<>();

        try (NetcdfFile ncFile = NetcdfFile.open(filePath)) {

            ncFile.getVariables().stream()
                    .forEach( var -> {

                        Attribute standardName = var.findAttribute("standard_name");
                        if (standardName != null) {

                            standardNames.add(standardName.getStringValue());
                        }
                    });

        } catch (NoSuchFileException e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw e;

        } catch (Exception e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return standardNames;
    }
}
