package com.vietle.vault;

import com.vietle.vault.util.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BasicAuthConfig extends WebSecurityConfigurerAdapter {
    private String basicAuthUsername;
    private String basicAuthPassword;
    private String basicAuthPassphrase;

    @Autowired
    public BasicAuthConfig(@Value("${basic.auth.basicAuthUsername}") String basicAuthUsername,
                           @Value("${basic.auth.basisAuthPassword}") String basicAuthPassword,
                           @Value("${basic.auth.basicAuthPassphrase}") String basicAuthPassphrase) {
        this.basicAuthUsername = basicAuthUsername; this.basicAuthPassword = basicAuthPassword; this.basicAuthPassphrase = basicAuthPassphrase;
    }

    /**
     * take the encrypted basicAuthPassword and decrypt it using the basicAuthPassphrase and store in memory
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(basicAuthUsername)
                .password(passwordEncoder().encode(CryptoUtil.decryptString(basicAuthPassword, basicAuthPassphrase)))
                .roles("USER");

    }

    // Secure the endpoints with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic()
            .and()
            .authorizeRequests()
            //exclude /health and /ping
            .antMatchers("/actuator/health", "/api/ping").permitAll()
            //every other url, need to be authenticated
            .anyRequest().authenticated()
            .and()
            .csrf().disable()
            .formLogin().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
        //        http
        //HTTP Basic authentication
//                .httpBasic()
//                .and()
//                .authorizeRequests()
//                .antMatchers( "/api/**").authenticated()
//                .antMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PATCH, "/books/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")
//                .and()
//                .csrf().disable()
//                .formLogin().disable();
//    }
}
