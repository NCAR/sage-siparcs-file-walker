package edu.ncar.cisl.sage.identification;

import java.nio.charset.StandardCharsets;

public class md5Calculator implements IdCalculator {


    @Override
    public String calculateId(String path) {

        StringBuilder checksum = new StringBuilder();
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");

            byte[] array = md.digest(path.getBytes(StandardCharsets.UTF_8));

            for (byte b : array) {

                checksum.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
        } catch (java.security.NoSuchAlgorithmException ignored) {

        }

        return checksum.toString();
    }
}