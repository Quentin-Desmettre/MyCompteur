package com.example.compteur.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class HeartRateZoneService_Factory implements Factory<HeartRateZoneService> {
  @Override
  public HeartRateZoneService get() {
    return newInstance();
  }

  public static HeartRateZoneService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HeartRateZoneService newInstance() {
    return new HeartRateZoneService();
  }

  private static final class InstanceHolder {
    private static final HeartRateZoneService_Factory INSTANCE = new HeartRateZoneService_Factory();
  }
}
