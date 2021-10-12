package dev.poncio.SystemApps.InstantNotificationSdk.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public class ResponseEntity {
    
    @Getter
	private Boolean status;
	@Getter
    private Integer code;
	@Getter
    private String message;
	@Getter
    private Object attach;

}
