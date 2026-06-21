package com.example.compteur.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.compteur.data.db.entity.SensorDataEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SensorDataDao_Impl implements SensorDataDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SensorDataEntity> __insertionAdapterOfSensorDataEntity;

  public SensorDataDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSensorDataEntity = new EntityInsertionAdapter<SensorDataEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sensor_data` (`id`,`sessionId`,`timestampMs`,`powerWatts`,`cadenceRpm`,`heartRateBpm`,`bleSpeedKph`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SensorDataEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        statement.bindLong(3, entity.getTimestampMs());
        if (entity.getPowerWatts() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getPowerWatts());
        }
        if (entity.getCadenceRpm() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getCadenceRpm());
        }
        if (entity.getHeartRateBpm() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getHeartRateBpm());
        }
        if (entity.getBleSpeedKph() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getBleSpeedKph());
        }
      }
    };
  }

  @Override
  public Object insertBatch(final List<SensorDataEntity> data,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSensorDataEntity.insert(data);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SensorDataEntity>> getDataForSession(final long sessionId) {
    final String _sql = "SELECT * FROM sensor_data WHERE sessionId = ? ORDER BY timestampMs ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sensor_data"}, new Callable<List<SensorDataEntity>>() {
      @Override
      @NonNull
      public List<SensorDataEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfPowerWatts = CursorUtil.getColumnIndexOrThrow(_cursor, "powerWatts");
          final int _cursorIndexOfCadenceRpm = CursorUtil.getColumnIndexOrThrow(_cursor, "cadenceRpm");
          final int _cursorIndexOfHeartRateBpm = CursorUtil.getColumnIndexOrThrow(_cursor, "heartRateBpm");
          final int _cursorIndexOfBleSpeedKph = CursorUtil.getColumnIndexOrThrow(_cursor, "bleSpeedKph");
          final List<SensorDataEntity> _result = new ArrayList<SensorDataEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SensorDataEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final Integer _tmpPowerWatts;
            if (_cursor.isNull(_cursorIndexOfPowerWatts)) {
              _tmpPowerWatts = null;
            } else {
              _tmpPowerWatts = _cursor.getInt(_cursorIndexOfPowerWatts);
            }
            final Integer _tmpCadenceRpm;
            if (_cursor.isNull(_cursorIndexOfCadenceRpm)) {
              _tmpCadenceRpm = null;
            } else {
              _tmpCadenceRpm = _cursor.getInt(_cursorIndexOfCadenceRpm);
            }
            final Integer _tmpHeartRateBpm;
            if (_cursor.isNull(_cursorIndexOfHeartRateBpm)) {
              _tmpHeartRateBpm = null;
            } else {
              _tmpHeartRateBpm = _cursor.getInt(_cursorIndexOfHeartRateBpm);
            }
            final Float _tmpBleSpeedKph;
            if (_cursor.isNull(_cursorIndexOfBleSpeedKph)) {
              _tmpBleSpeedKph = null;
            } else {
              _tmpBleSpeedKph = _cursor.getFloat(_cursorIndexOfBleSpeedKph);
            }
            _item = new SensorDataEntity(_tmpId,_tmpSessionId,_tmpTimestampMs,_tmpPowerWatts,_tmpCadenceRpm,_tmpHeartRateBpm,_tmpBleSpeedKph);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
