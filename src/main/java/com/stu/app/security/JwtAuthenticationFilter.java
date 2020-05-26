package com.stu.app.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepoService userRepoService;

    //@Autowired
    //private RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = tokenProvider.getJwtFromRequest(request);
            Integer userId = 0;

            try {
                userId = tokenProvider.getUserIdFromJWT(Objects.requireNonNull(jwt));
            }catch (NullPointerException e){
                log.error(e.getMessage());
            }

            if ( tokenProvider.validateToken(jwt)) {
            	log.info("calling filter inside>>>>>>>>>>>>>>>>>>>"+userId);
            	
					/*
	                    Note that you could also encode the user's username and roles inside JWT claims
	                    and create the UserDetails object by parsing those claims from the JWT.
	                    That would avoid the following database hit. It's completely up to you.
					 */
            	request.setAttribute("userId", userId);
            	
                UserDetails userDetails = userRepoService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

}
