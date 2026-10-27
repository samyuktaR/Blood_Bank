package com.bloodbank.matching;

public final class GeoUtils {

    public static final double EARTH_RADIUS_KM = 6371.0088;
    public static final double KM_PER_MILE = 1.609344;

    private GeoUtils() { }

    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2) * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public static double kmToMiles(double km) {
        return km / KM_PER_MILE;
    }

    public static BoundingBox boundingBox(double lat, double lon, double radiusKm) {
        double angular = radiusKm / EARTH_RADIUS_KM;
        double latDelta = Math.toDegrees(angular);
        double minLat = lat - latDelta;
        double maxLat = lat + latDelta;
        if (minLat <= -90 || maxLat >= 90) {
            return new BoundingBox(Math.max(minLat, -90), Math.min(maxLat, 90), -180, 180);
        }
        double ratio = Math.min(1.0, Math.sin(angular) / Math.cos(Math.toRadians(lat)));
        double lonDelta = Math.toDegrees(Math.asin(ratio));
        double minLon = lon - lonDelta;
        double maxLon = lon + lonDelta;
        if (minLon < -180 || maxLon > 180) {
            return new BoundingBox(minLat, maxLat, -180, 180);
        }
        return new BoundingBox(minLat, maxLat, minLon, maxLon);
    }

    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record BoundingBox(double minLat, double maxLat, double minLon, double maxLon) {
        public boolean contains(double lat, double lon) {
            return lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon;
        }
    }
}