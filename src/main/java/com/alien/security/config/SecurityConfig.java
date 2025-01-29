package com.alien.security.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alien.security.jwt.JWTSecureEntryPoint;
import com.alien.security.jwt.JwtAuthenticationFilter;
import com.alien.security.jwt.TokenBlacklistService;

import jakarta.servlet.http.HttpServletResponse;


import java.util.List;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	   
	  @Autowired
	  private UserDetailsService userdetailsService;
	
	  @Autowired
      private JWTSecureEntryPoint  point;
	  
      @Autowired
      private JwtAuthenticationFilter filter;
	  
      @Autowired
      private PasswordEncoder passwordEncoder;
      
      @Autowired
      private TokenBlacklistService tokenBlacklistService;


		
	   @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.csrf((c)-> c.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .authorizeHttpRequests((authz) ->
	        authz.requestMatchers(
	        		"/api/login",
	        		"/api/user",
	        		"/api/createuser",
	        		"/api/adduser",
	        		"/api/refreshToken",
					"/api/event/all",
					"/api/event/photo/*",
					"/api/news/all",
					"/api/news/photo/*",
					"/api/photo-bank/all",
					"/api/photo-bank/photo/*"
					).permitAll()
	        .requestMatchers("/api/**", "/api/uploadPhoto/*", "/api/event/uploadPhoto/*", "/api/applications/submit", "/api/applications/currentuser").authenticated()
            .anyRequest().authenticated()
            )
	        .exceptionHandling(ex -> ex.authenticationEntryPoint(point))
            .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .logout(logout -> logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler((request, response, authentication) -> {
                        String token = (String) filter.extractTokenFromRequest(request); 
                        tokenBlacklistService.addToBlacklist(token);
                        tokenBlacklistService.toString();
                        response.setStatus(HttpServletResponse.SC_OK);
                    })
                    .invalidateHttpSession(true)
                );
             http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
	        return http.build();
	    }


 
	   private Customizer<FormLoginConfigurer<HttpSecurity>> withDefaults() {
	        return Customizer.withDefaults();
	    }

	   @Bean
	   public DaoAuthenticationProvider  daoAuthenticationProvider() {
		   DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		   provider.setUserDetailsService(userdetailsService);
		   provider.setPasswordEncoder(passwordEncoder);
		   return provider;
	   }
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:8080"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS","CONNECT","PUT","TRACE","HEAD"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	  
}
