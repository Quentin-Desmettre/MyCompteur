package com.example.compteur;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.example.compteur.data.api.LiveTrackingApi;
import com.example.compteur.data.api.StravaApi;
import com.example.compteur.data.db.AppDatabase;
import com.example.compteur.data.db.dao.DeviceDao;
import com.example.compteur.data.db.dao.GpsPointDao;
import com.example.compteur.data.db.dao.RouteDao;
import com.example.compteur.data.db.dao.RoutePointDao;
import com.example.compteur.data.db.dao.SensorDataDao;
import com.example.compteur.data.db.dao.SessionDao;
import com.example.compteur.data.gpx.GpxParser;
import com.example.compteur.data.repository.DeviceRepositoryImpl;
import com.example.compteur.data.repository.LiveTrackingRepositoryImpl;
import com.example.compteur.data.repository.RouteRepositoryImpl;
import com.example.compteur.data.repository.SessionRepositoryImpl;
import com.example.compteur.data.repository.SettingsRepository;
import com.example.compteur.data.repository.StravaRepositoryImpl;
import com.example.compteur.di.AppModule_ProvideApplicationScopeFactory;
import com.example.compteur.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.example.compteur.di.DatabaseModule_ProvideDeviceDaoFactory;
import com.example.compteur.di.DatabaseModule_ProvideGpsPointDaoFactory;
import com.example.compteur.di.DatabaseModule_ProvideRouteDaoFactory;
import com.example.compteur.di.DatabaseModule_ProvideRoutePointDaoFactory;
import com.example.compteur.di.DatabaseModule_ProvideSensorDataDaoFactory;
import com.example.compteur.di.DatabaseModule_ProvideSessionDaoFactory;
import com.example.compteur.di.NetworkModule_ProvideLiveTrackingApiFactory;
import com.example.compteur.di.NetworkModule_ProvideMoshiFactory;
import com.example.compteur.di.NetworkModule_ProvideOkHttpClientFactory;
import com.example.compteur.di.NetworkModule_ProvideStravaApiFactory;
import com.example.compteur.di.ParserModule_ProvideGpxParserFactory;
import com.example.compteur.domain.repository.DeviceRepository;
import com.example.compteur.domain.repository.RouteRepository;
import com.example.compteur.domain.repository.SessionRepository;
import com.example.compteur.domain.usecase.DeleteRouteUseCase;
import com.example.compteur.domain.usecase.GetRoutePointsUseCase;
import com.example.compteur.domain.usecase.GetRouteUseCase;
import com.example.compteur.domain.usecase.GetRoutesUseCase;
import com.example.compteur.domain.usecase.ImportGpxUseCase;
import com.example.compteur.domain.usecase.StartSessionUseCase;
import com.example.compteur.service.BleManager;
import com.example.compteur.service.HeartRateZoneService;
import com.example.compteur.service.OfflineMapManager;
import com.example.compteur.service.RecordingService;
import com.example.compteur.service.RecordingService_MembersInjector;
import com.example.compteur.ui.dashboard.DashboardViewModel;
import com.example.compteur.ui.dashboard.DashboardViewModel_HiltModules;
import com.example.compteur.ui.history.HistoryViewModel;
import com.example.compteur.ui.history.HistoryViewModel_HiltModules;
import com.example.compteur.ui.recording.RecordingViewModel;
import com.example.compteur.ui.recording.RecordingViewModel_HiltModules;
import com.example.compteur.ui.route_detail.RouteDetailViewModel;
import com.example.compteur.ui.route_detail.RouteDetailViewModel_HiltModules;
import com.example.compteur.ui.session_detail.SessionDetailViewModel;
import com.example.compteur.ui.session_detail.SessionDetailViewModel_HiltModules;
import com.example.compteur.ui.settings.SettingsViewModel;
import com.example.compteur.ui.settings.SettingsViewModel_HiltModules;
import com.squareup.moshi.Moshi;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineScope;
import okhttp3.OkHttpClient;

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
public final class DaggerCompteurApplication_HiltComponents_SingletonC {
  private DaggerCompteurApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public CompteurApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements CompteurApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public CompteurApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements CompteurApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public CompteurApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements CompteurApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public CompteurApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements CompteurApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CompteurApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements CompteurApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CompteurApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements CompteurApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public CompteurApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements CompteurApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public CompteurApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends CompteurApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends CompteurApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends CompteurApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends CompteurApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(6).put(LazyClassKeyProvider.com_example_compteur_ui_dashboard_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_compteur_ui_history_HistoryViewModel, HistoryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_compteur_ui_recording_RecordingViewModel, RecordingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_compteur_ui_route_detail_RouteDetailViewModel, RouteDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_compteur_ui_session_detail_SessionDetailViewModel, SessionDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_example_compteur_ui_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectStravaRepository(instance, singletonCImpl.stravaRepositoryImplProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_example_compteur_ui_session_detail_SessionDetailViewModel = "com.example.compteur.ui.session_detail.SessionDetailViewModel";

      static String com_example_compteur_ui_recording_RecordingViewModel = "com.example.compteur.ui.recording.RecordingViewModel";

      static String com_example_compteur_ui_history_HistoryViewModel = "com.example.compteur.ui.history.HistoryViewModel";

      static String com_example_compteur_ui_dashboard_DashboardViewModel = "com.example.compteur.ui.dashboard.DashboardViewModel";

      static String com_example_compteur_ui_route_detail_RouteDetailViewModel = "com.example.compteur.ui.route_detail.RouteDetailViewModel";

      static String com_example_compteur_ui_settings_SettingsViewModel = "com.example.compteur.ui.settings.SettingsViewModel";

      @KeepFieldType
      SessionDetailViewModel com_example_compteur_ui_session_detail_SessionDetailViewModel2;

      @KeepFieldType
      RecordingViewModel com_example_compteur_ui_recording_RecordingViewModel2;

      @KeepFieldType
      HistoryViewModel com_example_compteur_ui_history_HistoryViewModel2;

      @KeepFieldType
      DashboardViewModel com_example_compteur_ui_dashboard_DashboardViewModel2;

      @KeepFieldType
      RouteDetailViewModel com_example_compteur_ui_route_detail_RouteDetailViewModel2;

      @KeepFieldType
      SettingsViewModel com_example_compteur_ui_settings_SettingsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends CompteurApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<RecordingViewModel> recordingViewModelProvider;

    private Provider<RouteDetailViewModel> routeDetailViewModelProvider;

    private Provider<SessionDetailViewModel> sessionDetailViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetRoutesUseCase getRoutesUseCase() {
      return new GetRoutesUseCase(singletonCImpl.bindRouteRepositoryProvider.get());
    }

    private ImportGpxUseCase importGpxUseCase() {
      return new ImportGpxUseCase(singletonCImpl.bindRouteRepositoryProvider.get());
    }

    private DeleteRouteUseCase deleteRouteUseCase() {
      return new DeleteRouteUseCase(singletonCImpl.bindRouteRepositoryProvider.get());
    }

    private GetRoutePointsUseCase getRoutePointsUseCase() {
      return new GetRoutePointsUseCase(singletonCImpl.bindRouteRepositoryProvider.get());
    }

    private StartSessionUseCase startSessionUseCase() {
      return new StartSessionUseCase(singletonCImpl.bindSessionRepositoryProvider.get());
    }

    private GetRouteUseCase getRouteUseCase() {
      return new GetRouteUseCase(singletonCImpl.bindRouteRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.recordingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.routeDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.sessionDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(6).put(LazyClassKeyProvider.com_example_compteur_ui_dashboard_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_example_compteur_ui_history_HistoryViewModel, ((Provider) historyViewModelProvider)).put(LazyClassKeyProvider.com_example_compteur_ui_recording_RecordingViewModel, ((Provider) recordingViewModelProvider)).put(LazyClassKeyProvider.com_example_compteur_ui_route_detail_RouteDetailViewModel, ((Provider) routeDetailViewModelProvider)).put(LazyClassKeyProvider.com_example_compteur_ui_session_detail_SessionDetailViewModel, ((Provider) sessionDetailViewModelProvider)).put(LazyClassKeyProvider.com_example_compteur_ui_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_example_compteur_ui_recording_RecordingViewModel = "com.example.compteur.ui.recording.RecordingViewModel";

      static String com_example_compteur_ui_settings_SettingsViewModel = "com.example.compteur.ui.settings.SettingsViewModel";

      static String com_example_compteur_ui_route_detail_RouteDetailViewModel = "com.example.compteur.ui.route_detail.RouteDetailViewModel";

      static String com_example_compteur_ui_session_detail_SessionDetailViewModel = "com.example.compteur.ui.session_detail.SessionDetailViewModel";

      static String com_example_compteur_ui_dashboard_DashboardViewModel = "com.example.compteur.ui.dashboard.DashboardViewModel";

      static String com_example_compteur_ui_history_HistoryViewModel = "com.example.compteur.ui.history.HistoryViewModel";

      @KeepFieldType
      RecordingViewModel com_example_compteur_ui_recording_RecordingViewModel2;

      @KeepFieldType
      SettingsViewModel com_example_compteur_ui_settings_SettingsViewModel2;

      @KeepFieldType
      RouteDetailViewModel com_example_compteur_ui_route_detail_RouteDetailViewModel2;

      @KeepFieldType
      SessionDetailViewModel com_example_compteur_ui_session_detail_SessionDetailViewModel2;

      @KeepFieldType
      DashboardViewModel com_example_compteur_ui_dashboard_DashboardViewModel2;

      @KeepFieldType
      HistoryViewModel com_example_compteur_ui_history_HistoryViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.example.compteur.ui.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(viewModelCImpl.getRoutesUseCase(), viewModelCImpl.importGpxUseCase(), viewModelCImpl.deleteRouteUseCase(), viewModelCImpl.getRoutePointsUseCase(), singletonCImpl.settingsRepositoryProvider.get());

          case 1: // com.example.compteur.ui.history.HistoryViewModel 
          return (T) new HistoryViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.bindSessionRepositoryProvider.get());

          case 2: // com.example.compteur.ui.recording.RecordingViewModel 
          return (T) new RecordingViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), viewModelCImpl.savedStateHandle, viewModelCImpl.startSessionUseCase(), viewModelCImpl.getRoutesUseCase(), viewModelCImpl.getRoutePointsUseCase(), singletonCImpl.settingsRepositoryProvider.get(), singletonCImpl.bleManagerProvider.get(), singletonCImpl.heartRateZoneServiceProvider.get());

          case 3: // com.example.compteur.ui.route_detail.RouteDetailViewModel 
          return (T) new RouteDetailViewModel(viewModelCImpl.savedStateHandle, viewModelCImpl.getRouteUseCase(), viewModelCImpl.getRoutePointsUseCase());

          case 4: // com.example.compteur.ui.session_detail.SessionDetailViewModel 
          return (T) new SessionDetailViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.bindSessionRepositoryProvider.get(), singletonCImpl.stravaRepositoryImplProvider.get(), viewModelCImpl.savedStateHandle);

          case 5: // com.example.compteur.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.bleManagerProvider.get(), singletonCImpl.bindDeviceRepositoryProvider.get(), singletonCImpl.settingsRepositoryProvider.get(), singletonCImpl.offlineMapManagerProvider.get(), singletonCImpl.heartRateZoneServiceProvider.get(), singletonCImpl.stravaRepositoryImplProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends CompteurApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends CompteurApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectRecordingService(RecordingService recordingService) {
      injectRecordingService2(recordingService);
    }

