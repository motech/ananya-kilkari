package org.motechproject.ananya.kilkari.admin.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.kilkari.admin.domain.AdminUser;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllAdminUsers extends MotechBaseRepository<AdminUser> {

    @Autowired
    public AllAdminUsers(@Qualifier("adminDbConnector") CouchDbConnector db) {
        super(AdminUser.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public AdminUser findByName(String name) {
        if (StringUtils.isBlank(name)) return null;
        ViewQuery viewQuery = createQuery("by_name").key(name).includeDocs(true);
        List<AdminUser> adminUsers = db.queryView(viewQuery, AdminUser.class);
        if (adminUsers == null || adminUsers.isEmpty()) return null;
        return adminUsers.get(0);
    }
}
