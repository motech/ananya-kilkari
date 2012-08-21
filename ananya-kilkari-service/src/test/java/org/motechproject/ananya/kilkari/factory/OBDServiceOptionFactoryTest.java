package org.motechproject.ananya.kilkari.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.handlers.callback.obd.OBDDeactivateHandler;
import org.motechproject.ananya.kilkari.handlers.callback.obd.OBDHelpHandler;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class OBDServiceOptionFactoryTest {
    @Mock
    private OBDDeactivateHandler obdDeactivateHandler;
    @Mock
    private OBDHelpHandler obdHelpHandler;

    @Test
    public void shouldGetTheUnsubscriptionHandler() {
        OBDServiceOptionFactory obdServiceOptionFactory = new OBDServiceOptionFactory(obdDeactivateHandler, obdHelpHandler);

        ServiceOptionHandler handler = obdServiceOptionFactory.getHandler(ServiceOption.UNSUBSCRIBE);

        assertTrue(handler instanceof OBDDeactivateHandler);
    }

    @Test
    public void shouldGetTheHelpHandler() {
        OBDServiceOptionFactory obdServiceOptionFactory = new OBDServiceOptionFactory(obdDeactivateHandler, obdHelpHandler);

        ServiceOptionHandler handler = obdServiceOptionFactory.getHandler(ServiceOption.HELP);

        assertTrue(handler instanceof OBDHelpHandler);
    }
}
