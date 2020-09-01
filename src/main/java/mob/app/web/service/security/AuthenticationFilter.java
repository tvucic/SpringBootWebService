package mob.app.web.service.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import mob.app.web.service.SpringApplicationContext;
import mob.app.web.service.service.UserService;
import mob.app.web.service.shared.dto.UserDto;
import mob.app.web.service.ui.model.request.UserLoginRequestModel;

/*
    Class responsible for user login 
*/
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;

  public AuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  /*
   * Spring framework will be used to authenticate user with the username and
   * password and this method will be triggered
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {

    try {
      UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);

      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
      Authentication auth) throws IOException, ServletException {

    String userName = ((User) auth.getPrincipal()).getUsername();

    String token = Jwts.builder().setSubject(userName)
        .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();

    UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");

    UserDto userDto = userService.getUser(userName);

    res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    res.addHeader("userId", userDto.getUserId());
  }

}