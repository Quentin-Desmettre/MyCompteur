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
public final class DeleteRouteUseCase_Factory implements Factory<DeleteRouteUseCase> {
  private final Provider<RouteRepository> repositoryProvider;

  public DeleteRouteUseCase_Factory(Provider<RouteRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DeleteRouteUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static DeleteRouteUseCase_Factory create(Provider<RouteRepository> repositoryProvider) {
    return new DeleteRouteUseCase_Factory(repositoryProvider);
  }

  public static DeleteRouteUseCase newInstance(RouteRepository repository) {
    return new DeleteRouteUseCase(repository);
  }
}
