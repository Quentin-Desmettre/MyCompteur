package com.example.compteur

import com.example.compteur.utils.RouteMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteMathTest {

    // Parcours simple : segment est-ouest le long de l'équateur (lat 0), de lng 0 à lng 0.02.
    // ~2226 m de long (111320 m/deg * 0.02).
    private val points = listOf(
        0.0 to 0.0,
        0.0 to 0.005,
        0.0 to 0.010,
        0.0 to 0.015,
        0.0 to 0.020
    )

    @Test
    fun totalDistanceMatchesHaversine() {
        val index = RouteMath.buildIndex(points)
        val expected = RouteMath.haversineMeters(0.0, 0.0, 0.0, 0.020)
        assertEquals(expected, index.totalMeters, 1.0)
    }

    @Test
    fun projectionAtStartIsZero() {
        val index = RouteMath.buildIndex(points)
        val p = RouteMath.project(index, 0.0, 0.0)
        assertEquals(0.0, p.distanceAlongMeters, 1.0)
        assertEquals(index.totalMeters, p.distanceRemainingMeters, 1.0)
        assertEquals(0.0, p.fractionDone, 0.001)
    }

    @Test
    fun projectionAtMidpointIsHalf() {
        val index = RouteMath.buildIndex(points)
        // Position légèrement décalée au nord du milieu : doit se projeter à mi-parcours.
        val p = RouteMath.project(index, 0.0001, 0.010)
        assertEquals(index.totalMeters / 2.0, p.distanceAlongMeters, 5.0)
        assertEquals(0.5, p.fractionDone, 0.01)
    }

    @Test
    fun alongPlusRemainingEqualsTotal() {
        val index = RouteMath.buildIndex(points)
        val p = RouteMath.project(index, 0.0, 0.013)
        assertEquals(index.totalMeters, p.distanceAlongMeters + p.distanceRemainingMeters, 1.0)
    }

    @Test
    fun distanceAlongIsMonotonicAlongRoute() {
        val index = RouteMath.buildIndex(points)
        var prev = -1.0
        for (lng in listOf(0.0, 0.004, 0.008, 0.012, 0.016, 0.020)) {
            val along = RouteMath.project(index, 0.0, lng).distanceAlongMeters
            assertTrue("along doit croître (lng=$lng)", along >= prev - 1.0)
            prev = along
        }
    }

    @Test
    fun etaAtConstantSpeed() {
        // 1000 m restants à 5 m/s => 200 s.
        val now = 1_000_000L
        val (secs, eta) = RouteMath.etaSecondsAndEpoch(1000.0, 5.0, now)
        assertEquals(200L, secs)
        assertEquals(now + 200_000L, eta)
    }

    @Test
    fun etaInvalidWhenStopped() {
        val (secs, eta) = RouteMath.etaSecondsAndEpoch(1000.0, 0.0, 1_000_000L)
        assertEquals(-1L, secs)
        assertEquals(-1L, eta)
    }
}
