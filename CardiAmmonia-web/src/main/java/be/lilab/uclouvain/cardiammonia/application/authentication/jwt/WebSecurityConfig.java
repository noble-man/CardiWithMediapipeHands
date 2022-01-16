package be.lilab.uclouvain.cardiammonia.application.authentication.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
				.antMatchers("/**").permitAll()
				.antMatchers("/api/auth/**").permitAll()
				.antMatchers("/api/test/**").permitAll()
			.anyRequest().authenticated();

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	 
// protected void configure(HttpSecurity httpSecurity) throws Exception {
//    httpSecurity
//            // we don't need CSRF because our token is invulnerable
//            .csrf().disable()
//
//            // don't create session
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//
//            .authorizeRequests()
//            //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//            // allow anonymous resource requests
//            .antMatchers(
//                    HttpMethod.GET,
//                    "/",
//                    "/*.html",
//                    "/favicon.ico",
//                    "/**/*.html",
//                    "/**/*.css",
//                    "/**/*.js",
//                    "/**"
//            ).permitAll()
//            .antMatchers("/auth/**").permitAll()
//            .anyRequest().authenticated();
//
//    // disable page caching
//    httpSecurity.headers().cacheControl();
//}
	
	
/*	  @Override
	  protected void configure(HttpSecurity http) throws Exception {

	    if (h2ConsoleEnabled) {
	      http.authorizeRequests()
	          .antMatchers("/h2-console", "/h2-console/**")
	          .permitAll()
	          .and()
	          .headers()
	          .frameOptions()
	          .sameOrigin();
	    }

	    http.csrf()
	        .disable()
	        .cors()
	        .and()
	        .exceptionHandling()
	        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
	        .and()
	        .sessionManagement()
	        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        .and()
	        .authorizeRequests()
	        .antMatchers(HttpMethod.OPTIONS)
	        .permitAll()
	        .antMatchers("/graphiql")
	        .permitAll()
	        .antMatchers("/graphql")
	        .permitAll()
	        .antMatchers(HttpMethod.GET, "/articles/feed")
	        .authenticated()
	        .antMatchers(HttpMethod.POST, "/users", "/users/login")
	        .permitAll()
	        .antMatchers(HttpMethod.GET, "/articles/**", "/profiles/**", "/tags")
	        .permitAll()
	        .anyRequest()
	        .authenticated();

	    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	  }
*/
}
