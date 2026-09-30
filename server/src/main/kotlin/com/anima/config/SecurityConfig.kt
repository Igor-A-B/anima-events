package com.anima.config

import com.anima.features.auth.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.anima.features.auth.ratelimit.AuthRateLimitFilter
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter

// reads "Authorization: Bearer <access token>", sets the user id as the principal name
// and the accountType claim as a ROLE_ authority
class JwtAuthFilter(private val jwtService: JwtService) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = request.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }?.substring(7)
        token?.let { jwtService.validateAndDecode(it) }?.ifPresent {
            val role = it.getClaim("accountType").asString()?.let { type -> SimpleGrantedAuthority("ROLE_$type") }
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(it.subject, null, listOfNotNull(role))
        }
        chain.doFilter(request, response)
    }
}

@Configuration
class SecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity, jwtService: JwtService): SecurityFilterChain =
        http
            .csrf { it.disable() }
            // the web app runs on another origin, without this the browser preflight gets a 401
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/events", "/events/**").permitAll()
                    .anyRequest().authenticated()
            }
            // clients refresh their session on 401, Spring's default would answer 403
            .exceptionHandling { it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) }
            .addFilterBefore(AuthRateLimitFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter::class.java)
            .build()

    // comma separated origin patterns, the default covers the web app dev servers
    @Bean
    fun corsConfigurationSource(
        @Value("\${cors.allowed-origins:http://localhost:*,http://127.0.0.1:*}") origins: List<String>,
    ): CorsConfigurationSource = UrlBasedCorsConfigurationSource().apply {
        registerCorsConfiguration("/**", CorsConfiguration().apply {
            allowedOriginPatterns = origins
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type")
        })
    }
}
