package com.example.compteur.data.gpx

import android.util.Xml
import com.example.compteur.data.db.entity.RoutePointEntity
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream

data class GpxMetadata(val name: String, val points: List<RoutePointEntity>)

class GpxParser {

    fun parse(inputStream: InputStream, routeId: Long): GpxMetadata {
        val handler = GpxHandler(routeId)
        Xml.parse(inputStream, Xml.Encoding.UTF_8, handler)
        return GpxMetadata(handler.routeName, handler.points)
    }

    private class GpxHandler(private val routeId: Long) : DefaultHandler() {
        var routeName: String = "Imported Route"
        val points = mutableListOf<RoutePointEntity>()
        
        private var currentElement: String? = null
        private var currentLat: Double? = null
        private var currentLon: Double? = null
        private var currentEle: Float? = null
        private var sequenceOrder = 0

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            currentElement = localName ?: qName
            if (currentElement == "trkpt") {
                currentLat = attributes?.getValue("lat")?.toDoubleOrNull()
                currentLon = attributes?.getValue("lon")?.toDoubleOrNull()
            }
        }

        override fun characters(ch: CharArray?, start: Int, length: Int) {
            val content = String(ch!!, start, length).trim()
            if (content.isEmpty()) return

            when (currentElement) {
                "name" -> if (routeName == "Imported Route") routeName = content
                "ele" -> currentEle = content.toFloatOrNull()
            }
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            if ((localName ?: qName) == "trkpt") {
                val lat = currentLat
                val lon = currentLon
                if (lat != null && lon != null) {
                    points.add(
                        RoutePointEntity(
                            routeId = routeId,
                            latitude = lat,
                            longitude = lon,
                            altitudeMeters = currentEle,
                            sequenceOrder = sequenceOrder++
                        )
                    )
                }
                // Reset for next point
                currentEle = null
                currentLat = null
                currentLon = null
            }
            currentElement = null
        }
    }
}
