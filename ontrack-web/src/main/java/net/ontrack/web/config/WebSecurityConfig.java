package net.ontrack.web.config;

import net.ontrack.core.security.SecurityRoles;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
    private AuthenticationManager authenticationManager;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManager);
    }

    @Configuration
    @Order(5)
    public static class APIHTTPSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/ui/login")
                    // FIXME Reenable CSRF protection (depends on the client)
                    // See http://docs.spring.io/spring-security/site/docs/3.2.0.RC2/reference/htmlsingle/#csrf-using
                    .csrf().disable()
                    .httpBasic().realmName("ontrack").and()
                    .authorizeRequests().antMatchers("/ui/login").access(
                        String.format(
                                "hasAnyRole('%s')",
                                StringUtils.join(SecurityRoles.ALL, "','")
                        )
                    );
        }

    }

    @Configuration
    @Order(10)
    public static class GUIHTTPSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationSuccessHandler authenticationSuccessHandler;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    // FIXME Reenable CSRF protection (depends on the client)
                    // See http://docs.spring.io/spring-security/site/docs/3.2.0.RC2/reference/htmlsingle/#csrf-using
                    .csrf().disable()
                    .formLogin().loginPage("/login").successHandler(authenticationSuccessHandler).and()
                    .httpBasic().realmName("ontrack").and()
                    .logout().logoutUrl("/logout").logoutSuccessUrl("/").and()
                    .authorizeRequests().anyRequest().permitAll();
        }

    }
}
