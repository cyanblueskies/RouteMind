import React from "react";
import Preferences from "./Preferences";
import RouteCard from "./RouteCard";

export default function SidePanel({
  open, onClose, noise, setNoise, pollution, setPollution, lighting, setLighting,
  toggled, setToggled, onPreferenceChange, routeOptions, selectedRouteIdx,
  onSelectRoute, onSaveRoute, onShareRoute, isLoggedIn, savedRoutes,
  savedRoutesOpen, setSavedRoutesOpen, onFetchSavedRoutes, onLoadSavedRoute,
  onGenerateRoutes, isLoading, darkMode, onHazardOpen
}) {
  return (
    <div className={`side-panel ${open ? "side-panel-open" : ""}`}>
      <button className="side-panel-close" onClick={onClose}>
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2">
          <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
        </svg>
      </button>

      <div className="side-panel-content">
        <Preferences
          noise={noise} setNoise={setNoise}
          pollution={pollution} setPollution={setPollution}
          lighting={lighting} setLighting={setLighting}
          toggled={toggled} setToggled={setToggled}
          onPreferenceChange={onPreferenceChange}
        />

        <div className="panel-section">
          <button
            className="btn btn-generate"
            onClick={onGenerateRoutes}
            disabled={isLoading}
          >
            {isLoading ? "Generating..." : "Generate Routes"}
          </button>
        </div>

        {routeOptions.length > 0 && (
          <div className="panel-section">
            <h3 className="panel-title">Route Options</h3>
            {routeOptions.map((route, idx) => (
              <RouteCard
                key={idx} route={route} idx={idx}
                isSelected={selectedRouteIdx === idx}
                onSelect={onSelectRoute}
                onSave={onSaveRoute}
                onShare={onShareRoute}
                darkMode={darkMode}
              />
            ))}
          </div>
        )}

        {isLoggedIn && (
          <div className="panel-section">
            <div
              className="saved-routes-header"
              onClick={() => { setSavedRoutesOpen(!savedRoutesOpen); if (!savedRoutesOpen) onFetchSavedRoutes(); }}
            >
              <h3 className="panel-title" style={{ margin: 0 }}>Saved Routes</h3>
              <span className="chevron">{savedRoutesOpen ? "▲" : "▼"}</span>
            </div>
            {savedRoutesOpen && (
              <div className="saved-routes-list">
                {savedRoutes.length === 0 && <p className="text-muted">No saved routes yet.</p>}
                {savedRoutes.map((sr, idx) => (
                  <div key={idx} className="saved-route-item">
                    <span className="saved-route-name">{sr.routeName || `Route ${idx + 1}`}</span>
                    <button className="btn btn-sm btn-primary" onClick={() => onLoadSavedRoute(sr)}>Load</button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        <div className="panel-section">
          <button className="btn btn-hazard" onClick={onHazardOpen}>
            Report a Hazard
          </button>
        </div>
      </div>
    </div>
  );
}
