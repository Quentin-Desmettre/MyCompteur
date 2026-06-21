package com.example.compteur.domain.usecase;

import com.example.compteur.domain.repository.SessionRepository;
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
public final class StartSessionUseCase_Factory implements Factory<StartSessionUseCase> {
  private final Provider<SessionRepository> repositoryProvider;

  public StartSessionUseCase_Factory(Provider<SessionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public StartSessionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static StartSessionUseCase_Factory create(Provider<SessionRepository> repositoryProvider) {
    return new StartSessionUseCase_Factory(repositoryProvider);
  }

  public static StartSessionUseCase newInstance(SessionRepository repository) {
    return new StartSessionUseCase(repository);
  }
}
