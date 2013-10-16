package org.motechproject.ananya.kilkari.admin.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.admin.domain.AuthenticatedUser;
import org.motechproject.ananya.kilkari.admin.service.AuthenticationProvider;
import org.motechproject.ananya.kilkari.admin.service.AuthenticationResponse;
import org.motechproject.ananya.kilkari.admin.service.AuthenticationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuthenticationProviderTest {
    @Mock
    private AuthenticationService authenticationService;

    private AuthenticationProvider authenticationProvider;

    @Before
    public void setUp() {
        initMocks(this);
        authenticationProvider = new AuthenticationProvider(authenticationService);
    }

    @Test
    public void shouldUseAuthenticationServiceAndReturnAuthenticatedUserWithRoles() {
        String username = "drStrangeLove";
        String password = "atomBomb";
        String role = "admin";
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.addRole("admin");
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);

        when(authentication.getCredentials()).thenReturn(password);
        when(authenticationService.checkFor(username, password)).thenReturn(authenticationResponse);

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authenticationProvider.retrieveUser(username, authentication);

        assertTrue(authenticatedUser.getAuthorities().contains(new GrantedAuthorityImpl(role)));
        assertThat(authenticatedUser.getUsername(), is(username));
        assertThat(authenticatedUser.getPassword(), is(password));
    }
}
