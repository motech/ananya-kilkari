package db.migration;

import com.googlecode.flyway.core.migration.java.JavaMigration;
import org.codehaus.jackson.map.ObjectMapper;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.motechproject.ananya.kilkari.admin.domain.AdminUser;
import org.motechproject.ananya.kilkari.admin.domain.AdminUserList;
import org.motechproject.ananya.kilkari.admin.repository.AllAdminUsers;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.Properties;

public class V1_1__AdminUserCreation implements JavaMigration {

    public static final String ADMIN_DB_NAME = "motech-admin";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        AllAdminUsers allAdminUsers = getAdminUserRepository();
        AdminUserList adminUsers = getAllCredentials();

        for (AdminUser adminUser : adminUsers) {
            allAdminUsers.add(adminUser);
        }
    }

    protected AllAdminUsers getAdminUserRepository() throws Exception {
        Properties couchDBProperties = new Properties();
        couchDBProperties.load(V1_1__AdminUserCreation.class.getClassLoader().getResourceAsStream("couchdb.properties"));
        HttpClientFactoryBean httpClientFactoryBean = new HttpClientFactoryBean();
        httpClientFactoryBean.setProperties(couchDBProperties);
        httpClientFactoryBean.afterPropertiesSet();
        CouchDbConnector couchDbConnector = new StdCouchDbConnector(ADMIN_DB_NAME, new StdCouchDbInstance(httpClientFactoryBean.getObject()));
        return new AllAdminUsers(couchDbConnector);
    }

    private AdminUserList getAllCredentials() throws IOException {
        Properties adminUserProperties = new Properties();
        adminUserProperties.load(V1_1__AdminUserCreation.class.getClassLoader().getResourceAsStream("admin.properties"));
        String credentials = adminUserProperties.getProperty("credentials");
        return new ObjectMapper().readValue(credentials, AdminUserList.class);
    }
}
