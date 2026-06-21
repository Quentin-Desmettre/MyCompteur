package com.example.compteur.ui.session_detail;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import com.example.compteur.domain.repository.SessionRepository;
import com.example.compteur.domain.repository.StravaRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SessionDetailViewModel_Factory implements Factory<SessionDetailViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<StravaRepository> stravaRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public SessionDetailViewModel_Factory(Provider<Context> contextProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<StravaRepository> stravaRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.contextProvider = contextProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.stravaRepositoryProvider = stravaRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public SessionDetailViewModel get() {
    return newInstance(contextProvider.get(), sessionRepositoryProvider.get(), stravaRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static SessionDetailViewModel_Factory create(Provider<Context> contextProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<StravaRepository> stravaRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new SessionDetailViewModel_Factory(contextProvider, sessionRepositoryProvider, stravaRepositoryProvider, savedStateHandleProvider);
  }

  public static SessionDetailViewModel newInstance(Context context,
      SessionRepository sessionRepository, StravaRepository stravaRepository,
      SavedStateHandle savedStateHandle) {
    return new SessionDetailViewModel(context, sessionRepository, stravaRepository, savedStateHandle);
  }
}
