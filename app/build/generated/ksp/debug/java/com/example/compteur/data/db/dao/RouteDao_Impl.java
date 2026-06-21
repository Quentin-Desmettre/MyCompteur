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
import com.example.compteur.data.db.entity.RouteEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class RouteDao_Impl implements RouteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RouteEntity> __insertionAdapterOfRouteEntity;

  private final EntityDeletionOrUpdateAdapter<RouteEntity> __deletionAdapterOfRouteEntity;

  private final EntityDeletionOrUpdateAdapter<RouteEntity> __updateAdapterOfRouteEntity;

  public RouteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRouteEntity = new EntityInsertionAdapter<RouteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `routes` (`id`,`name`,`totalDistanceMeters`,`totalAscentMeters`,`dateImported`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RouteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getTotalDistanceMeters());
        statement.bindDouble(4, entity.getTotalAscentMeters());
        statement.bindLong(5, entity.getDateImported());
      }
    };
    this.__deletionAdapterOfRouteEntity = new EntityDeletionOrUpdateAdapter<RouteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `routes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RouteEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRouteEntity = new EntityDeletionOrUpdateAdapter<RouteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `routes` SET `id` = ?,`name` = ?,`totalDistanceMeters` = ?,`totalAscentMeters` = ?,`dateImported` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RouteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getTotalDistanceMeters());
        statement.bindDouble(4, entity.getTotalAscentMeters());
        statement.bindLong(5, entity.getDateImported());
        statement.bindLong(6, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final RouteEntity route, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRouteEntity.insertAndReturnId(route);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RouteEntity route, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRouteEntity.handle(route);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RouteEntity route, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRouteEntity.handle(route);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RouteEntity>> getAllRoutes() {
    final String _sql = "SELECT * FROM routes ORDER BY dateImported DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"routes"}, new Callable<List<RouteEntity>>() {
      @Override
      @NonNull
      public List<RouteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTotalDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceMeters");
          final int _cursorIndexOfTotalAscentMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAscentMeters");
          final int _cursorIndexOfDateImported = CursorUtil.getColumnIndexOrThrow(_cursor, "dateImported");
          final List<RouteEntity> _result = new ArrayList<RouteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RouteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final float _tmpTotalDistanceMeters;
            _tmpTotalDistanceMeters = _cursor.getFloat(_cursorIndexOfTotalDistanceMeters);
            final float _tmpTotalAscentMeters;
            _tmpTotalAscentMeters = _cursor.getFloat(_cursorIndexOfTotalAscentMeters);
            final long _tmpDateImported;
            _tmpDateImported = _cursor.getLong(_cursorIndexOfDateImported);
            _item = new RouteEntity(_tmpId,_tmpName,_tmpTotalDistanceMeters,_tmpTotalAscentMeters,_tmpDateImported);
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
  public Object getRouteById(final long routeId,
      final Continuation<? super RouteEntity> $completion) {
    final String _sql = "SELECT * FROM routes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, routeId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RouteEntity>() {
      @Override
      @Nullable
      public RouteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTotalDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistanceMeters");
          final int _cursorIndexOfTotalAscentMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAscentMeters");
          final int _cursorIndexOfDateImported = CursorUtil.getColumnIndexOrThrow(_cursor, "dateImported");
          final RouteEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final float _tmpTotalDistanceMeters;
            _tmpTotalDistanceMeters = _cursor.getFloat(_cursorIndexOfTotalDistanceMeters);
            final float _tmpTotalAscentMeters;
            _tmpTotalAscentMeters = _cursor.getFloat(_cursorIndexOfTotalAscentMeters);
            final long _tmpDateImported;
            _tmpDateImported = _cursor.getLong(_cursorIndexOfDateImported);
            _result = new RouteEntity(_tmpId,_tmpName,_tmpTotalDistanceMeters,_tmpTotalAscentMeters,_tmpDateImported);
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
