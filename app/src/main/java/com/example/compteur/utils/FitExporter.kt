package com.example.compteur.utils

import com.example.compteur.data.db.entity.GpsPointEntity
import com.example.compteur.data.db.entity.SensorDataEntity
import com.example.compteur.domain.model.Session
import com.garmin.fit.*
import java.io.File
import java.io.FileOutputStream
import java.util.Date

object FitExporter {

    fun exportSession(
        session: Session,
        gpsPoints: List<GpsPointEntity>,
        sensorData: List<SensorDataEntity>,
        outputFile: File
    ) {
        val encode = FileEncoder(outputFile, Fit.ProtocolVersion.V2_0)

        // 1. File ID Message
        val fileIdMesg = FileIdMesg()
        fileIdMesg.type = com.garmin.fit.File.ACTIVITY
        fileIdMesg.manufacturer = Manufacturer.DEVELOPMENT
        fileIdMesg.product = 1
        fileIdMesg.timeCreated = DateTime(Date(session.startedAt))
        fileIdMesg.serialNumber = session.id
        encode.write(fileIdMesg)

        // 2. Event Message (Start Activity)
        val startEvent = EventMesg()
        startEvent.event = Event.TIMER
        startEvent.eventType = EventType.START
        startEvent.eventGroup = 0
        startEvent.timestamp = DateTime(Date(session.startedAt))
        encode.write(startEvent)

        // Merge GPS and Sensor Data — indexation par timestamp pour éviter un parcours O(n²)
        val gpsByTimestamp = gpsPoints.associateBy { it.timestampMs }
        val sensorByTimestamp = sensorData.associateBy { it.timestampMs }
        val allTimestamps = (gpsByTimestamp.keys + sensorByTimestamp.keys).distinct().sorted()

        var currentDistance = 0f
        var lastGpsPoint: GpsPointEntity? = null

        for (timestamp in allTimestamps) {
            val gps = gpsByTimestamp[timestamp]
            val sensor = sensorByTimestamp[timestamp]
            
            val record = RecordMesg()
            record.timestamp = DateTime(Date(timestamp))
            
            if (gps != null) {
                record.positionLat = (gps.latitude * 2147483648.0 / 180.0).toInt()
                record.positionLong = (gps.longitude * 2147483648.0 / 180.0).toInt()
                record.altitude = gps.altitudeMeters
                record.speed = gps.speedMps
                
                // Track distance since FIT format often needs distance incrementally
                if (lastGpsPoint != null) {
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(
                        lastGpsPoint!!.latitude, lastGpsPoint!!.longitude,
                        gps.latitude, gps.longitude,
                        results
                    )
                    currentDistance += results[0]
                }
                lastGpsPoint = gps
            } else if (lastGpsPoint != null) {
                // If there's no new GPS point but we have a previous one, carry it forward
                // so we don't send missing coordinates (which some platforms interpret as 0,0)
                record.positionLat = (lastGpsPoint.latitude * 2147483648.0 / 180.0).toInt()
                record.positionLong = (lastGpsPoint.longitude * 2147483648.0 / 180.0).toInt()
            }
            
            record.distance = currentDistance

            if (sensor != null) {
                sensor.heartRateBpm?.let { record.heartRate = it.toShort() }
                sensor.cadenceRpm?.let { record.cadence = it.toShort() }
                sensor.powerWatts?.let { record.power = it }
                // Use BLE speed if GPS isn't present
                if (gps == null && sensor.bleSpeedKph != null) {
                    record.speed = (sensor.bleSpeedKph * 1000f / 3600f) // kph to m/s
                }
            }
            
            encode.write(record)
        }

        // 3. Event Message (Stop Activity)
        val stopEvent = EventMesg()
        stopEvent.event = Event.TIMER
        stopEvent.eventType = EventType.STOP_ALL
        stopEvent.eventGroup = 0
        stopEvent.timestamp = DateTime(Date(session.endedAt ?: System.currentTimeMillis()))
        encode.write(stopEvent)

        val endTime = session.endedAt ?: System.currentTimeMillis()
        val elapsedTime = (endTime - session.startedAt) / 1000f

        // Lap unique couvrant toute la session (requis/recommandé par la plupart des plateformes)
        val lapMesg = LapMesg()
        lapMesg.messageIndex = 0
        lapMesg.timestamp = DateTime(Date(endTime))
        lapMesg.startTime = DateTime(Date(session.startedAt))
        lapMesg.totalElapsedTime = elapsedTime
        lapMesg.totalTimerTime = elapsedTime
        lapMesg.totalDistance = session.distanceMeters
        lapMesg.avgSpeed = (session.avgSpeedKph * 1000f / 3600f)
        lapMesg.maxSpeed = (session.maxSpeedKph * 1000f / 3600f)
        lapMesg.totalAscent = session.ascentMeters.toInt()
        session.avgHeartRateBpm?.let { lapMesg.avgHeartRate = it.toShort() }
        session.avgPowerWatts?.let { lapMesg.avgPower = it }
        gpsPoints.firstOrNull()?.let {
            lapMesg.startPositionLat = (it.latitude * 2147483648.0 / 180.0).toInt()
            lapMesg.startPositionLong = (it.longitude * 2147483648.0 / 180.0).toInt()
        }
        encode.write(lapMesg)

        val sessionMesg = SessionMesg()
        sessionMesg.sport = Sport.CYCLING
        sessionMesg.subSport = SubSport.ROAD // default for road cycling
        sessionMesg.messageIndex = 0
        sessionMesg.firstLapIndex = 0
        sessionMesg.numLaps = 1
        sessionMesg.timestamp = DateTime(Date(endTime))
        sessionMesg.startTime = DateTime(Date(session.startedAt))
        sessionMesg.totalElapsedTime = elapsedTime
        sessionMesg.totalTimerTime = elapsedTime
        sessionMesg.totalDistance = session.distanceMeters
        sessionMesg.avgSpeed = (session.avgSpeedKph * 1000f / 3600f)
        sessionMesg.maxSpeed = (session.maxSpeedKph * 1000f / 3600f)
        session.avgHeartRateBpm?.let { sessionMesg.avgHeartRate = it.toShort() }
        session.avgPowerWatts?.let { sessionMesg.avgPower = it }
        sessionMesg.totalAscent = session.ascentMeters.toInt()

        val firstGps = gpsPoints.firstOrNull()
        if (firstGps != null) {
            sessionMesg.startPositionLat = (firstGps.latitude * 2147483648.0 / 180.0).toInt()
            sessionMesg.startPositionLong = (firstGps.longitude * 2147483648.0 / 180.0).toInt()
        }

        encode.write(sessionMesg)

        // Activity Message — clôture du fichier d'activité
        val activityMesg = ActivityMesg()
        activityMesg.timestamp = DateTime(Date(endTime))
        activityMesg.totalTimerTime = elapsedTime
        activityMesg.numSessions = 1
        activityMesg.type = Activity.MANUAL
        activityMesg.event = Event.ACTIVITY
        activityMesg.eventType = EventType.STOP
        encode.write(activityMesg)

        // Finalize
        encode.close()
    }
}
