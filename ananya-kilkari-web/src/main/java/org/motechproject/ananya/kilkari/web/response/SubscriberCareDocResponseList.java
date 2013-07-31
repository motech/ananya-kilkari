package org.motechproject.ananya.kilkari.web.response;

import org.joda.time.DateTime;
import org.motechproject.export.annotation.ComponentTypeProvider;
import org.motechproject.web.message.converters.annotations.CSVEntity;
import org.motechproject.web.message.converters.annotations.CSVFileName;

import java.util.ArrayList;
import java.util.Collection;

@CSVEntity
public class SubscriberCareDocResponseList extends ArrayList<SubscriberCareDocResponse> {
    public SubscriberCareDocResponseList(Collection<? extends SubscriberCareDocResponse> responses) {
        super(responses);
    }

    @ComponentTypeProvider
    public Class<?> getType(){
        return SubscriberCareDocResponse.class;
    }

    @CSVFileName
    public String getFileName(){
        return "help_"+ DateTime.now().toString("yyyy-MM-dd'T'HH:mm")+".csv";
    }
}
