package org.example.mapreservation.config;

public abstract class CsrfConst {
    public static String headerName = "X-CSRF-TOKEN";
    public static String parameterName = "_csrf";
    public static String sessionAttributeName = "CSRF_TOKEN";
}
