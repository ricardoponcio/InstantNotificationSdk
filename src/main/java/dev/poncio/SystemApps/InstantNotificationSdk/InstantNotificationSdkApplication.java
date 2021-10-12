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

	private static final INCWatcher WATCHER = INCConnector.get().initialize(
			INCBroker.builder().url("http://localhost:4049").token("0f3427b1227045dc8b84baca4989d365").build());
	private static final Logger logger = LoggerFactory.getLogger(InstantNotificationSdkApplication.class);

	private static final Integer TEST_LOOPS = 20;
	private static final Integer TEST_MILLIS_BETWEEN_LOOPS = 1500;
	private static final Integer TEST_MILLIS_BEFORE_LOOPS = 1000;

	public static void main(String[] args) {
		SpringApplication.run(InstantNotificationSdkApplication.class, args);
	}

	@Bean
	public void init() {
		INCJob job = null;
		try {
			job = WATCHER.createNewJob("Teste 123", "Testing SDK jobs!");
			if (TEST_MILLIS_BEFORE_LOOPS != null) {
				Thread.sleep(TEST_MILLIS_BEFORE_LOOPS);
			}
			for (int i = 0; i < TEST_LOOPS; i++) {
				WATCHER.addNewUpdate(job, INCWatcher.calcPercent(i, TEST_LOOPS).intValue(),
						"In a loop (" + INCWatcher.calcPercentStr(i, TEST_LOOPS) + ")...");
				Thread.sleep(TEST_MILLIS_BETWEEN_LOOPS);
			}
			job = WATCHER.finalizeJob(job, true, "Concluído!");
		} catch (Exception e) {
			try {
				if (job != null)
					job = WATCHER.finalizeJob(job, false, e.getMessage());
			} catch (Exception ex) {
				logger.error("Fail to finalize job!", e);
			}
			logger.error("Fail to schedule job!", e);
		} finally {
			WATCHER.stopThreadUpdater();
		}
	}

}
