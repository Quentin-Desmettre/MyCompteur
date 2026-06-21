package com.example.compteur.service;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class OfflineMapManager_Factory implements Factory<OfflineMapManager> {
  private final Provider<Context> contextProvider;

  public OfflineMapManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OfflineMapManager get() {
    return newInstance(contextProvider.get());
  }

  public static OfflineMapManager_Factory create(Provider<Context> contextProvider) {
    return new OfflineMapManager_Factory(contextProvider);
  }

  public static OfflineMapManager newInstance(Context context) {
    return new OfflineMapManager(context);
  }
}
