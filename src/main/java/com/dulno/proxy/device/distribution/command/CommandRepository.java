package com.dulno.proxy.device.distribution.command;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.proxy.device.distribution.device.DeviceRequestEntry;
import com.dulno.proxy.distribution.client.ProxyClient;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class CommandRepository {
  private final Map<DeviceRequestEntry, ScheduledFuture<?>> commands = Maps.newHashMap();
  private final ScheduledExecutorService executorService =
    Executors.newSingleThreadScheduledExecutor();

  public void registerCommand(UUID command, ProxyClient client) {
    var schedule = executorService.schedule(() -> unregisterCommand(command),
      10, TimeUnit.SECONDS);
    commands.put(DeviceRequestEntry.create(command, client), schedule);
  }

  public void unregisterCommand(UUID command) {
    var clientOptional = commands.keySet().stream()
      .filter(request -> request.id().equals(command))
      .findFirst();
    if (clientOptional.isEmpty()) {
      return;
    }
    var content = clientOptional.get();
    commands.get(content).cancel(true);
    commands.remove(content);
  }

  public Optional<ProxyClient> findCommandClient(UUID command) {
    return commands.keySet().stream()
      .filter(request -> request.id().equals(command))
      .map(DeviceRequestEntry::client)
      .findFirst();
  }
}

