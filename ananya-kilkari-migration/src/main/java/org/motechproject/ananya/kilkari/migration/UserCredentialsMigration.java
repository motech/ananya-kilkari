package org.motechproject.ananya.kilkari.migration;

import db.migration.V1_1__AdminUserCreation;
import org.codehaus.jackson.map.ObjectMapper;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.motechproject.ananya.kilkari.admin.domain.AdminUser;
import org.motechproject.ananya.kilkari.admin.domain.AdminUserList;
import org.motechproject.ananya.kilkari.admin.repository.AllAdminUsers;

import java.io.IOException;
import java.util.Properties;

public class UserCredentialsMigration {
    public static final String ADMIN_DB_NAME = "motech-admin";

    public void migrate() throws Exception {
        AllAdminUsers allAdminUsers = getAdminUserRepository();
        AdminUserList adminUsers = getAllCredentials();

        for (AdminUser adminUser : adminUsers) {
            allAdminUsers.addOrUpdate(adminUser);
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
