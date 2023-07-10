package edu.ncar.cisl.sage.filewalker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoggingFileVisitorTest {

    private LoggingFileVisitor loggingFileVisitor;

    @BeforeEach
    public void setup() {
        this.loggingFileVisitor = new LoggingFileVisitor(null);
    }

    @Test
    public void given_string__when_getExtension__then_extension() {

        assertTrue("conf", "containers.conf");
        assertTrue("ign", "podman-machine-default.ign");
        assertTrue("json2637118634",".tmp-podman-machine-default.json2637118634");
        assertTrue(null, "8b00e831333391a9_0");
        assertTrue(null, " ");
        assertTrue(null, "");
    }

    @Test
    public void given_null__when_getExtension__then_NullException() {

        Assertions.assertThrows(NullPointerException.class, () -> {
            this.loggingFileVisitor.getExtension(null);
        });
    }

    private void assertTrue(String expectedExtension, String pathName) {

        String foundExtension = this.loggingFileVisitor.getExtension(pathName);
        Assertions.assertEquals(expectedExtension, foundExtension);
    }
}