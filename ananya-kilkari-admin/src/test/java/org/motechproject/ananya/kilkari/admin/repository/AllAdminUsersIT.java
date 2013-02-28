package org.motechproject.ananya.kilkari.admin.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.admin.domain.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariAdminContext.xml")
@ActiveProfiles("test")
public class AllAdminUsersIT {

    @Autowired
    private AllAdminUsers allAdminUsers;

    @Test
    public void shouldFindByName() {
        allAdminUsers.add(new AdminUser("cartman", "killHippie"));

        AdminUser adminUser = allAdminUsers.findByName("cartman");
        assertEquals("cartman", adminUser.getName());
        assertTrue(adminUser.passwordIs("killHippie"));
    }

    @Test
    public void shouldReturnNullIfUserNameIsEmpty() {
        assertNull(allAdminUsers.findByName(""));
        assertNull(allAdminUsers.findByName(null));
    }

    @After
    public void tearDown() {
        allAdminUsers.removeAll();
    }
}
