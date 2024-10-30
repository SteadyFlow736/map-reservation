package org.example.mapreservation.initialization;

public class CsvLineHandler {

    public static String getHairShopName(String[] csvLine) {
        return csvLine[1];
    }

    public static String getRoadAddress(String[] csvLine) {
        return csvLine[2];
    }

    public static String getLongitude(String[] csvLine) {
        return csvLine[3];
    }

    public static String getLatitude(String[] csvLine) {
        return csvLine[4];
    }
}
