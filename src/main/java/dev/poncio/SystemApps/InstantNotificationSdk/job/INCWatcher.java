package dev.poncio.SystemApps.InstantNotificationSdk.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import dev.poncio.SystemApps.InstantNotificationSdk.dto.ResponseEntity;
import dev.poncio.SystemApps.InstantNotificationSdk.utils.HttpUtils;
import dev.poncio.SystemApps.InstantNotificationSdk.utils.HttpUtils.MetodoRequisicao;

@Configurable
public class INCWatcher {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean connectionSuccess = false;
    private INCThreadUpdater threadUpdater;

    private INCBroker brokerConfiguration;

    protected INCWatcher(INCBroker brokerConfiguration) {
        this.brokerConfiguration = brokerConfiguration;

        this.threadUpdater = new INCThreadUpdater(brokerConfiguration);
        this.threadUpdater.start();
    }

    public void stopThreadUpdater() {
        this.threadUpdater.stopAsyncThread();
    }

    protected boolean makeTestConnection() {
        try {
            ResponseEntity result = HttpUtils.get().makeReq(MetodoRequisicao.POST,
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
            ResponseEntity resultCreateJob = HttpUtils.get().makeReq(MetodoRequisicao.POST,
                    this.brokerConfiguration.getUrl() + "/job/create", job, null, doAuthMap(brokerConfiguration));
            if (resultCreateJob != null && resultCreateJob.getStatus() != null
                    && resultCreateJob.getStatus().booleanValue()) {
                return resultCreateJob.castObject(INCJob.class);
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

    public INCJob finalizeJob(INCJob job, boolean success, String resultMessage) throws Exception {
        try {
            if (job == null || job.getId() == null)
                throw new Exception("Job provided is not a synchronized job");
            ResponseEntity resultCreateJob = HttpUtils.get().makeReq(MetodoRequisicao.POST,
                    this.brokerConfiguration.getUrl() + "/job/finalize/" + job.getId(),
                    INCJobFinalize.builder().success(success).resultMessage(resultMessage).build(), null,
                    doAuthMap(brokerConfiguration));
            if (resultCreateJob != null && resultCreateJob.getStatus() != null
                    && resultCreateJob.getStatus().booleanValue()) {
                return resultCreateJob.castObject(INCJob.class);
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

    protected static Map<String, String> doAuthMap(INCBroker brokerConfiguration) {
        return new HashMap<String, String>() {
            {
                put("SDK-Auth", brokerConfiguration.getToken());
            }
        };
    }

}
