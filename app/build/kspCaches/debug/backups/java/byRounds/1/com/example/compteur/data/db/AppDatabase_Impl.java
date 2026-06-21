package com.example.compteur.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.compteur.data.db.dao.DeviceDao;
import com.example.compteur.data.db.dao.DeviceDao_Impl;
import com.example.compteur.data.db.dao.GpsPointDao;
import com.example.compteur.data.db.dao.GpsPointDao_Impl;
import com.example.compteur.data.db.dao.RouteDao;
import com.example.compteur.data.db.dao.RouteDao_Impl;
import com.example.compteur.data.db.dao.RoutePointDao;
import com.example.compteur.data.db.dao.RoutePointDao_Impl;
import com.example.compteur.data.db.dao.SensorDataDao;
import com.example.compteur.data.db.dao.SensorDataDao_Impl;
import com.example.compteur.data.db.dao.SessionDao;
import com.example.compteur.data.db.dao.SessionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile SessionDao _sessionDao;

  private volatile GpsPointDao _gpsPointDao;

  private volatile SensorDataDao _sensorDataDao;

  private volatile RouteDao _routeDao;

  private volatile RoutePointDao _routePointDao;

  private volatile DeviceDao _deviceDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routeId` INTEGER, `startedAt` INTEGER NOT NULL, `endedAt` INTEGER, `totalDistanceMeters` REAL NOT NULL, `avgSpeedKph` REAL NOT NULL, `maxSpeedKph` REAL NOT NULL, `avgPowerWatts` INTEGER, `avgHeartRateBpm` INTEGER, `totalAscentMeters` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `gps_points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `altitudeMeters` REAL NOT NULL, `speedMps` REAL NOT NULL, `timestampMs` INTEGER NOT NULL, FOREIGN KEY(`sessionId`) REFERENCES `sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_gps_points_sessionId` ON `gps_points` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sensor_data` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `timestampMs` INTEGER NOT NULL, `powerWatts` INTEGER, `cadenceRpm` INTEGER, `heartRateBpm` INTEGER, `bleSpeedKph` REAL, FOREIGN KEY(`sessionId`) REFERENCES `sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sensor_data_sessionId` ON `sensor_data` (`sessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `routes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `totalDistanceMeters` REAL NOT NULL, `totalAscentMeters` REAL NOT NULL, `dateImported` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `route_points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routeId` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `altitudeMeters` REAL, `sequenceOrder` INTEGER NOT NULL, FOREIGN KEY(`routeId`) REFERENCES `routes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_route_points_routeId` ON `route_points` (`routeId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `synchronized_devices` (`macAddress` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, PRIMARY KEY(`macAddress`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '320dc01bd521e4aa901f40095882862b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `sessions`");
        db.execSQL("DROP TABLE IF EXISTS `gps_points`");
        db.execSQL("DROP TABLE IF EXISTS `sensor_data`");
        db.execSQL("DROP TABLE IF EXISTS `routes`");
        db.execSQL("DROP TABLE IF EXISTS `route_points`");
        db.execSQL("DROP TABLE IF EXISTS `synchronized_devices`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsSessions = new HashMap<String, TableInfo.Column>(10);
        _columnsSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("routeId", new TableInfo.Column("routeId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("endedAt", new TableInfo.Column("endedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("totalDistanceMeters", new TableInfo.Column("totalDistanceMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("avgSpeedKph", new TableInfo.Column("avgSpeedKph", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("maxSpeedKph", new TableInfo.Column("maxSpeedKph", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("avgPowerWatts", new TableInfo.Column("avgPowerWatts", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("avgHeartRateBpm", new TableInfo.Column("avgHeartRateBpm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("totalAscentMeters", new TableInfo.Column("totalAscentMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSessions = new TableInfo("sessions", _columnsSessions, _foreignKeysSessions, _indicesSessions);
        final TableInfo _existingSessions = TableInfo.read(db, "sessions");
        if (!_infoSessions.equals(_existingSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "sessions(com.example.compteur.data.db.entity.SessionEntity).\n"
                  + " Expected:\n" + _infoSessions + "\n"
                  + " Found:\n" + _existingSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsGpsPoints = new HashMap<String, TableInfo.Column>(7);
        _columnsGpsPoints.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGpsPoints.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGpsPoints.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGpsPoints.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGpsPoints.put("altitudeMeters", new TableInfo.Column("altitudeMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGpsPoints.put("speedMps", new TableInfo.Column("speedMps", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGpsPoints.put("timestampMs", new TableInfo.Column("timestampMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGpsPoints = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysGpsPoints.add(new TableInfo.ForeignKey("sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesGpsPoints = new HashSet<TableInfo.Index>(1);
        _indicesGpsPoints.add(new TableInfo.Index("index_gps_points_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoGpsPoints = new TableInfo("gps_points", _columnsGpsPoints, _foreignKeysGpsPoints, _indicesGpsPoints);
        final TableInfo _existingGpsPoints = TableInfo.read(db, "gps_points");
        if (!_infoGpsPoints.equals(_existingGpsPoints)) {
          return new RoomOpenHelper.ValidationResult(false, "gps_points(com.example.compteur.data.db.entity.GpsPointEntity).\n"
                  + " Expected:\n" + _infoGpsPoints + "\n"
                  + " Found:\n" + _existingGpsPoints);
        }
        final HashMap<String, TableInfo.Column> _columnsSensorData = new HashMap<String, TableInfo.Column>(7);
        _columnsSensorData.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("timestampMs", new TableInfo.Column("timestampMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("powerWatts", new TableInfo.Column("powerWatts", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("cadenceRpm", new TableInfo.Column("cadenceRpm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("heartRateBpm", new TableInfo.Column("heartRateBpm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSensorData.put("bleSpeedKph", new TableInfo.Column("bleSpeedKph", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSensorData = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSensorData.add(new TableInfo.ForeignKey("sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSensorData = new HashSet<TableInfo.Index>(1);
        _indicesSensorData.add(new TableInfo.Index("index_sensor_data_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        final TableInfo _infoSensorData = new TableInfo("sensor_data", _columnsSensorData, _foreignKeysSensorData, _indicesSensorData);
        final TableInfo _existingSensorData = TableInfo.read(db, "sensor_data");
        if (!_infoSensorData.equals(_existingSensorData)) {
          return new RoomOpenHelper.ValidationResult(false, "sensor_data(com.example.compteur.data.db.entity.SensorDataEntity).\n"
                  + " Expected:\n" + _infoSensorData + "\n"
                  + " Found:\n" + _existingSensorData);
        }
        final HashMap<String, TableInfo.Column> _columnsRoutes = new HashMap<String, TableInfo.Column>(5);
        _columnsRoutes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("totalDistanceMeters", new TableInfo.Column("totalDistanceMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("totalAscentMeters", new TableInfo.Column("totalAscentMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("dateImported", new TableInfo.Column("dateImported", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRoutes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRoutes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRoutes = new TableInfo("routes", _columnsRoutes, _foreignKeysRoutes, _indicesRoutes);
        final TableInfo _existingRoutes = TableInfo.read(db, "routes");
        if (!_infoRoutes.equals(_existingRoutes)) {
          return new RoomOpenHelper.ValidationResult(false, "routes(com.example.compteur.data.db.entity.RouteEntity).\n"
                  + " Expected:\n" + _infoRoutes + "\n"
                  + " Found:\n" + _existingRoutes);
        }
        final HashMap<String, TableInfo.Column> _columnsRoutePoints = new HashMap<String, TableInfo.Column>(6);
        _columnsRoutePoints.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutePoints.put("routeId", new TableInfo.Column("routeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutePoints.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutePoints.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutePoints.put("altitudeMeters", new TableInfo.Column("altitudeMeters", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutePoints.put("sequenceOrder", new TableInfo.Column("sequenceOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRoutePoints = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRoutePoints.add(new TableInfo.ForeignKey("routes", "CASCADE", "NO ACTION", Arrays.asList("routeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRoutePoints = new HashSet<TableInfo.Index>(1);
        _indicesRoutePoints.add(new TableInfo.Index("index_route_points_routeId", false, Arrays.asList("routeId"), Arrays.asList("ASC")));
        final TableInfo _infoRoutePoints = new TableInfo("route_points", _columnsRoutePoints, _foreignKeysRoutePoints, _indicesRoutePoints);
        final TableInfo _existingRoutePoints = TableInfo.read(db, "route_points");
        if (!_infoRoutePoints.equals(_existingRoutePoints)) {
          return new RoomOpenHelper.ValidationResult(false, "route_points(com.example.compteur.data.db.entity.RoutePointEntity).\n"
                  + " Expected:\n" + _infoRoutePoints + "\n"
                  + " Found:\n" + _existingRoutePoints);
        }
        final HashMap<String, TableInfo.Column> _columnsSynchronizedDevices = new HashMap<String, TableInfo.Column>(3);
        _columnsSynchronizedDevices.put("macAddress", new TableInfo.Column("macAddress", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSynchronizedDevices.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSynchronizedDevices.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSynchronizedDevices = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSynchronizedDevices = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSynchronizedDevices = new TableInfo("synchronized_devices", _columnsSynchronizedDevices, _foreignKeysSynchronizedDevices, _indicesSynchronizedDevices);
        final TableInfo _existingSynchronizedDevices = TableInfo.read(db, "synchronized_devices");
        if (!_infoSynchronizedDevices.equals(_existingSynchronizedDevices)) {
          return new RoomOpenHelper.ValidationResult(false, "synchronized_devices(com.example.compteur.data.db.entity.SynchronizedDeviceEntity).\n"
                  + " Expected:\n" + _infoSynchronizedDevices + "\n"
                  + " Found:\n" + _existingSynchronizedDevices);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "320dc01bd521e4aa901f40095882862b", "d19e56b3239fca460cc44d00092ef373");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "sessions","gps_points","sensor_data","routes","route_points","synchronized_devices");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `sessions`");
      _db.execSQL("DELETE FROM `gps_points`");
      _db.execSQL("DELETE FROM `sensor_data`");
      _db.execSQL("DELETE FROM `routes`");
      _db.execSQL("DELETE FROM `route_points`");
      _db.execSQL("DELETE FROM `synchronized_devices`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(SessionDao.class, SessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GpsPointDao.class, GpsPointDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SensorDataDao.class, SensorDataDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RouteDao.class, RouteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RoutePointDao.class, RoutePointDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DeviceDao.class, DeviceDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public SessionDao sessionDao() {
    if (_sessionDao != null) {
      return _sessionDao;
    } else {
      synchronized(this) {
        if(_sessionDao == null) {
          _sessionDao = new SessionDao_Impl(this);
        }
        return _sessionDao;
      }
    }
  }

  @Override
  public GpsPointDao gpsPointDao() {
    if (_gpsPointDao != null) {
      return _gpsPointDao;
    } else {
      synchronized(this) {
        if(_gpsPointDao == null) {
          _gpsPointDao = new GpsPointDao_Impl(this);
        }
        return _gpsPointDao;
      }
    }
  }

  @Override
  public SensorDataDao sensorDataDao() {
    if (_sensorDataDao != null) {
      return _sensorDataDao;
    } else {
      synchronized(this) {
        if(_sensorDataDao == null) {
          _sensorDataDao = new SensorDataDao_Impl(this);
        }
        return _sensorDataDao;
      }
    }
  }

  @Override
  public RouteDao routeDao() {
    if (_routeDao != null) {
      return _routeDao;
    } else {
      synchronized(this) {
        if(_routeDao == null) {
          _routeDao = new RouteDao_Impl(this);
        }
        return _routeDao;
      }
    }
  }

  @Override
  public RoutePointDao routePointDao() {
    if (_routePointDao != null) {
      return _routePointDao;
    } else {
      synchronized(this) {
        if(_routePointDao == null) {
          _routePointDao = new RoutePointDao_Impl(this);
        }
        return _routePointDao;
      }
    }
  }

  @Override
  public DeviceDao deviceDao() {
    if (_deviceDao != null) {
      return _deviceDao;
    } else {
      synchronized(this) {
        if(_deviceDao == null) {
          _deviceDao = new DeviceDao_Impl(this);
        }
        return _deviceDao;
      }
    }
  }
}
