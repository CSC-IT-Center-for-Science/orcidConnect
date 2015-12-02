package fi.csc.orcidconnect;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
public class MyWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
    @Override
    public void init(WebSecurity web) {
        web.ignoring().antMatchers("/", "/index.html");
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
        //http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
		http.authorizeRequests()
			.antMatchers("/**").authenticated()
			//.and().formLogin().loginProcessingUrl("/login").loginPage("/login.jsp").permitAll();
			;

	}
	
}