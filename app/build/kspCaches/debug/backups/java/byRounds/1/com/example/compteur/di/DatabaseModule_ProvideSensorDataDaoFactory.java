package com.example.compteur.di;

import com.example.compteur.data.db.AppDatabase;
import com.example.compteur.data.db.dao.SensorDataDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideSensorDataDaoFactory implements Factory<SensorDataDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideSensorDataDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SensorDataDao get() {
    return provideSensorDataDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSensorDataDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSensorDataDaoFactory(databaseProvider);
  }

  public static SensorDataDao provideSensorDataDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSensorDataDao(database));
  }
}
