package dev.poncio.SystemApps.InstantNotificationSdk.job;

import java.io.Serializable;

import dev.poncio.SystemApps.InstantNotificationSdk.dto.ResponseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class INCJobUpdate implements Serializable {

    private Integer percent;
    private String log;
    private INCJob job;

    @Getter(value = AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PROTECTED)
    private ResponseEntity resultUpdate;

}
