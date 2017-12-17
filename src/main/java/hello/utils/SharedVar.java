package hello.utils;

import java.security.SecureRandom;

public class SharedVar {
    private static byte[] sharedSecret;

    public static byte[] getSharedSecret() {
        if (sharedSecret == null) {
            SecureRandom random = new SecureRandom();
            sharedSecret = new byte[32];
            random.nextBytes(sharedSecret);
        }
        return sharedSecret;
    }
}
