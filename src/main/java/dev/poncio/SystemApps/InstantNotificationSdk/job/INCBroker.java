package dev.poncio.SystemApps.InstantNotificationSdk.job;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class INCBroker {
    
    private String url;
    private String token;
    @Builder.Default
    private boolean makeAsyncCalls = true;

}
