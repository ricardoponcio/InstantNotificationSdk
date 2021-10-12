package dev.poncio.SystemApps.InstantNotificationSdk.dto;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
public class ResponseEntity {

    @Getter
    private Boolean status;
    @Getter
    private Integer code;
    @Getter
    private String message;
    @Getter
    private Object attach;

    public <T> T castObject(Class<T> clazz) {
        if (!(this.attach instanceof LinkedTreeMap))
            return null;
        return new Gson().fromJson(new JSONObject((LinkedTreeMap<String, String>) this.attach).toString(), clazz);
    }

}
