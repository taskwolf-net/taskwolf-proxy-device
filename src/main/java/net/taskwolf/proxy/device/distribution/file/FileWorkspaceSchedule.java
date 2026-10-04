package net.taskwolf.proxy.device.distribution.file;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import net.taskwolf.device.DeviceConfiguration;
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
  private final GoogleCredentials googleCredentials;
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

  private static final String FIREBASE_URL =
    "https://fcm.googleapis.com/v1/projects/%s/messages:send";

  private void execute() {
    try {
      googleCredentials.refreshIfExpired();
      var token = googleCredentials.getAccessToken().getTokenValue();
      var requestBody = new JSONObject(Map.of("to", "/topics/taskwolf-workspace-monitor",
        "data", Map.of("workspaceMonitor", true)));
      var url = String.format(FIREBASE_URL, deviceConfiguration.firebaseProjectId());
      var requestBuilder = HttpRequest.newBuilder().uri(URI.create(url))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
        .setHeader("Content-Type", "application/json")
        .setHeader("Authorization", "Bearer " + token)
        .build();
      HttpClient.newHttpClient().sendAsync(requestBuilder,
        HttpResponse.BodyHandlers.ofByteArray());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
    scheduler.cancel(false);
  }
}
