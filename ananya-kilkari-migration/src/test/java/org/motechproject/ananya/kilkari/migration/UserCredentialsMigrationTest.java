package org.motechproject.ananya.kilkari.migration;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.admin.domain.AdminUser;
import org.motechproject.ananya.kilkari.admin.repository.AllAdminUsers;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserCredentialsMigrationTest {
    private UserCredentialsMigration userCredentialsMigration;
    private AllAdminUsers adminUserRepository;

    @Before
    @After
    public void setUp() throws Exception {
        userCredentialsMigration = new UserCredentialsMigration();
        adminUserRepository = userCredentialsMigration.getAdminUserRepository();
        adminUserRepository.removeAll();
    }

    @Test
    public void shouldCreateOrUpdateAnUser() throws Exception {
        userCredentialsMigration.migrate();

        List<AdminUser> adminUsers = adminUserRepository.getAll();
        assertEquals(1, adminUsers.size());
        assertEquals("admin", adminUsers.get(0).getName());
        assertTrue(adminUsers.get(0).passwordIs("password"));
    }
}
