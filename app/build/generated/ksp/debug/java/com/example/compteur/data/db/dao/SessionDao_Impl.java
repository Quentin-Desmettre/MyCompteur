package com.example.compteur.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.compteur.data.db.entity.SessionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class SessionDao_Impl implements SessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SessionEntity> __insertionAdapterOfSessionEntity;

  private final EntityDeletionOrUpdateAdapter<SessionEntity> __deletionAdapterOfSessionEntity;

  private final EntityDeletionOrUpdateAdapter<SessionEntity> __updateAdapterOfSessionEntity;

  public SessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSessionEntity = new EntityInsertionAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sessions` (`id`,`routeId`,`startedAt`,`endedAt`,`totalDistanceMeters`,`avgSpeedKph`,`maxSpeedKph`,`avgPowerWatts`,`avgHeartRateBpm`,`totalAscentMeters`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getRouteId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getRouteId());
        }
        statement.bindLong(3, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndedAt());
        }
        statement.bindDouble(5, entity.getTotalDistanceMeters());
        statement.bindDouble(6, entity.getAvgSpeedKph());
        statement.bindDouble(7, entity.getMaxSpeedKph());
        if (entity.getAvgPowerWatts() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getAvgPowerWatts());
        }
        if (entity.getAvgHeartRateBpm() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getAvgHeartRateBpm());
        }
        statement.bindDouble(10, entity.getTotalAscentMeters());
      }
    };
    this.__deletionAdapterOfSessionEntity = new EntityDeletionOrUpdateAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sessions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSessionEntity = new EntityDeletionOrUpdateAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sessions` SET `id` = ?,`routeId` = ?,`startedAt` = ?,`endedAt` = ?,`totalDistanceMeters` = ?,`avgSpeedKph` = ?,`maxSpeedKph` = ?,`avgPowerWatts` = ?,`avgHeartRateBpm` = ?,`totalAscentMeters` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getRouteId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getRouteId());
        }
        statement.bindLong(3, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndedAt());
        }
        statement.bindDouble(5, entity.getTotalDistanceMeters());
        statement.bindDouble(6, entity.getAvgSpeedKph());
        statement.bindDouble(7, entity.getMaxSpeedKph());
        if (entity.getAvgPowerWatts() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getAvgPowerWatts());
        }
        if (entity.getAvgHeartRateBpm() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getAvgHeartRateBpm());
        }
        statement.bindDouble(10, entity.getTotalAscentMeters());
        statement.bindLong(11, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final SessionEntity session, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSessionEntity.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final SessionEntity session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSessionEntity.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SessionEntity session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSessionEntity.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SessionEntity>> getAllSessions() {
    final String _sql = "SELECT * FROM sessions ORDER BY startedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfTotalDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceMeters");
          final int _cursorIndexOfAvgSpeedKph = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKph");
          final int _cursorIndexOfMaxSpeedKph = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeedKph");
          final int _cursorIndexOfAvgPowerWatts = CursorUtil.getColumnIndexOrThrow(_cursor, "avgPowerWatts");
          final int _cursorIndexOfAvgHeartRateBpm = CursorUtil.getColumnIndexOrThrow(_cursor, "avgHeartRateBpm");
          final int _cursorIndexOfTotalAscentMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAscentMeters");
          final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SessionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpRouteId;
            if (_cursor.isNull(_cursorIndexOfRouteId)) {
              _tmpRouteId = null;
            } else {
              _tmpRouteId = _cursor.getLong(_cursorIndexOfRouteId);
            }
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final float _tmpTotalDistanceMeters;
            _tmpTotalDistanceMeters = _cursor.getFloat(_cursorIndexOfTotalDistanceMeters);
            final float _tmpAvgSpeedKph;
            _tmpAvgSpeedKph = _cursor.getFloat(_cursorIndexOfAvgSpeedKph);
            final float _tmpMaxSpeedKph;
            _tmpMaxSpeedKph = _cursor.getFloat(_cursorIndexOfMaxSpeedKph);
            final Integer _tmpAvgPowerWatts;
            if (_cursor.isNull(_cursorIndexOfAvgPowerWatts)) {
              _tmpAvgPowerWatts = null;
            } else {
              _tmpAvgPowerWatts = _cursor.getInt(_cursorIndexOfAvgPowerWatts);
            }
            final Integer _tmpAvgHeartRateBpm;
            if (_cursor.isNull(_cursorIndexOfAvgHeartRateBpm)) {
              _tmpAvgHeartRateBpm = null;
            } else {
              _tmpAvgHeartRateBpm = _cursor.getInt(_cursorIndexOfAvgHeartRateBpm);
            }
            final float _tmpTotalAscentMeters;
            _tmpTotalAscentMeters = _cursor.getFloat(_cursorIndexOfTotalAscentMeters);
            _item = new SessionEntity(_tmpId,_tmpRouteId,_tmpStartedAt,_tmpEndedAt,_tmpTotalDistanceMeters,_tmpAvgSpeedKph,_tmpMaxSpeedKph,_tmpAvgPowerWatts,_tmpAvgHeartRateBpm,_tmpTotalAscentMeters);
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

  @Override
  public Object getSessionById(final long sessionId,
      final Continuation<? super SessionEntity> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SessionEntity>() {
      @Override
      @Nullable
      public SessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfTotalDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceMeters");
          final int _cursorIndexOfAvgSpeedKph = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKph");
          final int _cursorIndexOfMaxSpeedKph = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeedKph");
          final int _cursorIndexOfAvgPowerWatts = CursorUtil.getColumnIndexOrThrow(_cursor, "avgPowerWatts");
          final int _cursorIndexOfAvgHeartRateBpm = CursorUtil.getColumnIndexOrThrow(_cursor, "avgHeartRateBpm");
          final int _cursorIndexOfTotalAscentMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAscentMeters");
          final SessionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpRouteId;
            if (_cursor.isNull(_cursorIndexOfRouteId)) {
              _tmpRouteId = null;
            } else {
              _tmpRouteId = _cursor.getLong(_cursorIndexOfRouteId);
            }
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final float _tmpTotalDistanceMeters;
            _tmpTotalDistanceMeters = _cursor.getFloat(_cursorIndexOfTotalDistanceMeters);
            final float _tmpAvgSpeedKph;
            _tmpAvgSpeedKph = _cursor.getFloat(_cursorIndexOfAvgSpeedKph);
            final float _tmpMaxSpeedKph;
            _tmpMaxSpeedKph = _cursor.getFloat(_cursorIndexOfMaxSpeedKph);
            final Integer _tmpAvgPowerWatts;
            if (_cursor.isNull(_cursorIndexOfAvgPowerWatts)) {
              _tmpAvgPowerWatts = null;
            } else {
              _tmpAvgPowerWatts = _cursor.getInt(_cursorIndexOfAvgPowerWatts);
            }
            final Integer _tmpAvgHeartRateBpm;
            if (_cursor.isNull(_cursorIndexOfAvgHeartRateBpm)) {
              _tmpAvgHeartRateBpm = null;
            } else {
              _tmpAvgHeartRateBpm = _cursor.getInt(_cursorIndexOfAvgHeartRateBpm);
            }
            final float _tmpTotalAscentMeters;
            _tmpTotalAscentMeters = _cursor.getFloat(_cursorIndexOfTotalAscentMeters);
            _result = new SessionEntity(_tmpId,_tmpRouteId,_tmpStartedAt,_tmpEndedAt,_tmpTotalDistanceMeters,_tmpAvgSpeedKph,_tmpMaxSpeedKph,_tmpAvgPowerWatts,_tmpAvgHeartRateBpm,_tmpTotalAscentMeters);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
