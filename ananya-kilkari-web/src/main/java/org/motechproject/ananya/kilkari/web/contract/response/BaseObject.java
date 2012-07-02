package org.motechproject.ananya.kilkari.web.contract.response;

import com.google.gson.Gson;

public class BaseObject {

    public Object fromJson(String jsonSerializedString) {
        return new Gson().fromJson(jsonSerializedString, this.getClass());
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
