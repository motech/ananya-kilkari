package db.migration;

import com.googlecode.flyway.core.migration.java.JavaMigration;
import org.motechproject.ananya.kilkari.migration.UserCredentialsMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_1__AdminUserCreation extends UserCredentialsMigration implements JavaMigration {
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        migrate();
    }
}
