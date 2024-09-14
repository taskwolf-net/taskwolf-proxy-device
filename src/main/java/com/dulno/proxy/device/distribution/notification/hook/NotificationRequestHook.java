package com.dulno.proxy.device.distribution.notification.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.device.distribution.notification.packet.outgoing.PacketOutgoingNotificationRequest;
import com.dulno.device.distribution.notification.packet.outgoing.PacketOutgoingNotificationResponse;
import com.dulno.proxy.device.distribution.device.DeviceRepository;
import com.dulno.proxy.device.distribution.notification.event.ProxyNotificationRequestEvent;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class NotificationRequestHook implements Hook {
  private final DeviceRepository deviceRepository;

  @EventHook
  private void notificationRequest(ProxyNotificationRequestEvent event) {
    var client = deviceRepository.findDeviceClient(event.deviceId());
    if (client.isEmpty()) {
      event.client().sendPacket(new PacketOutgoingNotificationResponse(
        event.notificationId(), false));
      return;
    }
    client.get().sendPacket(new PacketOutgoingNotificationRequest(
      event.notificationId(), event.deviceId(), event.title(), event.body()));
    event.client().sendPacket(new PacketOutgoingNotificationResponse(
      event.notificationId(), true));
  }
}
