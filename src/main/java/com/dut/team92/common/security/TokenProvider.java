package com.dut.team92.common.security;

import com.dut.team92.common.constant.CommonConstant;
import com.dut.team92.common.exception.InvalidTokenHeader;
import com.dut.team92.common.exception.LoadKeyStoreTokenException;
import com.dut.team92.common.exception.RetrievePrivateKeyException;
import com.dut.team92.common.exception.RetrievePublicKeyException;
import com.dut.team92.common.security.model.CustomUserPrincipal;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {
    private static final String KEY_RSA ="test_auth";
    private KeyStore keyStore;

    @Value("${keystore.password}")
    private String keyStorePassword;

    @PostConstruct
    public void init() {
        try {
            // load keystore RSA
            this.keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/auth.jks");
            keyStore.load(resourceAsStream, keyStorePassword.toCharArray());
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new LoadKeyStoreTokenException(e.getMessage());
        }
    }

    /**
     * Retrieve PrivateKey RSA generating certificate from keytool
     * */
    public PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(KEY_RSA, this.keyStorePassword.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error("Occurred while retrieving public key from keystore: {}", e.getMessage());
            throw new RetrievePrivateKeyException(e.getMessage());
        }
    }

    /**
     * Retrieve PublicKey RSA generating certificate from keytool
     * */
    public PublicKey getPublicKey() {
        try {
            Certificate certificate = keyStore.getCertificate(KEY_RSA);
            return certificate.getPublicKey();
        } catch (KeyStoreException e) {
            throw new RetrievePublicKeyException(e.getMessage());
        }
    }

    /**
     * validate token
     *
     * @param token Token retrieved from client send to server
     */
    public boolean validateToken(String token) {
        try {
            // check token valid
            Jwts.parser().setSigningKey(getPrivateKey()).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
            throw new InvalidTokenHeader("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.error("JWT Token is expired: {}", e.getMessage());
            throw new InvalidTokenHeader("JWT Token is expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new InvalidTokenHeader("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact of handler are invalid : {}", e.getMessage());
            throw new InvalidTokenHeader("JWT token compact of handler are invalid");
        }
    }

    public Claims extractClaim(String token) {
        return Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();
    }

    public Authentication parseAuthentication(String authToken) {
        Claims claims = extractClaim(authToken);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(TokenKey.AUTHORITIES).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = getPrincipal(claims, authorities);
        return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
    }

    private CustomUserPrincipal getPrincipal(Claims claims, Collection<? extends GrantedAuthority> authorities) {
        String subject = claims.getSubject();
        String auth = claims.get(TokenKey.AUTHORITIES, String.class);
        String tokenType = claims.get(TokenKey.TOKEN_TYPE, String.class);
        String fullName = claims.get(TokenKey.FULL_NAME, String.class);
        String subId = claims.get(TokenKey.SUB_ID, String.class);
        return new CustomUserPrincipal(subject, subId, fullName, tokenType, auth, authorities);
    }

    /**
     * parse token retrieve from header
     * slice prefix bearer token
     * */
    public String parseJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(CommonConstant.HttpAttribute.BEARER)) {
            return bearerToken.replace(CommonConstant.HttpAttribute.BEARER, "").trim();
        }
        return bearerToken;
    }

}
