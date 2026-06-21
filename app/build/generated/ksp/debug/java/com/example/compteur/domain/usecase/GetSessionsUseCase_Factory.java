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
public final class GetSessionsUseCase_Factory implements Factory<GetSessionsUseCase> {
  private final Provider<SessionRepository> repositoryProvider;

  public GetSessionsUseCase_Factory(Provider<SessionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetSessionsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetSessionsUseCase_Factory create(Provider<SessionRepository> repositoryProvider) {
    return new GetSessionsUseCase_Factory(repositoryProvider);
  }

  public static GetSessionsUseCase newInstance(SessionRepository repository) {
    return new GetSessionsUseCase(repository);
  }
}
