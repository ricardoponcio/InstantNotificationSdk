package dev.poncio.SystemApps.InstantNotificationSdk.job;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class INCJob {
    
    private Long id;
    private String name;
    private String description;

}
