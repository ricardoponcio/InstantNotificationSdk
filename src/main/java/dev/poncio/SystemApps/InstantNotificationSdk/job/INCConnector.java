package dev.poncio.SystemApps.InstantNotificationSdk.job;

public class INCConnector {
    
    private static INCConnector instance;

    private INCConnector() {
        
    }

    public static INCConnector get() {
        if (instance == null)
            instance = new INCConnector();
        return instance;
    }

    public INCWatcher initialize(INCBroker brokerConfiguration) {
        try {
            INCWatcher watcher = new INCWatcher(brokerConfiguration);
            boolean success = watcher.makeTestConnection();
            if (success) {
                this.onConnectionSuccess();
            } else {
                this.onConnectionError();
            }
            return watcher;
        } catch(Exception e) {
            this.onConnectionError();
        }
        return null;
    }

    public void onPreConnection() {

    };

    public void onConnectionSuccess() {

    };

    public void onConnectionError() {

    };

}
