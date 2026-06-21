package com.example.compteur.domain.usecase;

import com.example.compteur.domain.repository.RouteRepository;
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
public final class ImportGpxUseCase_Factory implements Factory<ImportGpxUseCase> {
  private final Provider<RouteRepository> repositoryProvider;

  public ImportGpxUseCase_Factory(Provider<RouteRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ImportGpxUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ImportGpxUseCase_Factory create(Provider<RouteRepository> repositoryProvider) {
    return new ImportGpxUseCase_Factory(repositoryProvider);
  }

  public static ImportGpxUseCase newInstance(RouteRepository repository) {
    return new ImportGpxUseCase(repository);
  }
}