    private RecordingService injectRecordingService2(RecordingService instance) {
      RecordingService_MembersInjector.injectGpsDao(instance, singletonCImpl.gpsPointDao());
      RecordingService_MembersInjector.injectSensorDataDao(instance, singletonCImpl.sensorDataDao());
      RecordingService_MembersInjector.injectBleManager(instance, singletonCImpl.bleManagerProvider.get());
      RecordingService_MembersInjector.injectDeviceRepository(instance, singletonCImpl.bindDeviceRepositoryProvider.get());
      RecordingService_MembersInjector.injectSessionRepository(instance, singletonCImpl.bindSessionRepositoryProvider.get());
      RecordingService_MembersInjector.injectRouteRepository(instance, singletonCImpl.bindRouteRepositoryProvider.get());
      RecordingService_MembersInjector.injectLiveTrackingRepository(instance, singletonCImpl.liveTrackingRepositoryImplProvider.get());
      RecordingService_MembersInjector.injectSettingsRepository(instance, singletonCImpl.settingsRepositoryProvider.get());
      RecordingService_MembersInjector.injectAppScope(instance, singletonCImpl.provideApplicationScopeProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends CompteurApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Moshi> provideMoshiProvider;

    private Provider<StravaApi> provideStravaApiProvider;

    private Provider<SettingsRepository> settingsRepositoryProvider;

    private Provider<StravaRepositoryImpl> stravaRepositoryImplProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<GpxParser> provideGpxParserProvider;

    private Provider<RouteRepositoryImpl> routeRepositoryImplProvider;

    private Provider<RouteRepository> bindRouteRepositoryProvider;

    private Provider<SessionRepositoryImpl> sessionRepositoryImplProvider;

    private Provider<SessionRepository> bindSessionRepositoryProvider;

    private Provider<CoroutineScope> provideApplicationScopeProvider;

    private Provider<BleManager> bleManagerProvider;

    private Provider<HeartRateZoneService> heartRateZoneServiceProvider;

    private Provider<DeviceRepositoryImpl> deviceRepositoryImplProvider;

    private Provider<DeviceRepository> bindDeviceRepositoryProvider;

    private Provider<OfflineMapManager> offlineMapManagerProvider;

    private Provider<LiveTrackingApi> provideLiveTrackingApiProvider;

    private Provider<LiveTrackingRepositoryImpl> liveTrackingRepositoryImplProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private RouteDao routeDao() {
      return DatabaseModule_ProvideRouteDaoFactory.provideRouteDao(provideAppDatabaseProvider.get());
    }

    private RoutePointDao routePointDao() {
      return DatabaseModule_ProvideRoutePointDaoFactory.provideRoutePointDao(provideAppDatabaseProvider.get());
    }

    private SessionDao sessionDao() {
      return DatabaseModule_ProvideSessionDaoFactory.provideSessionDao(provideAppDatabaseProvider.get());
    }

    private GpsPointDao gpsPointDao() {
      return DatabaseModule_ProvideGpsPointDaoFactory.provideGpsPointDao(provideAppDatabaseProvider.get());
    }

    private SensorDataDao sensorDataDao() {
      return DatabaseModule_ProvideSensorDataDaoFactory.provideSensorDataDao(provideAppDatabaseProvider.get());
    }

    private DeviceDao deviceDao() {
      return DatabaseModule_ProvideDeviceDaoFactory.provideDeviceDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 2));
      this.provideMoshiProvider = DoubleCheck.provider(new SwitchingProvider<Moshi>(singletonCImpl, 3));
      this.provideStravaApiProvider = DoubleCheck.provider(new SwitchingProvider<StravaApi>(singletonCImpl, 1));
      this.settingsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepository>(singletonCImpl, 4));
      this.stravaRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<StravaRepositoryImpl>(singletonCImpl, 0));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 6));
      this.provideGpxParserProvider = DoubleCheck.provider(new SwitchingProvider<GpxParser>(singletonCImpl, 7));
      this.routeRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 5);
      this.bindRouteRepositoryProvider = DoubleCheck.provider((Provider) routeRepositoryImplProvider);
      this.sessionRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 8);
      this.bindSessionRepositoryProvider = DoubleCheck.provider((Provider) sessionRepositoryImplProvider);
      this.provideApplicationScopeProvider = DoubleCheck.provider(new SwitchingProvider<CoroutineScope>(singletonCImpl, 10));
      this.bleManagerProvider = DoubleCheck.provider(new SwitchingProvider<BleManager>(singletonCImpl, 9));
      this.heartRateZoneServiceProvider = DoubleCheck.provider(new SwitchingProvider<HeartRateZoneService>(singletonCImpl, 11));
      this.deviceRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 12);
      this.bindDeviceRepositoryProvider = DoubleCheck.provider((Provider) deviceRepositoryImplProvider);
      this.offlineMapManagerProvider = DoubleCheck.provider(new SwitchingProvider<OfflineMapManager>(singletonCImpl, 13));
      this.provideLiveTrackingApiProvider = DoubleCheck.provider(new SwitchingProvider<LiveTrackingApi>(singletonCImpl, 15));
      this.liveTrackingRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<LiveTrackingRepositoryImpl>(singletonCImpl, 14));
    }

    @Override
    public void injectCompteurApplication(CompteurApplication compteurApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.example.compteur.data.repository.StravaRepositoryImpl 
          return (T) new StravaRepositoryImpl(singletonCImpl.provideStravaApiProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          case 1: // com.example.compteur.data.api.StravaApi 
          return (T) NetworkModule_ProvideStravaApiFactory.provideStravaApi(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideMoshiProvider.get());

          case 2: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 3: // com.squareup.moshi.Moshi 
          return (T) NetworkModule_ProvideMoshiFactory.provideMoshi();

          case 4: // com.example.compteur.data.repository.SettingsRepository 
          return (T) new SettingsRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.example.compteur.data.repository.RouteRepositoryImpl 
          return (T) new RouteRepositoryImpl(singletonCImpl.routeDao(), singletonCImpl.routePointDao(), singletonCImpl.provideGpxParserProvider.get());

          case 6: // com.example.compteur.data.db.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.example.compteur.data.gpx.GpxParser 
          return (T) ParserModule_ProvideGpxParserFactory.provideGpxParser();

          case 8: // com.example.compteur.data.repository.SessionRepositoryImpl 
          return (T) new SessionRepositoryImpl(singletonCImpl.sessionDao(), singletonCImpl.gpsPointDao(), singletonCImpl.sensorDataDao());

          case 9: // com.example.compteur.service.BleManager 
          return (T) new BleManager(singletonCImpl.provideApplicationScopeProvider.get());

          case 10: // kotlinx.coroutines.CoroutineScope 
          return (T) AppModule_ProvideApplicationScopeFactory.provideApplicationScope();

          case 11: // com.example.compteur.service.HeartRateZoneService 
          return (T) new HeartRateZoneService();

          case 12: // com.example.compteur.data.repository.DeviceRepositoryImpl 
          return (T) new DeviceRepositoryImpl(singletonCImpl.deviceDao());

          case 13: // com.example.compteur.service.OfflineMapManager 
          return (T) new OfflineMapManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 14: // com.example.compteur.data.repository.LiveTrackingRepositoryImpl 
          return (T) new LiveTrackingRepositoryImpl(singletonCImpl.provideLiveTrackingApiProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          case 15: // com.example.compteur.data.api.LiveTrackingApi 
          return (T) NetworkModule_ProvideLiveTrackingApiFactory.provideLiveTrackingApi(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideMoshiProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
