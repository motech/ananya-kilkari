package org.motechproject.ananya.kilkari.handlers;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequest;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OBDServiceOptionHandlerTest {

    @Mock
    private OBDServiceOptionFactory obdServiceOptionFactory;
    @Mock
    private KilkariCampaignService kilkariCampaignService;

    @Test
    public void shouldHandleAOBDCallBackRequest() {
        OBDServiceOptionHandler obdServiceOptionHandler = new OBDServiceOptionHandler(obdServiceOptionFactory, kilkariCampaignService);
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        OBDRequest obdRequest = new OBDRequest();
        obdRequest.setServiceOption(ServiceOption.HELP.name());
        OBDRequestWrapper expectedObdRequest = new OBDRequestWrapper(obdRequest, "subscriptionId", DateTime.now());
        stringObjectHashMap.put("0", expectedObdRequest);

        obdServiceOptionHandler.handleOBDCallbackRequest(new MotechEvent(OBDEventKeys.PROCESS_CALLBACK_REQUEST, stringObjectHashMap));

        verify(kilkariCampaignService).processSuccessfulMessageDelivery(expectedObdRequest);
    }
}
