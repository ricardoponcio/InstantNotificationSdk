package dev.poncio.SystemApps.InstantNotificationSdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dev.poncio.SystemApps.InstantNotificationSdk.job.INCBroker;
import dev.poncio.SystemApps.InstantNotificationSdk.job.INCConnector;
import dev.poncio.SystemApps.InstantNotificationSdk.job.INCJob;
import dev.poncio.SystemApps.InstantNotificationSdk.job.INCWatcher;

@SpringBootApplication
public class InstantNotificationSdkApplication {

	private static final INCWatcher WATCHER = INCConnector.get()
			.initialize(INCBroker.builder().url("localhost:4049").token("").build());
	private static final Logger logger = LoggerFactory.getLogger(InstantNotificationSdkApplication.class);

	private static final Integer TEST_LOOPS = 100;
	private static final Integer TEST_MILLIS_BETWEEN_LOOPS = 500;

	public static void main(String[] args) {
		SpringApplication.run(InstantNotificationSdkApplication.class, args);
	}

	@Bean
	public void init() {
		try {
			INCJob job = WATCHER.createNewJob("Teste 123", "Testing SDK jobs!");
			for (int i = 0; i < TEST_LOOPS; i++) {
				WATCHER.addNewUpdate(job, INCWatcher.calcPercent(i, TEST_LOOPS).intValue(),
						"In a loop (" + INCWatcher.calcPercentStr(i, TEST_LOOPS) + ")...");
				Thread.sleep(TEST_MILLIS_BETWEEN_LOOPS);
			}
			job = WATCHER.finalizeJob(job);
		} catch (Exception e) {
			logger.error("Fail to schedule job!", e);
		}
	}

}
