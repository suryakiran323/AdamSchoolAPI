package com.stu.app.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.stu.app.model.Users;
import com.stu.app.repository.UsersRepo;
import com.stu.app.util.AesUtil;

@Component
@Slf4j
public class TokenProvider {

 // final
  //RedisService redisService;
  @Autowired
  UsersRepo userRepository;
  
  @Value("${app.jwtSecret}")
  private String jwtSecret;
  @Value("${app.jwtExpirationInMs}")
  private long jwtExpirationInMs;

  public TokenProvider() {
  //this.redisService = redisService;
  }

  /**
   * this method is to generate the token from the user principal
   *
   * @param authentication to get the user principal
   * @return jwt token
   */
  public String generateToken(Authentication authentication, Users user) {

	    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
	   // PrivilegesDTO userPrivileges = userService.getRolesAndPermissions(authentication);
	    Date now = new Date();
	    Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

	    return Jwts.builder()
	      .setIssuer("Nexco.com")
	      .setAudience("Nexco-"+user.getType())
	      .claim("token", userPrincipal.getId())
	      .claim("utype", user.getType())
	      .setSubject(Long.toString(userPrincipal.getId()))
	      .setIssuedAt(new Date())
	      .setExpiration(expiryDate)
	      .signWith(SignatureAlgorithm.HS512, jwtSecret)
	      .compact();

	  }

  /**
   * this service is to validate the token for expiration time
   *
   * @param authToken for validation
   * @return boolean
   */
  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException ex) {
      log.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty.");
    }
    return false;
  }

  /**
   * this method is to get the token from the servlet request
   *
   * @param request HttpServletRequest to get the token
   * @return jwt token
   */
  public String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      //return AesUtil.decrypt(bearerToken.substring(7));
    	return bearerToken.substring(7);
    }
    return null;
  }

  /**
   * this method is to get the userId from the jwt token
   *
   * @param jwt to get the userId
   * @return userId
   */
  public Integer getUserIdFromJWT(String jwt) {
    Claims claims = Jwts.parser()
      .setSigningKey(jwtSecret)
      .parseClaimsJws(jwt)
      .getBody();

    /*JsonArray list = redisService.getTokensJsonArray(claims.getSubject() + "");
    boolean tokenExists = false;
    for (int i = 0; i < list.size(); i++) {
      if (jwt.trim().equalsIgnoreCase(list.get(i).toString().substring(1, list.get(i).toString().length() - 1))) {
        tokenExists = true;
      }
    }
    if (!tokenExists) {
      throw new NexcoRTException(HttpStatus.UNAUTHORIZED, "You are unauthorized to access this resource");
    }*/
    return Integer.parseInt(claims.getSubject());
  }

  /**
   * method to get user id from request
   *
   * @param request
   * @return
   */
  public Integer getUserIdFromRequest(HttpServletRequest request) {
    String token = getJwtFromRequest(request);
    if (token != null && (!token.equals("null"))) {
      return getUserIdFromJWT(token);
    } else {
      return null;
    }
  }

}
