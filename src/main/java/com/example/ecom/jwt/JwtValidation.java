package com.example.ecom.jwt;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtValidation {

    @Value("${spring.key.jwt}")
    protected String JWT_SECRET;

    protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);;

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(HttpServletRequest request) {
        String jwt = getJwtFromRequest(request);
        if (StringUtils.hasText(jwt)) {
            try {
                Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(jwt);
                return true;
            } catch (MalformedJwtException e) {
                APP_LOGGER.error("Invalid JWT Token");
            } catch (ExpiredJwtException e) {
                APP_LOGGER.error("Expired JWT Token or Deprecated JWT Token");
            } catch (UnsupportedJwtException e) {
                APP_LOGGER.error("Unsupported JWT Token");
            } catch (IllegalArgumentException e) {
                APP_LOGGER.error("JWT claims is empty string");
            } catch (SignatureException e) {
                APP_LOGGER.error("JWT signature does not match locally computed signature!");
            }
        }
        return false;
    }
}
