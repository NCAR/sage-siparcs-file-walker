package edu.ncar.cisl.sage.identification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Md5CalculatorTest {

    private Md5Calculator calculator;

    @BeforeEach
    public void setup() {

        this.calculator = new Md5Calculator();
    }

    @Test
    public void given_string__when_calculateId__then_md5() {

        assertTrue("a50b27be9f45121b0064e17894edaf90", "/Users/mcnette/Library/Application Support/CallHistoryTransactions");
        assertTrue("05cbe896d5cc9c8d4063581a0a9e8499", "/Users/mcnette/.config/iterm2");
        assertTrue("bf7980bef19c008b93b5677875b5aed4", "/Users/mcnette/.config/containers/containers.conf");
        assertTrue("938c2cc0dcc05f2b68c4287040cfcf71", "frog");
        assertTrue("d41d8cd98f00b204e9800998ecf8427e", "");
        assertTrue("7215ee9c7d9dc229d2921a40e899ec5f", " ");
    }

    private void assertTrue(String expectedMd5, String string) {

        String calculatedMd5 = this.calculator.calculateId(string);

        Assertions.assertEquals(expectedMd5, calculatedMd5);
    }

    @Test
    public void given_null__when_calculatedId__then_what() {

        Assertions.assertThrows(NullPointerException.class, () -> {

            this.calculator.calculateId(null);
        });
    }

}
