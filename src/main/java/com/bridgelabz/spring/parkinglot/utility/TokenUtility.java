package com.bridgelabz.spring.parkinglot.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

public class TokenUtility {

    /**
     * this method is used to create a jwt token.
     */
    public static String getToken(Long userId) {
        //The JWT signature algorithm we will be using to sign the token
        Algorithm algorithm = Algorithm.HMAC256("minho");
        return JWT.create().withClaim("userId", userId).sign(algorithm);
    }

    public static Long decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Verification verification = JWT.require(Algorithm.HMAC256("minho"));
        JWTVerifier jwtVerifier = verification.build();
        //to decode token
        DecodedJWT decodedJwt = jwtVerifier.verify(jwt);
        //retrieve data
        Claim claim = decodedJwt.getClaim("userId");
        Long userid = claim.asLong();
        return userid;

    }

}

