package dev.poncio.SystemApps.InstantNotificationSdk.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import dev.poncio.SystemApps.InstantNotificationSdk.dto.ResponseEntity;
import dev.poncio.SystemApps.InstantNotificationSdk.utils.HttpUtils;
import dev.poncio.SystemApps.InstantNotificationSdk.utils.HttpUtils.MetodoRequisicao;

@Configurable
public class INCThreadUpdater extends Thread {

    private INCBroker brokerConfiguration;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BlockingQueue<INCJobUpdate> jobs2Sync = new ArrayBlockingQueue<>(1000);
    private List<INCJobUpdate> finalizedJobs = new ArrayList<>();
    private boolean running = true;

    protected INCThreadUpdater(INCBroker brokerConfiguration) {
        this.brokerConfiguration = brokerConfiguration;
    }

    @Override
    public void run() {
        if (this.brokerConfiguration == null) {
            logger.error("No one configuration provided in configure()");
            return;
        }

        while (running) {
            INCJobUpdate update = this.jobs2Sync.poll();
            if (update == null) {
                sleepWTC(500);
                continue;
            }

            try {
                if (update == null || (update.getPercent() == null
                        && (update.getLog() == null || update.getLog().trim().isEmpty())))
                    throw new Exception("No updates to sync!");
                if (update.getJob() == null || update.getJob().getId() == null)
                    throw new Exception("Job is not a synchronized job!");

                ResponseEntity result = HttpUtils.get().makeReq(MetodoRequisicao.POST,
                        this.brokerConfiguration.getUrl() + "/job/update/" + update.getJob().getId(), update, null,
                        INCWatcher.doAuthMap(this.brokerConfiguration));
                if (result == null) {
                    throw new Exception("No Result Received!");
                }
                update.setResultUpdate(result);
                if (result.getStatus() == null || !result.getStatus().booleanValue())
                    throw new Exception(result.getMessage());
            } catch (Exception e) {
                update.setResultUpdate(ResponseEntity.builder().status(false).message(e.getMessage()).build());
                logger.error("Failed to sync update in job!", e);
            }
        }
    }

    public void stopAsyncThread() {
        this.running = false;
    }

    private void sleepWTC(long millis) {
        try {
            sleep(millis);
        } catch (Exception e) {
            logger.error("Failed on sleep!", e);
        }
    }

    public synchronized boolean finished(INCJobUpdate update) {
        return this.finalizedJobs.stream().anyMatch(job -> job == update);
    }

    public synchronized ResponseEntity getResult(INCJobUpdate update) {
        return this.finalizedJobs.stream().filter(job -> job == update).map(job -> job.getResultUpdate()).findFirst()
                .orElse(null);
    }

    public synchronized ResponseEntity getResultBlock(INCJobUpdate update) {
        boolean isFinished = this.finished(update);
        while (!isFinished) {
            sleepWTC(500);
            isFinished = finished(update);
        }
        return getResult(update);
    }

    public synchronized boolean addUpdate2Sync(INCJobUpdate update) {
        return this.jobs2Sync.add(update);
    }

}
