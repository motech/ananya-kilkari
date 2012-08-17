package org.motechproject.ananya.kilkari.performance.tests.utils;

import junit.framework.TestCase;
import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariPerformanceContext.xml")
@ActiveProfiles("test")
public abstract class SpringIntegrationTest extends TestCase{

    @Qualifier("kilkariSubscriptionDbConnector")
    @Autowired
    protected CouchDbConnector kilkariSubscriptionDbConnector;

    protected ArrayList<BulkDeleteDocument> toDelete;
    private Properties performanceProperties;

    public SpringIntegrationTest() {
    }

    public SpringIntegrationTest(String name) {
        super(name);
        this.performanceProperties = BaseConfiguration.performanceProperties;
    }

    @Before
    public void before() {
        toDelete = new ArrayList<BulkDeleteDocument>();
    }

    @After
    public void after() {
        kilkariSubscriptionDbConnector.executeBulk(toDelete);
    }

    protected String baseUrl() {
        return performanceProperties.getProperty("baseurl");
    }

    protected void markForDeletion(Object... documents) {
        for (Object document : documents) {
            toDelete.add(BulkDeleteDocument.of(document));
        }
    }
}
