package link.yauritux.xphrtht.adapter.config;

import link.yauritux.xphrtht.core.domain.vo.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/web/reports/work_hours")
                        .hasAnyRole(UserRole.EMPLOYEE.name(), UserRole.ADMIN.name()).anyRequest().permitAll()
        ).formLogin(form -> form.loginPage("/login").permitAll()
        ).logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails tom = User.builder()
                .username("tom").password(passwordEncoder.encode("tom123"))
                .roles(UserRole.EMPLOYEE.name()).build();
        UserDetails jerry = User.builder()
                .username("jerry").password(passwordEncoder.encode("jerry123"))
                .roles(UserRole.EMPLOYEE.name()).build();
        UserDetails admin = User.builder()
                .username("admin").password(passwordEncoder.encode("admin123"))
                .roles(UserRole.ADMIN.name()).build();
        return new InMemoryUserDetailsManager(tom, jerry, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
