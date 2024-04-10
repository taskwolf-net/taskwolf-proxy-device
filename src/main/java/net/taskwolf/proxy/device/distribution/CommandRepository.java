package net.taskwolf.proxy.device.distribution;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.distribution.client.DistributionClient;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class CommandRepository {
  private final Map<UUID, DistributionClient> commands = Maps.newHashMap();

  public void registerCommand(UUID command, DistributionClient client) {
    commands.put(command, client);
  }

  public void unregisterCommand(UUID command) {
    commands.remove(command);
  }

  public Optional<DistributionClient> findCommandClient(UUID command) {
    return Optional.ofNullable(commands.get(command));
  }
}

