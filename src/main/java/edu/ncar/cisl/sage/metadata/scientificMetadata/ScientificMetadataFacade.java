package edu.ncar.cisl.sage.metadata.scientificMetadata;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ncar.cisl.sage.model.ScientificMetadataVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class ScientificMetadataFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ScientificMetadataFacade.class);
    private static final Logger SM_LOG = LoggerFactory.getLogger("scientific-metadata");

    public ScientificMetadataFacade() {}

    public List<ScientificMetadataVariable> getVariables(String filePath) throws NoSuchFileException {

        List<ScientificMetadataVariable> variables = new ArrayList<>();

        try (NetcdfFile file = NetcdfFile.open(filePath)) {

            file.getVariables().stream()
                    .forEach( var -> {

                        ScientificMetadataVariable variable = new ScientificMetadataVariable();

                        variable.setName(var.getFullName());

                        variable.setStandard_name(getVariableAttribute(var,"standard_name"));
                        variable.setLong_name(getVariableAttribute(var,"long_name"));
                        variable.setShort_name(getVariableAttribute(var,"short_name"));

                        variables.add(variable);
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

        return variables;
    }

    private String getVariableAttribute(Variable var, String attribute) {

        String value = "";
        Attribute attr = var.findAttribute(attribute);
        if (attr != null) {

            value = attr.getStringValue();
        }
        return value;
    }

    public String getGlobalAttributes(String filePath, String field) throws NoSuchFileException {

        final String[] fieldValue = new String[1];

        try (NetcdfFile file = NetcdfFile.open(filePath)) {

            file.getGlobalAttributes().stream()
                    .forEach( attr -> {

                        if(attr.getFullName().equalsIgnoreCase(field)) {

                            fieldValue[0] = attr.getStringValue();
                        }
                    });

            if (SM_LOG.isDebugEnabled()) {

                SM_LOG.debug(String.format("%s Global attributes: %s", filePath, file.getGlobalAttributes().stream().map(Attribute::toString)
                        .collect(Collectors.toList())));
            }

        } catch (NoSuchFileException e) {

            LOG.error(e.getMessage(), e);
            throw e;

        } catch (Exception e) {

            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return fieldValue[0];
    }
}
