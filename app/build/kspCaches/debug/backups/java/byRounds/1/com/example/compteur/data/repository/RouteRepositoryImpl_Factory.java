package com.example.compteur.data.repository;

import com.example.compteur.data.db.dao.RouteDao;
import com.example.compteur.data.db.dao.RoutePointDao;
import com.example.compteur.data.gpx.GpxParser;
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
public final class RouteRepositoryImpl_Factory implements Factory<RouteRepositoryImpl> {
  private final Provider<RouteDao> routeDaoProvider;

  private final Provider<RoutePointDao> routePointDaoProvider;

  private final Provider<GpxParser> gpxParserProvider;

  public RouteRepositoryImpl_Factory(Provider<RouteDao> routeDaoProvider,
      Provider<RoutePointDao> routePointDaoProvider, Provider<GpxParser> gpxParserProvider) {
    this.routeDaoProvider = routeDaoProvider;
    this.routePointDaoProvider = routePointDaoProvider;
    this.gpxParserProvider = gpxParserProvider;
  }

  @Override
  public RouteRepositoryImpl get() {
    return newInstance(routeDaoProvider.get(), routePointDaoProvider.get(), gpxParserProvider.get());
  }

  public static RouteRepositoryImpl_Factory create(Provider<RouteDao> routeDaoProvider,
      Provider<RoutePointDao> routePointDaoProvider, Provider<GpxParser> gpxParserProvider) {
    return new RouteRepositoryImpl_Factory(routeDaoProvider, routePointDaoProvider, gpxParserProvider);
  }

  public static RouteRepositoryImpl newInstance(RouteDao routeDao, RoutePointDao routePointDao,
      GpxParser gpxParser) {
    return new RouteRepositoryImpl(routeDao, routePointDao, gpxParser);
  }
}
