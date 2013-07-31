package org.motechproject.ananya.kilkari.admin.service;

import org.junit.Test;
import org.motechproject.ananya.kilkari.admin.service.AuthenticationResponse;

import static junit.framework.Assert.assertTrue;

public class AuthenticationResponseTest {

    @Test
    public void shouldAddRole(){
        AuthenticationResponse response = new AuthenticationResponse();
        response.addRole("admin");
        assertTrue(response.roles().contains("admin"));
    }
}
