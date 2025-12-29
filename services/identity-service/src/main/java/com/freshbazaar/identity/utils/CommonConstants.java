package com.freshbazaar.identity.utils;

public final class CommonConstants {

    private CommonConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String SECRET_KEY_PROP = "${jwt.secret}";
    public static final String EXPIRATION_PROP = "${jwt.expiration}";
    public static final long TOKEN_EXPIRATION_TIME = 3600;

}
