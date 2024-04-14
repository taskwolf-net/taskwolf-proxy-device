package net.taskwolf.proxy.device;

import com.google.inject.Injector;
import net.taskwolf.core.distribution.packet.PacketEventRepository;
import net.taskwolf.core.distribution.packet.PacketRegistry;
import net.taskwolf.core.event.HookRegistry;
import net.taskwolf.core.log.Log;
import net.taskwolf.device.distribution.command.event.CommandRequestEvent;
import net.taskwolf.device.distribution.command.event.CommandResponseEvent;
import net.taskwolf.device.distribution.command.packet.incoming.PacketIncomingCommandRequest;
import net.taskwolf.device.distribution.command.packet.incoming.PacketIncomingCommandResponse;
import net.taskwolf.device.distribution.device.event.DeviceLoginEvent;
import net.taskwolf.device.distribution.device.event.DeviceLogoutEvent;
import net.taskwolf.device.distribution.device.packet.incoming.PacketIncomingDeviceLogin;
import net.taskwolf.device.distribution.device.packet.incoming.PacketIncomingDeviceLogout;
import net.taskwolf.device.distribution.file.event.FileInfoRequestEvent;
import net.taskwolf.device.distribution.file.event.FileInfoResponseEvent;
import net.taskwolf.device.distribution.file.event.FileStorageRequestEvent;
import net.taskwolf.device.distribution.file.event.FileStorageResponseEvent;
import net.taskwolf.device.distribution.file.packet.incoming.PacketIncomingFileInfoRequest;
import net.taskwolf.device.distribution.file.packet.incoming.PacketIncomingFileInfoResponse;
import net.taskwolf.device.distribution.file.packet.incoming.PacketIncomingFileStorageRequest;
import net.taskwolf.device.distribution.file.packet.incoming.PacketIncomingFileStorageResponse;
import net.taskwolf.device.distribution.notification.event.NotificationRequestEvent;
import net.taskwolf.device.distribution.notification.event.NotificationResponseEvent;
import net.taskwolf.device.distribution.notification.packet.incoming.PacketIncomingNotificationRequest;
import net.taskwolf.device.distribution.notification.packet.incoming.PacketIncomingNotificationResponse;
import net.taskwolf.proxy.device.distribution.command.CommandRequestHook;
import net.taskwolf.proxy.device.distribution.command.CommandResponseHook;
import net.taskwolf.proxy.device.distribution.device.DeviceLoginHook;
import net.taskwolf.proxy.device.distribution.device.DeviceLogoutHook;
import net.taskwolf.proxy.device.distribution.file.FileInfoRequestHook;
import net.taskwolf.proxy.device.distribution.file.FileInfoResponseHook;
import net.taskwolf.proxy.device.distribution.file.FileStorageRequestHook;
import net.taskwolf.proxy.device.distribution.file.FileStorageResponseHook;
import net.taskwolf.proxy.device.distribution.notification.NotificationRequestHook;
import net.taskwolf.proxy.module.ProxyModule;
import net.taskwolf.proxy.module.ProxyModuleDescription;
import net.taskwolf.proxy.module.ProxyModuleLoadPriority;

@ProxyModuleDescription(name = "device", version = "1.0.0-SNAPSHOT",
  priority = ProxyModuleLoadPriority.NEUTRAL)
public final class DeviceModule extends ProxyModule {
  private Log log;

  public DeviceModule(Injector injector) {
    super(injector.createChildInjector(DeviceInjectionModule.create()));
  }

  @Override
  public void enable() throws Exception {
    log = injector().getInstance(Log.class).subLog("Device");
    registerPackets();
    registerPacketEvents();
    registerHooks();
  }

