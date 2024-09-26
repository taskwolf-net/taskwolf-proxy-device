package com.dulno.proxy.device;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.inject.Injector;
import com.dulno.core.event.HookRegistry;
import com.dulno.core.log.Log;
import com.dulno.core.packet.PacketEventRepository;
import com.dulno.core.packet.PacketRegistry;
import com.dulno.device.DeviceConfiguration;
import com.dulno.device.distribution.command.packet.incoming.PacketIncomingCommandRequest;
import com.dulno.device.distribution.command.packet.incoming.PacketIncomingCommandResponse;
import com.dulno.device.distribution.file.packet.incoming.*;
import com.dulno.device.distribution.notification.packet.incoming.PacketIncomingNotificationRequest;
import com.dulno.device.distribution.notification.packet.incoming.PacketIncomingNotificationResponse;
import com.dulno.proxy.device.distribution.command.event.ProxyCommandRequestEvent;
import com.dulno.proxy.device.distribution.command.event.ProxyCommandResponseEvent;
import com.dulno.proxy.device.distribution.command.hook.CommandRequestHook;
import com.dulno.proxy.device.distribution.command.hook.CommandResponseHook;
import com.dulno.proxy.device.distribution.device.event.DeviceLoginEvent;
import com.dulno.proxy.device.distribution.device.event.DeviceLogoutEvent;
import com.dulno.proxy.device.distribution.device.hook.DeviceLoginHook;
import com.dulno.proxy.device.distribution.device.hook.DeviceLogoutHook;
import com.dulno.proxy.device.distribution.device.hook.NodeDisconnectHook;
import com.dulno.proxy.device.distribution.device.incoming.PacketIncomingDeviceLogin;
import com.dulno.proxy.device.distribution.device.incoming.PacketIncomingDeviceLogout;
import com.dulno.proxy.device.distribution.file.*;
import com.dulno.proxy.device.distribution.file.event.*;
import com.dulno.proxy.device.distribution.file.hook.*;
import com.dulno.proxy.device.distribution.notification.event.ProxyNotificationRequestEvent;
import com.dulno.proxy.device.distribution.notification.hook.NotificationRequestHook;
import com.dulno.proxy.distribution.client.ProxyClient;
import com.dulno.proxy.module.ProxyModule;
import com.dulno.proxy.module.ProxyModuleDescription;
import com.dulno.proxy.module.ProxyModuleLoadPriority;

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
    FileWorkspaceSchedule.create(injector().getInstance(DeviceConfiguration.class),
      injector().getInstance(GoogleCredentials.class)).start();
  }

  private void registerPackets() throws Exception {
    var packetRegistry = injector().getInstance(PacketRegistry.class);
    packetRegistry.registerPacket(PacketIncomingDeviceLogin.class);
    packetRegistry.registerPacket(PacketIncomingDeviceLogout.class);
    packetRegistry.registerPacket(PacketIncomingNotificationRequest.class);
    packetRegistry.registerPacket(PacketIncomingNotificationResponse.class);
    packetRegistry.registerPacket(PacketIncomingFileStorageRedirectRequest.class);
    packetRegistry.registerPacket(PacketIncomingCommandRequest.class);
    packetRegistry.registerPacket(PacketIncomingCommandResponse.class);
    packetRegistry.registerPacket(PacketIncomingFileStorageRequest.class);
    packetRegistry.registerPacket(PacketIncomingFileStorageResponse.class);
    packetRegistry.registerPacket(PacketIncomingFileInfoRequest.class);
    packetRegistry.registerPacket(PacketIncomingFileInfoResponse.class);
    packetRegistry.registerPacket(PacketIncomingFileDeleteRequest.class);
    packetRegistry.registerPacket(PacketIncomingFileDeleteResponse.class);
  }

  private void registerPacketEvents() {
    var packetEventRepository = injector().getInstance(PacketEventRepository.class);
    packetEventRepository.<ProxyClient, PacketIncomingDeviceLogin>registerEvent(
      PacketIncomingDeviceLogin.class, (client, packet) ->
        DeviceLoginEvent.create(packet.deviceId(), client));
    packetEventRepository.registerEvent(PacketIncomingDeviceLogout.class,
      (client, packet) -> DeviceLogoutEvent.create(packet.deviceId()));
    registerNotificationPacketEvents(packetEventRepository);
    registerCommandPacketEvents(packetEventRepository);
    registerFilePacketEvents(packetEventRepository);
  }

  private void registerNotificationPacketEvents(PacketEventRepository repository) {
    repository.<ProxyClient, PacketIncomingNotificationRequest>registerEvent(
      PacketIncomingNotificationRequest.class, (client, packet) ->
        ProxyNotificationRequestEvent.create(packet.notificationId(),
          packet.deviceId(), packet.title(), packet.body(), client));
  }

  private void registerCommandPacketEvents(PacketEventRepository repository) {
    repository.<ProxyClient, PacketIncomingCommandRequest>registerEvent(
      PacketIncomingCommandRequest.class, (client, packet) ->
        ProxyCommandRequestEvent.create(packet.commandId(), packet.deviceId(),
          packet.devicePlatform(), packet.command(), client));
    repository.registerEvent(PacketIncomingCommandResponse.class,
      (client, packet) -> ProxyCommandResponseEvent.create(packet.commandId(),
        packet.delivered(), packet.output(), packet.errorMessage(),
        packet.exitCode()));
  }

  private void registerFilePacketEvents(PacketEventRepository repository) {
    repository.<ProxyClient, PacketIncomingFileStorageRequest>registerEvent(
      PacketIncomingFileStorageRequest.class, (client, packet) ->
        ProxyFileStorageRequestEvent.create(packet.storageId(), packet.deviceId(),
          packet.devicePlatform(), packet.filePath(), packet.fileName(), client));
    repository.registerEvent(PacketIncomingFileStorageResponse.class,
      (client, packet) -> ProxyFileStorageResponseEvent.create(packet.storageId(),
        packet.success()));
    repository.<ProxyClient, PacketIncomingFileStorageRedirectRequest>registerEvent(
      PacketIncomingFileStorageRedirectRequest.class, (client, packet) ->
        ProxyFileStorageRedirectRequestEvent.create(packet.storageId(), client));
    repository.<ProxyClient, PacketIncomingFileInfoRequest>registerEvent(
      PacketIncomingFileInfoRequest.class, (client, packet) ->
        ProxyFileInfoRequestEvent.create(packet.infoId(), packet.deviceId(),
          packet.devicePlatform(), packet.filePath(), packet.fileName(), client));
    repository.registerEvent(PacketIncomingFileInfoResponse.class,
      (client, packet) -> ProxyFileInfoResponseEvent.create(packet.infoId(),
        packet.content(), packet.success()));
    repository.<ProxyClient, PacketIncomingFileDeleteRequest>registerEvent(
      PacketIncomingFileDeleteRequest.class, (client, packet) ->
        ProxyFileDeleteRequestEvent.create(packet.deleteId(), packet.deviceId(),
          packet.devicePlatform(), packet.filePath(), packet.fileName(), client));
    repository.registerEvent(PacketIncomingFileDeleteResponse.class,
      (client, packet) -> ProxyFileDeleteResponseEvent.create(packet.deleteId(),
        packet.success()));
  }

  private void registerHooks() {
    var hookRegistry = injector().getInstance(HookRegistry.class);
    hookRegistry.register(injector().getInstance(DeviceLoginHook.class));
    hookRegistry.register(injector().getInstance(DeviceLogoutHook.class));
    hookRegistry.register(injector().getInstance(NodeDisconnectHook.class));
    hookRegistry.register(injector().getInstance(NotificationRequestHook.class));
    hookRegistry.register(injector().getInstance(CommandRequestHook.class));
    hookRegistry.register(injector().getInstance(CommandResponseHook.class));
    hookRegistry.register(injector().getInstance(FileStorageRequestHook.class));
    hookRegistry.register(injector().getInstance(FileStorageResponseHook.class));
    hookRegistry.register(injector().getInstance(FileStorageRedirectRequestHook.class));
    hookRegistry.register(injector().getInstance(FileInfoRequestHook.class));
    hookRegistry.register(injector().getInstance(FileInfoResponseHook.class));
    hookRegistry.register(injector().getInstance(FileDeleteRequestHook.class));
    hookRegistry.register(injector().getInstance(FileDeleteResponseHook.class));
  }

  @Override
  public void disable() {

  }
}