package com.example.compteur.data.repository;

import com.example.compteur.data.db.dao.GpsPointDao;
import com.example.compteur.data.db.dao.SensorDataDao;
import com.example.compteur.data.db.dao.SessionDao;
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
public final class SessionRepositoryImpl_Factory implements Factory<SessionRepositoryImpl> {
  private final Provider<SessionDao> sessionDaoProvider;

  private final Provider<GpsPointDao> gpsPointDaoProvider;

  private final Provider<SensorDataDao> sensorDataDaoProvider;

  public SessionRepositoryImpl_Factory(Provider<SessionDao> sessionDaoProvider,
      Provider<GpsPointDao> gpsPointDaoProvider, Provider<SensorDataDao> sensorDataDaoProvider) {
    this.sessionDaoProvider = sessionDaoProvider;
    this.gpsPointDaoProvider = gpsPointDaoProvider;
    this.sensorDataDaoProvider = sensorDataDaoProvider;
  }

  @Override
  public SessionRepositoryImpl get() {
    return newInstance(sessionDaoProvider.get(), gpsPointDaoProvider.get(), sensorDataDaoProvider.get());
  }

  public static SessionRepositoryImpl_Factory create(Provider<SessionDao> sessionDaoProvider,
      Provider<GpsPointDao> gpsPointDaoProvider, Provider<SensorDataDao> sensorDataDaoProvider) {
    return new SessionRepositoryImpl_Factory(sessionDaoProvider, gpsPointDaoProvider, sensorDataDaoProvider);
  }

  public static SessionRepositoryImpl newInstance(SessionDao sessionDao, GpsPointDao gpsPointDao,
      SensorDataDao sensorDataDao) {
    return new SessionRepositoryImpl(sessionDao, gpsPointDao, sensorDataDao);
  }
}
