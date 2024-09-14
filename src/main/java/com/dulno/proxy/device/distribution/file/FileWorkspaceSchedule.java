package com.dulno.proxy.device.distribution.file;

import lombok.RequiredArgsConstructor;
import com.dulno.device.DeviceConfiguration;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(staticName = "create")
public final class FileWorkspaceSchedule {
  private final DeviceConfiguration deviceConfiguration;
  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> scheduler;

  private static final int WORKSPACE_CHECK_INITIAL_DELAY = 10;
  private static final int WORKSPACE_CHECK_INTERVAL = 10;
  private static final TimeUnit WORKSPACE_CHECK_TIME_UNIT = TimeUnit.SECONDS;

  public void start() {
    scheduler = executorService.scheduleAtFixedRate(this::execute,
      WORKSPACE_CHECK_INITIAL_DELAY, WORKSPACE_CHECK_INTERVAL,
      WORKSPACE_CHECK_TIME_UNIT);
  }

  private static final String FIREBASE_URL = "https://fcm.googleapis.com/fcm/send";

  private void execute() {
    var requestBody = new JSONObject(Map.of("to", "/topics/dulno-workspace-monitor",
      "data", Map.of("workspaceMonitor", true)));
    var requestBuilder = HttpRequest.newBuilder().uri(URI.create(FIREBASE_URL))
      .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
      .setHeader("Content-Type", "application/json")
      .setHeader("Authorization", "key=" + deviceConfiguration.firebaseToken())
      .build();
    HttpClient.newHttpClient().sendAsync(requestBuilder,
      HttpResponse.BodyHandlers.ofByteArray());
  }

  public void stop() {
    scheduler.cancel(false);
  }
}
