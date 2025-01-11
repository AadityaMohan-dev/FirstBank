//package com.aadi.bank.the_first_bank.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import lombok.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@AllArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // Extract the token from the request
//        String token = getTokenFromRequest(request);
//
//        // Validate the token and check if it's valid
//        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
//            // Extract the username from the token
//            String username = jwtTokenProvider.getUsername(token);
//
//            // Load user details
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            // Create an authentication token with the user's authorities
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                    userDetails,
//                    null, // No credentials
//                    userDetails.getAuthorities() // Authorities
//            );
//
//            // Set the authentication details source
//            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            // Set the authentication in the SecurityContext
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        }
//
//        // Continue the filter chain
//        filterChain.doFilter(request, response);
//    }
//
//    // Helper method to extract token from the request
//    private String getTokenFromRequest(@NonNull HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization"); // Correct header name
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // Remove "Bearer " prefix
//        }
//        return null;
//    }
//}
