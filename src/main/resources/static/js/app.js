(function () {
    'use strict';

    var TILES = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    var ATTRIBUTION = '&copy; OpenStreetMap contributors';

    // <form data-confirm="..."> asks before submitting (used by Delete buttons)
    document.addEventListener('submit', function (e) {
        var msg = e.target.getAttribute('data-confirm');
        if (msg && !window.confirm(msg)) e.preventDefault();
    });

    function baseMap(el, center, zoom) {
        var map = L.map(el, { scrollWheelZoom: false }).setView(center, zoom);
        L.tileLayer(TILES, { maxZoom: 19, attribution: ATTRIBUTION }).addTo(map);
        return map;
    }

    function num(v) {
        var n = parseFloat(v);
        return isNaN(n) ? null : n;
    }

    function initPicker(el) {
        var latIn = document.getElementById(el.dataset.latInput);
        var lngIn = document.getElementById(el.dataset.lngInput);
        var status = document.getElementById('location-status');
        var query = document.getElementById('location-query');
        var lat = num(latIn.value), lng = num(lngIn.value);
        var has = lat !== null && lng !== null;
        var map = baseMap(el, has ? [lat, lng] : [39.5, -98.35], has ? 13 : 4);
        var marker = null;

        function say(t) { if (status) status.textContent = t; }
        function write(a, b) { latIn.value = a.toFixed(6); lngIn.value = b.toFixed(6); }
        function place(a, b, recenter) {
            if (marker) {
                marker.setLatLng([a, b]);
            } else {
                marker = L.marker([a, b], { draggable: true }).addTo(map);
                marker.on('dragend', function () {
                    var p = marker.getLatLng();
                    write(p.lat, p.lng);
                });
            }
            if (recenter) map.setView([a, b], Math.max(map.getZoom(), 13));
        }

        if (has) place(lat, lng, false);

        map.on('click', function (e) {
            write(e.latlng.lat, e.latlng.lng);
            place(e.latlng.lat, e.latlng.lng, false);
            say('Location set from the map.');
        });

        [latIn, lngIn].forEach(function (input) {
            input.addEventListener('change', function () {
                var a = num(latIn.value), b = num(lngIn.value);
                if (a !== null && b !== null) place(a, b, true);
            });
        });

        document.getElementById('use-my-location').addEventListener('click', function () {
            say('Finding your location...');
            navigator.geolocation.getCurrentPosition(function (pos) {
                write(pos.coords.latitude, pos.coords.longitude);
                place(pos.coords.latitude, pos.coords.longitude, true);
                say('Using your current location.');
            }, function () {
                say('Could not get your location. Pick a point on the map instead.');
            });
        });

        function search() {
            var q = query.value.trim();
            if (!q) return;
            say('Searching...');
            fetch('https://nominatim.openstreetmap.org/search?format=json&limit=1&q=' + encodeURIComponent(q))
                .then(function (r) { return r.json(); })
                .then(function (results) {
                    if (!results.length) { say('No results. Try a fuller address.'); return; }
                    var a = parseFloat(results[0].lat), b = parseFloat(results[0].lon);
                    write(a, b);
                    place(a, b, true);
                    say('Found: ' + results[0].display_name);
                })
                .catch(function () { say('Search unavailable. Click the map instead.'); });
        }

        document.getElementById('location-search-btn').addEventListener('click', search);
        query.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') { e.preventDefault(); search(); }   // don't submit the whole form
        });
    }

    function initMatchMap(el) {
        var center = [num(el.dataset.lat), num(el.dataset.lng)];
        var radiusKm = num(el.dataset.radiusKm) || 25;
        var map = baseMap(el, center, 11);
        var circle = L.circle(center, { radius: radiusKm * 1000, color: '#c8102e', weight: 1, fillOpacity: 0.04 }).addTo(map);
        L.circleMarker(center, { radius: 9, color: '#fff', weight: 2, fillColor: '#c8102e', fillOpacity: 1 })
            .addTo(map).bindPopup(el.dataset.label || '');

        var bounds = circle.getBounds();
        document.querySelectorAll('[data-match-lat]').forEach(function (row) {
            var p = [num(row.dataset.matchLat), num(row.dataset.matchLng)];
            L.circleMarker(p, { radius: 7, color: '#fff', weight: 2, fillColor: '#1d4ed8', fillOpacity: 1 })
                .addTo(map).bindPopup(row.dataset.matchLabel || '');
            bounds.extend(p);
        });
        map.fitBounds(bounds, { padding: [16, 16] });
    }

    document.addEventListener('DOMContentLoaded', function () {
        if (typeof L === 'undefined') return;   // Leaflet failed to load; the forms still work
        document.querySelectorAll('[data-location-picker]').forEach(initPicker);
        document.querySelectorAll('[data-match-map]').forEach(initMatchMap);
    });
})();