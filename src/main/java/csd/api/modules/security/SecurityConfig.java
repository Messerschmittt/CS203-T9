package csd.api.modules.security;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static csd.api.modules.security.SecurityConstants.SIGN_UP_URL;
import csd.api.modules.user.CustomUserDetailsService;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userSvc){
        this.userDetailsService = userSvc;
    }
    
    /** 
     * Attach the user details and password encoder.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(encoder());
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Security classifications
        String[] allUsers = new String[]{ "MANAGER", "ANALYST", "USER"};
        String[] onlyEmp = new String[]{ "MANAGER", "ANALYST"};
        String[] onlyManager = new String[]{ "MANAGER"};
        String[] onlyManagerAndUser = new String[]{"MANAGER", "USER"};
        String[] onlyUser = new String[]{ "USER"};
        
        http
        .requiresChannel().anyRequest().requiresSecure().and()  // For https
        .httpBasic()
        .and() //  "and()"" method allows us to continue configuring the parent
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/reset").permitAll()
        .and()
        .addFilter(new JWTAuthenticationFilter(authenticationManager()))
        .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService))
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests()
            // .anyRequest().authenticated() // All requests to API has to be authenticated
            
            // User Controller
            // .antMatchers(HttpMethod.GET, "/users").hasAnyRole(onlyManager   )
            .antMatchers(HttpMethod.POST, "/api/customers").hasAnyRole(onlyManager)
            .antMatchers(HttpMethod.PUT, "/api/customers/*").hasAnyRole(onlyManagerAndUser)
            .antMatchers(HttpMethod.GET, "/api/customers/*").hasAnyRole(onlyManagerAndUser)
            
            // Account Controller
            .antMatchers(HttpMethod.GET, "/api/accounts").hasAnyRole(allUsers)
            .antMatchers(HttpMethod.POST, "/api/accounts").hasAnyRole(onlyManager)
            .antMatchers(HttpMethod.GET, "/api/accounts/**").hasAnyRole(onlyManagerAndUser)
            .antMatchers(HttpMethod.GET, "/api/transactions").hasAnyRole(onlyManager)
            .antMatchers(HttpMethod.POST, "/api/accounts/{account_id}/transactions").hasAnyRole(onlyUser)
            .antMatchers(HttpMethod.POST, "/api/transactions/makeTransfer").hasAnyRole(onlyUser)

            // Trade Controller
            .antMatchers(HttpMethod.GET, "/api/trades").hasAnyRole(allUsers)
            .antMatchers(HttpMethod.POST, "/api/trades").hasAnyRole(onlyUser)
            .antMatchers(HttpMethod.GET, "/api/trades/*").hasAnyRole(onlyUser)
            .antMatchers(HttpMethod.DELETE, "/api/trades/*").hasAnyRole(onlyUser)
            .antMatchers(HttpMethod.PUT, "/api/trades/*").hasAnyRole(onlyUser)
            
            // Porfolio Controller
            .antMatchers(HttpMethod.GET, "/api/portfolio").hasAnyRole(onlyUser)
            // Stock Controller
            .antMatchers(HttpMethod.GET, "/api/stocks").hasAnyRole(onlyUser)
            .antMatchers(HttpMethod.GET, "/api/stocks/*").hasAnyRole(onlyUser)
            
            // Content Controller
            .antMatchers(HttpMethod.GET, "/api/contents").hasAnyRole(allUsers)
            .antMatchers(HttpMethod.GET, "/api/contents/*").hasAnyRole(allUsers)
            .antMatchers(HttpMethod.POST, "/api/contents").hasAnyRole(onlyEmp)
            .antMatchers(HttpMethod.PUT, "/api/contents/*").hasAnyRole(onlyEmp)
            .antMatchers(HttpMethod.DELETE, "/api/contents/*").hasAnyRole(onlyEmp)
            .antMatchers(HttpMethod.GET, "/api/contents/**").hasAnyRole(allUsers)

            
            .and()

        // Control Logging in and out
        // .formLogin().loginPage("/login_page").permitAll().and()
        .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .and()

        .csrf().disable() // CSRF protection is needed only for browser based attacks
        .formLogin().disable()
        .headers().disable(); // Disable the security headers, as we do not return HTML in our service
    }

    /**
     * @Bean annotation is used to declare a PasswordEncoder bean in the Spring application context. 
     * Any calls to encoder() will then be intercepted to return the bean instance.
     */
    @Bean
    public BCryptPasswordEncoder encoder() {
        // auto-generate a random salt internally
        return new BCryptPasswordEncoder();
    }
}
 