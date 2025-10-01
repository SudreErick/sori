package br.com.fiap.appSori.service.auth;

import br.com.fiap.appSori.domain.Usuario;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {


    @Value("${jwt.secret}")
    private String secret;

    // ⭐️ MODIFICAÇÃO: Alterar o parâmetro de UserDetails para Usuario
    public String generateToken(Usuario usuario) throws JOSEException {

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(usuario.getUsername())
                .issuer("projeto-psicosocial")
                .issueTime(new Date())
                .expirationTime(Date.from(ZonedDateTime.now().plus(24, ChronoUnit.HOURS).toInstant()))
                .jwtID(UUID.randomUUID().toString())

                // ⭐️ ADIÇÃO CRÍTICA: Adiciona o perfil (role) como uma claim
                // Usamos .name() para armazenar a string exata (CLIENTE, ADMIN, etc.)
                .claim("role", usuario.getRole().name())
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(new MACSigner(secret));

        return signedJWT.serialize();
    }

    public String getSubject(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secret);

            if (signedJWT.verify(verifier)) {
                return signedJWT.getJWTClaimsSet().getSubject();
            }
        } catch (ParseException | JOSEException e) {
            System.err.println("Erro ao validar o token: " + e.getMessage());
        }
        return null;
    }
}
