package net.taskwolf.proxy.device.distribution;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.taskwolf.core.event.EventHook;
import net.taskwolf.core.event.Hook;
import net.taskwolf.device.distribution.event.NotificationRequestEvent;
import net.taskwolf.device.distribution.packet.outgoing.PacketOutgoingNotificationRequest;
import net.taskwolf.device.distribution.packet.outgoing.PacketOutgoingNotificationResponse;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class NotificationRequestHook implements Hook {
  private final DeviceRepository deviceRepository;

  @EventHook
  private void notificationRequest(NotificationRequestEvent event) {
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
