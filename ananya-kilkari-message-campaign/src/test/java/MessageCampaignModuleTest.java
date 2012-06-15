import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import service.KilkariMessageCampaignService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application-kilkari-message-campaign.xml")
public class MessageCampaignModuleTest {

    @Autowired
    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Test
    @Ignore
    public void shouldCreateMessageCampaign() {
        kilkariMessageCampaignService.start(new CampaignRequest("my_id", "kilkari-15month-campaign", null, null));
    }

}
