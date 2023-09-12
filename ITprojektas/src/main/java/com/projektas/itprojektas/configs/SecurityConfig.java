package com.projektas.itprojektas.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests();
        http.authorizeRequests()
                .antMatchers("/", "/help").permitAll()
                .antMatchers("/admin", "/admin/**").hasRole("ADMIN")
                .antMatchers("/credit", "/credit/request").hasRole("USER")
                .antMatchers("/consultation").hasAnyRole("ROLE_USER", "ROLE_CONSULTANT")
                .and().formLogin().loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
    }
}
