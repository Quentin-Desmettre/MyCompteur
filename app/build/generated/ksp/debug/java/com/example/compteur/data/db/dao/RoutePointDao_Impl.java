package com.example.compteur.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.compteur.data.db.entity.RoutePointEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
public final class RoutePointDao_Impl implements RoutePointDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RoutePointEntity> __insertionAdapterOfRoutePointEntity;

  public RoutePointDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRoutePointEntity = new EntityInsertionAdapter<RoutePointEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `route_points` (`id`,`routeId`,`latitude`,`longitude`,`altitudeMeters`,`sequenceOrder`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutePointEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRouteId());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        if (entity.getAltitudeMeters() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getAltitudeMeters());
        }
        statement.bindLong(6, entity.getSequenceOrder());
      }
    };
  }

  @Override
  public Object insertBatch(final List<RoutePointEntity> points,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRoutePointEntity.insert(points);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RoutePointEntity>> getPointsForRoute(final long routeId) {
    final String _sql = "SELECT * FROM route_points WHERE routeId = ? ORDER BY sequenceOrder ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, routeId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"route_points"}, new Callable<List<RoutePointEntity>>() {
      @Override
      @NonNull
      public List<RoutePointEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitudeMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "altitudeMeters");
          final int _cursorIndexOfSequenceOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sequenceOrder");
          final List<RoutePointEntity> _result = new ArrayList<RoutePointEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoutePointEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRouteId;
            _tmpRouteId = _cursor.getLong(_cursorIndexOfRouteId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Float _tmpAltitudeMeters;
            if (_cursor.isNull(_cursorIndexOfAltitudeMeters)) {
              _tmpAltitudeMeters = null;
            } else {
              _tmpAltitudeMeters = _cursor.getFloat(_cursorIndexOfAltitudeMeters);
            }
            final int _tmpSequenceOrder;
            _tmpSequenceOrder = _cursor.getInt(_cursorIndexOfSequenceOrder);
            _item = new RoutePointEntity(_tmpId,_tmpRouteId,_tmpLatitude,_tmpLongitude,_tmpAltitudeMeters,_tmpSequenceOrder);
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
  public Object getPointsForRouteSync(final long routeId,
      final Continuation<? super List<RoutePointEntity>> $completion) {
    final String _sql = "SELECT * FROM route_points WHERE routeId = ? ORDER BY sequenceOrder ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, routeId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoutePointEntity>>() {
      @Override
      @NonNull
      public List<RoutePointEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitudeMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "altitudeMeters");
          final int _cursorIndexOfSequenceOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "sequenceOrder");
          final List<RoutePointEntity> _result = new ArrayList<RoutePointEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoutePointEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRouteId;
            _tmpRouteId = _cursor.getLong(_cursorIndexOfRouteId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Float _tmpAltitudeMeters;
            if (_cursor.isNull(_cursorIndexOfAltitudeMeters)) {
              _tmpAltitudeMeters = null;
            } else {
              _tmpAltitudeMeters = _cursor.getFloat(_cursorIndexOfAltitudeMeters);
            }
            final int _tmpSequenceOrder;
            _tmpSequenceOrder = _cursor.getInt(_cursorIndexOfSequenceOrder);
            _item = new RoutePointEntity(_tmpId,_tmpRouteId,_tmpLatitude,_tmpLongitude,_tmpAltitudeMeters,_tmpSequenceOrder);
            _result.add(_item);
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
