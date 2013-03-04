package db.migration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.admin.domain.AdminUser;
import org.motechproject.ananya.kilkari.admin.repository.AllAdminUsers;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class V1_1__AdminUserCreationTest {

    private V1_1__AdminUserCreation adminMigration;
    private AllAdminUsers adminUserRepository;

    @Before
    @After
    public void setUp() throws Exception {
        adminMigration = new V1_1__AdminUserCreation();
        adminUserRepository = adminMigration.getAdminUserRepository();
        adminUserRepository.removeAll();
    }

    @Test
    public void shouldCreateNewAdminUser() throws Exception {
        adminMigration.migrate(null);

        List<AdminUser> adminUsers = adminUserRepository.getAll();
        assertEquals(1, adminUsers.size());
        assertEquals("admin", adminUsers.get(0).getName());
        assertTrue(adminUsers.get(0).passwordIs("password"));
    }
}
