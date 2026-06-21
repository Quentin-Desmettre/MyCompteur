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
public final class GetRouteUseCase_Factory implements Factory<GetRouteUseCase> {
  private final Provider<RouteRepository> repositoryProvider;

  public GetRouteUseCase_Factory(Provider<RouteRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetRouteUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetRouteUseCase_Factory create(Provider<RouteRepository> repositoryProvider) {
    return new GetRouteUseCase_Factory(repositoryProvider);
  }

  public static GetRouteUseCase newInstance(RouteRepository repository) {
    return new GetRouteUseCase(repository);
  }
}
