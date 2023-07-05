package edu.ncar.cisl.sage.identification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class md5CalculatorTest {

    @Test
    @DisplayName("DirectoryErrored Checksum Id")
    public void calculateDirectoryErroredId() {

        md5Calculator calculator = new md5Calculator();

        String checksum = calculator.calculateId(Path.of("/Users/mcnette/Library/Application Support/CallHistoryTransactions").toString());

        Assertions.assertEquals("a50b27be9f45121b0064e17894edaf90", checksum);
    }

    @Test
    @DisplayName("DirectoryFound Checksum Id")
    public void calculateDirectoryFoundId() {

        md5Calculator calculator = new md5Calculator();

        String checksum = calculator.calculateId(Path.of("/Users/mcnette/.config/iterm2").toString());

        Assertions.assertEquals("05cbe896d5cc9c8d4063581a0a9e8499", checksum);
    }

    //FileErrored is not present for testing

    @Test
    @DisplayName("FileFound Checksum Id")
    public void calculateFileFoundId() {

        md5Calculator calculator = new md5Calculator();

        String checksum = calculator.calculateId(Path.of("/Users/mcnette/.config/containers/containers.conf").toString());

        Assertions.assertEquals("bf7980bef19c008b93b5677875b5aed4", checksum);
    }
}
