package org.motechproject.ananya.kilkari.admin.service;

import org.motechproject.ananya.kilkari.admin.domain.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static Logger log = LoggerFactory.getLogger(AuthenticationProvider.class);

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
            throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String password = (String) authentication.getCredentials();
        AuthenticationResponse authenticationResponse;
        try {
            authenticationResponse = authenticationService.checkFor(username, password);
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : authenticationResponse.roles())
            authorities.add(new SimpleGrantedAuthority(role));

        log.info("logging in  " + username);
        return new AuthenticatedUser(authorities, username, password);
    }
}
