package dev.poncio.SystemApps.InstantNotificationSdk.job;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class INCJobFinalize implements Serializable {
    
    private boolean success;
    private String resultMessage;

}
