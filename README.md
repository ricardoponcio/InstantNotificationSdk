# InstantNotificationSdk

InstantNotificationSdk is a Java SDK designed to easily synchronize job execution updates from your backend applications to an [InstantNotificationCenter](https://github.com/ricardoponcio/InstantNotificationCenter) server. It allows you to create, update, and finalize jobs, sending real-time progress and log information to a centralized monitoring dashboard.

## Features

- **Easy Integration:** Add real-time job tracking to any Java backend with minimal code.
- **Job Lifecycle Management:** Create jobs, send progress updates, and finalize jobs with success or failure status.
- **Asynchronous or Synchronous Updates:** Choose between async (default) or blocking update calls.
- **Authentication Support:** Secure communication with the broker using tokens.
- **Helper Utilities:** Includes utilities for HTTP requests and parameter handling.

## How It Works

The SDK communicates with an InstantNotificationCenter broker via HTTP, sending job events (creation, updates, finalization) as your application progresses through its tasks. This enables centralized, real-time monitoring of distributed or scheduled jobs.

## Usage Example

```java
INCBroker broker = INCBroker.builder()
    .url("http://localhost:4049")
    .token("YOUR_TOKEN")
    .build();

INCWatcher watcher = INCConnector.get().initialize(broker);

INCJob job = watcher.createNewJob("My Job", "Description of my job");
for (int i = 0; i < 10; i++) {
    watcher.addNewUpdate(job, INCWatcher.calcPercent(i, 10).intValue(), "Step " + i + " completed");
}
watcher.finalizeJob(job, true, "Job finished successfully");
watcher.stopThreadUpdater();
```
