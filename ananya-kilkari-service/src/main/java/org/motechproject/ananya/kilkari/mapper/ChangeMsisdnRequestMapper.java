package org.motechproject.ananya.kilkari.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.request.ChangeMsisdnWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeMsisdnRequest;

import java.util.ArrayList;
import java.util.List;

public class ChangeMsisdnRequestMapper {

    public static ChangeMsisdnRequest mapFrom(ChangeMsisdnWebRequest changeMsisdnWebRequest) {
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(
                changeMsisdnWebRequest.getOldMsisdn(), changeMsisdnWebRequest.getNewMsisdn());

        if (changeMsisdnWebRequest.getPacks().size() == 1 &&
            StringUtils.trim(changeMsisdnWebRequest.getPacks().get(0)).equalsIgnoreCase("ALL")) {
            changeMsisdnRequest.setShouldChangeAllPacks(true);
            return changeMsisdnRequest;
        }

        List<SubscriptionPack> packs = new ArrayList<>();
        for (String packString : changeMsisdnWebRequest.getPacks()) {
            packs.add(SubscriptionPack.from(packString));
        }
        changeMsisdnRequest.setPacks(packs);

        return changeMsisdnRequest;
    }
}
