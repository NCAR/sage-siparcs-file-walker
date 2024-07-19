package edu.ncar.cisl.sage.metadata.scientificMetadata;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class StandardNameFacade {

    private static final Logger LOG = LoggerFactory.getLogger(StandardNameFacade.class);
    private static final Logger SM_LOG = LoggerFactory.getLogger("scientific-metadata");

    public StandardNameFacade() {}

    public List<String> getStandardNames(String filePath) throws NoSuchFileException {

        List<String> standardNames = new ArrayList<>();

        try (NetcdfFile file = NetcdfFile.open(filePath)) {

            file.getVariables().stream()
                    .forEach( var -> {

                        Attribute standardName = var.findAttribute("standard_name");
                        if (standardName != null) {

                            standardNames.add(standardName.getStringValue());
                        }
                    });

            if (SM_LOG.isDebugEnabled()) {

                SM_LOG.debug(String.format("%s Variables: %s", filePath, file.getVariables().stream().map(Variable::toString)
                        .collect(Collectors.toList())));
            }

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
