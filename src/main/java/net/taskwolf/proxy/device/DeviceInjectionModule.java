package net.taskwolf.proxy.device;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.taskwolf.device.DeviceConfiguration;

@RequiredArgsConstructor(staticName = "create")
public final class DeviceInjectionModule extends AbstractModule {
  @Override
  protected void configure() {

  }

  @Provides
  @Singleton
  DeviceConfiguration provideDeviceConfiguration() throws Exception {
    return DeviceConfiguration.createAndLoad();
  }
}
