package com.episen.eshop_ms_membership.security;


import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.episen.eshop_ms_membership.model.User;
import com.episen.eshop_ms_membership.setting.InfraSettings;
@Service
public class JwTokenGenerator {
		
		private JWSSigner signer;
	@PostConstruct
	public void init() {
		
		signer = new RSASSASigner(InfraSettings.keypairLoader().getPrivate());
		System.out.println(generateToken("el-hourri-login", Arrays.asList("ADMIN")));
	}
	
	public String generateToken(String subject, List<String> privileges) {
		
		ZonedDateTime currentDate = ZonedDateTime.now();
		
		JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
										.keyID(UUID.randomUUID().toString()) // TODO build private key id
										.type(JOSEObjectType.JWT)
										.build();
		
		JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
												.subject(subject)
												.audience("web")
												.issuer("episen-el-hourri-eshop")
												.issueTime(Date.from(currentDate.toInstant()))
												.expirationTime(Date.from(currentDate.plusHours(12).toInstant()))
												.jwtID(UUID.randomUUID().toString())
												.claim("Privileges", privileges)
												.build();
		
		SignedJWT signedJWT = new SignedJWT(header, claimSet);
		
		try {
			signedJWT.sign(signer);
		} catch (JOSEException e) {
			throw new RuntimeException(e);
		}
		
		return signedJWT.serialize();
										
	}



	public String getLoginFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	public Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(InfraSettings.keypairLoader().getPrivate()).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	public String generateToken(User user) {
		return generateToken(user.getLogin(),user.getPrivileges());
	}


	public Boolean canTokenBeRefreshed(String token) {
		return (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	public Boolean validateToken(String token,User user) {
		final String login= getLoginFromToken(token);
		return (login.equals(user.getLogin()) && !isTokenExpired(token));
	}
}