  private void registerPackets() throws Exception {
    var packetRegistry = injector().getInstance(PacketRegistry.class);
    packetRegistry.registerPacket(PacketIncomingDeviceLogin.class);
    packetRegistry.registerPacket(PacketIncomingDeviceLogout.class);
    packetRegistry.registerPacket(PacketIncomingNotificationRequest.class);
    packetRegistry.registerPacket(PacketIncomingNotificationResponse.class);
    packetRegistry.registerPacket(PacketIncomingCommandRequest.class);
    packetRegistry.registerPacket(PacketIncomingCommandResponse.class);
    packetRegistry.registerPacket(PacketIncomingFileStorageRequest.class);
    packetRegistry.registerPacket(PacketIncomingFileStorageResponse.class);
    packetRegistry.registerPacket(PacketIncomingFileInfoRequest.class);
    packetRegistry.registerPacket(PacketIncomingFileInfoResponse.class);
  }

  private void registerPacketEvents() {
    var packetEventRepository = injector().getInstance(PacketEventRepository.class);
    packetEventRepository.registerEvent(PacketIncomingDeviceLogin.class,
      (client, packet) -> DeviceLoginEvent.create(packet.deviceId(), client));
    packetEventRepository.registerEvent(PacketIncomingDeviceLogout.class,
      (client, packet) -> DeviceLogoutEvent.create(packet.deviceId()));
    registerNotificationPacketEvents(packetEventRepository);
    registerCommandPacketEvents(packetEventRepository);
    registerFilePacketEvents(packetEventRepository);
  }

  private void registerNotificationPacketEvents(PacketEventRepository repository) {
    repository.registerEvent(PacketIncomingNotificationRequest.class,
      (client, packet) -> NotificationRequestEvent.create(packet.notificationId(),
        packet.deviceId(), packet.title(), packet.body(), client));
    repository.registerEvent(PacketIncomingNotificationResponse.class,
      (client, packet) -> NotificationResponseEvent.create(packet.notificationId(),
        packet.delivered()));
  }

  private void registerCommandPacketEvents(PacketEventRepository repository) {
    repository.registerEvent(PacketIncomingCommandRequest.class,
      (client, packet) -> CommandRequestEvent.create(packet.commandId(),
        packet.deviceId(), packet.command(), client));
    repository.registerEvent(PacketIncomingCommandResponse.class,
      (client, packet) -> CommandResponseEvent.create(packet.commandId(),
        packet.delivered(), packet.output(), packet.errorMessage(),
        packet.exitCode()));
  }

  private void registerFilePacketEvents(PacketEventRepository repository) {
    repository.registerEvent(PacketIncomingFileStorageRequest.class,
      (client, packet) -> FileStorageRequestEvent.create(packet.storageId(),
        packet.deviceId(), packet.path(), client));
    repository.registerEvent(PacketIncomingFileStorageResponse.class,
      (client, packet) -> FileStorageResponseEvent.create(packet.storageId(),
        packet.success()));
    repository.registerEvent(PacketIncomingFileInfoRequest.class,
      (client, packet) -> FileInfoRequestEvent.create(packet.infoId(),
        packet.deviceId(), packet.path(), client));
    repository.registerEvent(PacketIncomingFileInfoResponse.class,
      (client, packet) -> FileInfoResponseEvent.create(packet.infoId(),
        packet.success()));
  }

  private void registerHooks() {
    var hookRegistry = injector().getInstance(HookRegistry.class);
    hookRegistry.register(injector().getInstance(DeviceLoginHook.class));
    hookRegistry.register(injector().getInstance(DeviceLogoutHook.class));
    hookRegistry.register(injector().getInstance(NotificationRequestHook.class));
    hookRegistry.register(injector().getInstance(CommandRequestHook.class));
    hookRegistry.register(injector().getInstance(CommandResponseHook.class));
    hookRegistry.register(injector().getInstance(FileStorageRequestHook.class));
    hookRegistry.register(injector().getInstance(FileStorageResponseHook.class));
    hookRegistry.register(injector().getInstance(FileInfoRequestHook.class));
    hookRegistry.register(injector().getInstance(FileInfoResponseHook.class));
  }

  @Override
  public void disable() {

  }
}