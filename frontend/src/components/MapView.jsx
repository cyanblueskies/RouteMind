import React from "react";
import { MapContainer, TileLayer, Marker, Polyline, useMap, CircleMarker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";

function MapController({ center }) {
  const map = useMap();
  React.useEffect(() => {
    if (center) map.flyTo(center, 15);
  }, [center, map]);
  return null;
}

const HAZARD_STYLES = {
  Physical:       { color: "#dc2626", fillColor: "#dc2626" },
  Environmental:  { color: "#f59e0b", fillColor: "#f59e0b" },
  Infrastructure: { color: "#7c3aed", fillColor: "#7c3aed" },
};

export default function MapView({
  startPos, destination, mapTarget, routePoints, routeOptions,
  selectedRouteIdx, hazardMarkers, highContrast, onUpvoteHazard, isLoggedIn
}) {
  const center = [52.4862, -1.8904];

  return (
    <MapContainer center={startPos || center} zoom={13} className="map-container">
      <TileLayer
        key={highContrast ? "hc" : "normal"}
        attribution="&copy; OpenStreetMap contributors"
        url={highContrast
          ? "https://tiles.stadiamaps.com/tiles/stamen_toner/{z}/{x}/{y}{r}.png"
          : "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        }
      />
      <MapController center={mapTarget} />

      {startPos && <Marker position={startPos} />}
      {destination && <Marker position={destination} />}

      {/* Unselected routes in grey */}
      {routeOptions.map((route, idx) => {
        if (!route.points || idx === selectedRouteIdx) return null;
        const pts = route.points.map(p => [p.lat, p.lon]);
        return (
          <React.Fragment key={`route-${idx}`}>
            <Polyline positions={pts} color="#666" weight={8} opacity={0.5} />
            <Polyline positions={pts} color="#aaa" weight={5} opacity={0.6} lineCap="round" />
          </React.Fragment>
        );
      })}

      {/* Selected route in yellow */}
      {routePoints.length > 0 && (
        <>
          <Polyline positions={routePoints} color="#8C6B00" weight={10} />
          <Polyline positions={routePoints} color="#FFC107" weight={6} lineCap="round" />
        </>
      )}

      {/* Hazard markers */}
      {hazardMarkers.map((hazard, idx) => {
        const style = HAZARD_STYLES[hazard.hazardType] || HAZARD_STYLES.Physical;
        return (
          <CircleMarker
            key={`hazard-${idx}`}
            center={[hazard.latitude, hazard.longitude]}
            radius={14}
            pathOptions={{ ...style, fillOpacity: 0.8, weight: 2 }}
          >
            <Popup>
              <div className="hazard-popup">
                <strong>{hazard.hazardType || "Hazard"}</strong>
                <p>{hazard.description || "No description"}</p>
                <span className="hazard-meta">
                  Reported by {hazard.authorUserName || "Unknown"}<br />
                  Confirmed by {hazard.upvotes || 0} users
                </span>
                {isLoggedIn && (
                  <button className="btn btn-sm btn-hazard-confirm" onClick={() => onUpvoteHazard(hazard.id)}>
                    Confirm Hazard
                  </button>
                )}
              </div>
            </Popup>
          </CircleMarker>
        );
      })}
    </MapContainer>
  );
}
