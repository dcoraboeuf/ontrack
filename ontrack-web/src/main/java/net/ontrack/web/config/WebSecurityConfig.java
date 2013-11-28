package net.ontrack.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManager);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // FIXME ui/login protection for UI access
                // FIXME Reenable CSRF protection (depends on the client)
                // See http://docs.spring.io/spring-security/site/docs/3.2.0.RC2/reference/htmlsingle/#csrf-using
                .csrf().disable()
                .formLogin().loginPage("/login").successHandler(authenticationSuccessHandler).and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/").and()
                .authorizeRequests().anyRequest().permitAll();
    }
}
