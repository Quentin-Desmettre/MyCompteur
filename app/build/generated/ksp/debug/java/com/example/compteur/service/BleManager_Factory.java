package com.example.compteur.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

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
public final class BleManager_Factory implements Factory<BleManager> {
  private final Provider<CoroutineScope> scopeProvider;

  public BleManager_Factory(Provider<CoroutineScope> scopeProvider) {
    this.scopeProvider = scopeProvider;
  }

  @Override
  public BleManager get() {
    return newInstance(scopeProvider.get());
  }

  public static BleManager_Factory create(Provider<CoroutineScope> scopeProvider) {
    return new BleManager_Factory(scopeProvider);
  }

  public static BleManager newInstance(CoroutineScope scope) {
    return new BleManager(scope);
  }
}
