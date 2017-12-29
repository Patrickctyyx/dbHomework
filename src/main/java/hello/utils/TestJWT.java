package hello.utils;

import java.security.SecureRandom;
import java.util.Date;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

public class TestJWT {
    public static void main(String[] args) throws Exception {
        // Generate random 256-bit (32-byte) shared secret
        SecureRandom random = new SecureRandom();
        byte[] sharedSecret = new byte[32];
        System.out.println(new byte[32]);
        System.out.println(sharedSecret);
        random.nextBytes(sharedSecret);


        byte[] sharedSecret2 = new byte[32];
        random.nextBytes(sharedSecret2);
        System.out.println(sharedSecret2);

        // Create HMAC signer
        JWSSigner signer = new MACSigner(sharedSecret);

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("alice")
                .issuer("https://c2id.com")
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();
        System.out.println(new Date().getTime());
        System.out.println(new Date().getTime() + 60 * 1000);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        // Apply the HMAC protection
        signedJWT.sign(signer);

        // Serialize to compact form, produces something like
        // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
        String s = signedJWT.serialize();
        System.out.println(s);

        // On the consumer side, parse the JWS and verify its HMAC
        signedJWT = SignedJWT.parse(s);

        JWSVerifier verifier = new MACVerifier(sharedSecret);

        System.out.println(signedJWT.verify(verifier));

// Retrieve / verify the JWT claims according to the app requirements
        System.out.println("alice:" + signedJWT.getJWTClaimsSet().getSubject());
        System.out.println("https://c2id.com:" + signedJWT.getJWTClaimsSet().getIssuer());
        System.out.println(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
        System.out.println("".length() == 0);
    }
}
