package csd.api.modules.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userSvc){
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
        String[] allUsers = new String[]{"ADMIN", "MANAGER", "ANALYST", "USER"};
        String[] onlyEmp = new String[]{"ADMIN", "MANAGER", "ANALYST"};
        String[] onlyManager = new String[]{"ADMIN", "MANAGER"};
        String[] onlyUser = new String[]{"ADMIN", "USER"};
        String[] onlyAdmin = new String[]{"ADMIN"};
        
        http
        .httpBasic()
            .and() //  "and()"" method allows us to continue configuring the parent
        .authorizeRequests()
            // User Controller
            .antMatchers(HttpMethod.GET, "/users").hasAnyRole(onlyAdmin)
            .antMatchers(HttpMethod.POST, "/user/createUser").hasAnyRole(onlyManager)
            
            // Account Controller
            .antMatchers(HttpMethod.GET, "/accounts").hasAnyRole(onlyManager)
            .antMatchers(HttpMethod.POST, "/account/createAccount").hasAnyRole(onlyUser)
            .antMatchers(HttpMethod.GET, "/transfers").hasAnyRole(onlyManager)
            .antMatchers(HttpMethod.POST, "/transfer/makeTransfer").hasAnyRole(onlyUser)

            // Trade Controller
            
            // Content Controller
            .antMatchers(HttpMethod.GET, "/contents").hasAnyRole(onlyEmp)
            .antMatchers(HttpMethod.GET, "/content/approvedContents").hasAnyRole(allUsers)
            .antMatchers(HttpMethod.PUT, "/content/approveContent/{id}").hasAnyRole(onlyEmp)
            .and()

        // Control Logging in and out
        .formLogin().loginPage("/login_page").permitAll().and()
        .logout().logoutUrl("/perform_logout").and()

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
 