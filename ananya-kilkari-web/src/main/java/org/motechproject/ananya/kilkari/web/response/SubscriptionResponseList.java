package org.motechproject.ananya.kilkari.web.response;

import org.joda.time.DateTime;
import org.motechproject.export.annotation.ComponentTypeProvider;
import org.motechproject.web.message.converters.annotations.CSVEntity;
import org.motechproject.web.message.converters.annotations.CSVFileName;

import java.util.ArrayList;
import java.util.Collection;


@CSVEntity
public class SubscriptionResponseList extends ArrayList<SubscriptionCCReferredByFlwResponse> {
    public SubscriptionResponseList(Collection<? extends SubscriptionCCReferredByFlwResponse> responses) {
        super(responses);
    }

    @ComponentTypeProvider
    public Class<?> getType(){
        return SubscriptionCCReferredByFlwResponse.class;
    }

    @CSVFileName
    public String getFileName(){
        return "Subscription_Referred_by_FLW_"+ DateTime.now().toString("yyyy-MM-dd'T'HH:mm")+".csv";
    }
}
