//Not Being Used - Wish to Preserve Code for Future Use
//package edu.ncar.cisl.sage.mediator;
//
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class BasicDateTimeFormatterTest {
//
//    @Test
//    public void given_zoned_date_times__when_format__assert_true() {
//
//        // SETUP
//        DateTimeFormatter f  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        ZonedDateTime zonedDateTime = LocalDateTime.parse("2023-07-12 10:15:30", f).atZone(ZoneId.systemDefault());
//
//        // TEST
//        BasicDateTimeFormatter formatter = new BasicDateTimeFormatter();
//        String value = formatter.format(zonedDateTime);
//
//        // ASSERT
//        assertEquals("20230712T101530.000-0600", value);
//    }
//}