package com.works.glycemic.config;

import com.works.glycemic.services.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final UserService service;
    public SecurityConfig(UserService service) {
        this.service = service;
    }

    // sql -> jpa query -> user control
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService((service)).passwordEncoder(service.encoder());
    }

    // hangi yöntemle giriş yapılarak, rollere göre hangi servis kullanılcak?
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/customer/**").hasRole("USER")
                .antMatchers("/news/**").hasRole("ADMIN")
                .antMatchers("/admin/**").permitAll()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().logoutUrl("/admin/logout").invalidateHttpSession(true) ;
        http.headers().frameOptions().disable(); // h2-console for using
         */
    }


}