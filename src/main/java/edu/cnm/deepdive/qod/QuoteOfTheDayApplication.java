package edu.cnm.deepdive.qod;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@SpringBootApplication
@EnableWebSecurity
@EnableResourceServer
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class QuoteOfTheDayApplication extends ResourceServerConfigurerAdapter {

  @Value("${oauth.clientId}")
  private String clientId;

  public static void main(String[] args) {
    SpringApplication.run(QuoteOfTheDayApplication.class, args);
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId(clientId);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
   http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
   http.authorizeRequests()
       // only users can POST, PUT, or DELETE, but anybody can GET (matches filter out requests)
       .antMatchers(HttpMethod.POST, "/quotes").hasRole("USER")
       .antMatchers(HttpMethod.PUT, "/quotes/**").hasRole("USER") // antMatcher path-recursive wildcard
       .antMatchers(HttpMethod.DELETE, "/quotes/**").hasRole("USER")
       .antMatchers(HttpMethod.POST, "/sources").hasRole("USER")
       .antMatchers(HttpMethod.PUT, "/sources/**").hasRole("USER")
       .antMatchers(HttpMethod.DELETE, "/sources/**").hasRole("USER")
       .anyRequest().permitAll();
  }
}
