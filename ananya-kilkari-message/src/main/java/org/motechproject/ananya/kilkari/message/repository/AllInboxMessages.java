package org.motechproject.ananya.kilkari.message.repository;


import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.kilkari.message.domain.InboxMessage;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllInboxMessages extends MotechBaseRepository<InboxMessage> {

    @Autowired
    public AllInboxMessages(@Qualifier("messageDbConnector") CouchDbConnector db) {
        super(InboxMessage.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public InboxMessage findBySubscriptionId(String subscriptionId) {
        ViewQuery viewQuery = super.createQuery("by_subscriptionId").key(subscriptionId).includeDocs(true);
        List<InboxMessage> inboxMessages = db.queryView(viewQuery, InboxMessage.class);
        return singleResult(inboxMessages);
    }

    public void deleteFor(String subscriptionId) {
        InboxMessage inboxMessage = findBySubscriptionId(subscriptionId);
        if(inboxMessage != null)
            remove(inboxMessage);
    }
}
