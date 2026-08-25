package phil.grouper.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import phil.grouper.services.MyUserDetailsService;

@Configuration
public class SecurityConfig {

        private final MyUserDetailsService userDetailsService;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(MyUserDetailsService userDetailsService,
                        JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.userDetailsService = userDetailsService;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // 1. Disable CSRF for API endpoints so external tools/browsers won't require
                                // CSRF tokens
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**"))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/register", "/login", "/css/**", "/js/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/csv/display").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/csv/i10vx/display")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/csv/da/display").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/csv/ccex/display")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/csv/bmdc/display")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/csv/i10/display").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/csv/drg/display").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/csv/dcv/display").authenticated()
                                                 .requestMatchers(HttpMethod.GET, "/api/csv/dc/display").authenticated()
                                                .anyRequest()
                                                .authenticated())

                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/dashboard", true) // Ensure this points to
                                                                                       // /dashboard
                                                .permitAll())

                                .logout(logout -> logout.permitAll());

                return http.build();
        }
}
