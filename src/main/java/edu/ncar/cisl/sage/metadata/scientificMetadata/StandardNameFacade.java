package edu.ncar.cisl.sage.metadata.scientificMetadata;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;

public class StandardNameFacade {

    private static final Logger LOG = LoggerFactory.getLogger(StandardNameFacade.class);

    public StandardNameFacade() {}

    public List<String> getStandardNames(String filePath) throws NoSuchFileException {

        List<String> standardNames = new ArrayList<>();

        try (NetcdfFile ncFile = NetcdfFile.open(filePath)) {

            ncFile.getVariables().stream()
                    .forEach( var -> {

                        System.out.println(var);

                        Attribute standardName = var.findAttribute("standard_name");
                        if (standardName != null) {

                            standardNames.add(standardName.getStringValue());
                        }
                    });

        } catch (NoSuchFileException e) {

            LOG.error(e.getMessage(), e);
            throw e;

        } catch (Exception e) {

            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return standardNames;
    }
}