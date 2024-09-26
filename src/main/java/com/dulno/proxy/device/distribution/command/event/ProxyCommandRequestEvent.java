package com.dulno.proxy.device.distribution.command.event;

import com.dulno.device.structure.DevicePlatform;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import com.dulno.core.event.Event;
import com.dulno.proxy.distribution.client.ProxyClient;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class ProxyCommandRequestEvent extends Event {
  private final UUID commandId;
  private final String deviceId;
  private final DevicePlatform devicePlatform;
  private final String command;
  private final ProxyClient client;
}
