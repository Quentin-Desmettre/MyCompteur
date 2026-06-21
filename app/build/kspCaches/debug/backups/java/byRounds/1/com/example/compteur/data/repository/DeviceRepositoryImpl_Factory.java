package com.example.compteur.data.repository;

import com.example.compteur.data.db.dao.DeviceDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class DeviceRepositoryImpl_Factory implements Factory<DeviceRepositoryImpl> {
  private final Provider<DeviceDao> deviceDaoProvider;

  public DeviceRepositoryImpl_Factory(Provider<DeviceDao> deviceDaoProvider) {
    this.deviceDaoProvider = deviceDaoProvider;
  }

  @Override
  public DeviceRepositoryImpl get() {
    return newInstance(deviceDaoProvider.get());
  }

  public static DeviceRepositoryImpl_Factory create(Provider<DeviceDao> deviceDaoProvider) {
    return new DeviceRepositoryImpl_Factory(deviceDaoProvider);
  }

  public static DeviceRepositoryImpl newInstance(DeviceDao deviceDao) {
    return new DeviceRepositoryImpl(deviceDao);
  }
}
