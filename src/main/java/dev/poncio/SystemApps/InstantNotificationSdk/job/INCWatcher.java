package dev.poncio.SystemApps.InstantNotificationSdk.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import dev.poncio.SystemApps.InstantNotificationSdk.dto.ResponseEntity;
import dev.poncio.SystemApps.InstantNotificationSdk.services.HttpService;
import dev.poncio.SystemApps.InstantNotificationSdk.services.HttpService.MetodoRequisicao;

public class INCWatcher {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean connectionSuccess = false;
    private INCThreadUpdater threadUpdater;

    @Autowired
    private HttpService httpService;

    private INCBroker brokerConfiguration;

    protected INCWatcher(INCBroker brokerConfiguration) {
        this.brokerConfiguration = brokerConfiguration;
        this.threadUpdater = new INCThreadUpdater(brokerConfiguration);
        this.threadUpdater.start();
    }

    protected boolean makeTestConnection() {
        try {
            ResponseEntity result = this.httpService.makeReq(MetodoRequisicao.POST,
                    this.brokerConfiguration.getUrl() + "/sdk/test", null, null, null);
            this.connectionSuccess = result != null && result.getStatus() != null && result.getStatus().booleanValue();
            return this.connectionSuccess;
        } catch (Exception e) {
            logger.error("Fail to test connection!", e);
            return false;
        }
    }

    public boolean isConnectionSuccess() {
        return this.connectionSuccess;
    }

    public INCJob createNewJob(String name, String description) throws Exception {
        try {
            INCJob job = new INCJob(null, name, description);
            ResponseEntity resultCreateJob = this.httpService.makeReq(MetodoRequisicao.POST,
                    this.brokerConfiguration + "/job/create", job, null, null);
            if (resultCreateJob != null && resultCreateJob.getStatus() != null
                    && resultCreateJob.getStatus().booleanValue()) {
                return (INCJob) resultCreateJob.getAttach();
            } else
                throw new Exception("Was not possible to create the job!");
        } catch (Exception e) {
            throw e;
        }
    }

    public ResponseEntity addNewUpdate(INCJob job, Integer percent, String log) throws Exception {
        try {
            INCJobUpdate jobUpdate = new INCJobUpdate(percent, log, job, null);
            this.threadUpdater.addUpdate2Sync(jobUpdate);
            if (!this.brokerConfiguration.isMakeAsyncCalls()) {
                return this.threadUpdater.getResultBlock(jobUpdate);
            }
            return null;
        } catch (Exception e) {
            throw e;
        }
    }

    public INCJob finalizeJob(INCJob job) throws Exception {
        try {
            if (job == null || job.getId() == null)
                throw new Exception("Job provided is not a synchronized job");
            ResponseEntity resultCreateJob = this.httpService.makeReq(MetodoRequisicao.POST,
                    this.brokerConfiguration + "/job/finalize/" + job.getId(), null, null, null);
            if (resultCreateJob != null && resultCreateJob.getStatus() != null
                    && resultCreateJob.getStatus().booleanValue()) {
                return (INCJob) resultCreateJob.getAttach();
            } else
                throw new Exception("Was not possible to finalize the job!");
        } catch (Exception e) {
            throw e;
        }
    }

    public static <T> Float calcPercent(T actual, List<T> objects) {
        return calcPercent(objects.indexOf(actual), objects.size());
    }

    public static Float calcPercent(Integer idx, Integer size) {
        return ((float) idx / (float) size) * ((float) 100);
    }

    public static <T> String calcPercentStr(T actual, List<T> objects) {
        return calcPercentStr(objects.indexOf(actual), objects.size());
    }

    public static <T> String calcPercentStr(Integer idx, Integer size) {
        return String.format("%.2f%%", calcPercent(idx, size));
    }

}
