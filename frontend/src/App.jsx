// Import components from React Leaflet
// MapContainer = main interactive map
// TileLayer = background map tiles
// Marker = point markers (start/destination)
// Polyline = line used to draw a route
import { MapContainer, TileLayer, Marker, Polyline, useMap, CircleMarker, Tooltip, Popup } from "react-leaflet";
import React, { useState, useEffect, useRef } from "react";
// Import Leaflet CSS so the map renders correctly
// Without this the map tiles will not display properly
import "leaflet/dist/leaflet.css";
import "./preferences.css";
import hazardimage from "./assets/hazardgif.gif";
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Select, FormControl, InputLabel } from "@mui/material";
// InfiniteScroll can be re-added when we have multiple route options

function MapController({ center }) {
  const map = useMap();
  useEffect(() => {
    if (center) {
      map.flyTo(center, 15);
    }
  }, [center, map]);
  return null;
}

// score bar component for visual display
const ScoreBar = ({ label, score }) => {
  const color = score >= 70 ? "#4caf50" : score >= 40 ? "#ff9800" : "#f44336";
  return (
    <div style={{ marginBottom: "6px" }}>
      <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px", marginBottom: "2px" }}>
        <span>{label}</span>
        <span style={{ fontWeight: "bold" }}>{score.toFixed(0)}</span>
      </div>
      <div style={{ background: "#e0e0e0", borderRadius: "4px", height: "8px" }}>
        <div style={{ width: `${score}%`, background: color, borderRadius: "4px", height: "8px", transition: "width 0.3s" }} />
      </div>
    </div>
  );
};

// US-14: Generate a short explanation of why a route is recommended
const getRouteExplanation = (route) => {
  const strengths = [];
  const weaknesses = [];
  if (route.noiseScore >= 75) strengths.push("quiet streets");
  else if (route.noiseScore < 40) weaknesses.push("noisy roads");
  if (route.pollutionScore >= 75) strengths.push("clean air");
  else if (route.pollutionScore < 40) weaknesses.push("high pollution");
  if (route.lightingScore >= 75) strengths.push("well-lit paths");
  else if (route.lightingScore < 40) weaknesses.push("poor lighting");
  if (route.wheelchairScore >= 75) strengths.push("smooth surfaces");
  else if (route.wheelchairScore < 40) weaknesses.push("rough terrain");

  let text = "";
  if (strengths.length > 0) text += "This route offers " + strengths.join(", ") + ". ";
  if (weaknesses.length > 0) text += "Note: " + weaknesses.join(", ") + ".";
  if (!text) text = "This route has moderate scores across all categories.";
  return text;
};

const RouteList = ({ routeOptions, onSelectRoute, onSaveRoute, onShareRoute, selectedIdx, theme }) => {
  return (
    <div style={{ padding: "16px", borderBottom: "1px solid #e0e0e0", background: theme?.panel || "white", color: theme?.text || "black" }}>
        <h3 style={{ margin: "0 0 10px 0", fontSize: "15px", textTransform: "uppercase", letterSpacing: "0.5px", color: theme?.subtext || "#888" }}>Route Options</h3>
        {routeOptions.length === 0 && <p style={{ color: "#aaa", fontSize: "13px" }}>Generate a route to see options here.</p>}
        {routeOptions.map((route, idx) => (
          <div key={idx} style={{
            marginBottom: "12px",
            padding: "12px",
            border: selectedIdx === idx ? "2px solid #4caf50" : "1px solid #ddd",
            borderRadius: "8px",
            background: theme?.panel === "#2d2d2d" ? "#3a3a3a" : "#fafafa",
            cursor: "pointer",
            transition: "all 0.2s ease",
            boxShadow: selectedIdx === idx ? "0 2px 8px rgba(76,175,80,0.3)" : "none"
          }}
          onClick={() => onSelectRoute(route, idx)}
          >
            {/* route name + distance/duration */}
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "6px", fontSize: "12px", color: theme?.subtext || "#888" }}>
              <span style={{ fontWeight: "bold", fontSize: "13px", color: theme?.text || "#333" }}>Route {idx + 1}</span>
              <span>{route.distanceKm?.toFixed(1) || "?"} km &middot; {route.durationMin?.toFixed(0) || "?"} min</span>
            </div>

            {/* overall score header */}
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" }}>
              <div>
                <span style={{ fontSize: "13px", color: theme?.subtext || "#666" }}>Overall Score</span>
                <div style={{ fontSize: "28px", fontWeight: "bold", color: route.totalScore >= 70 ? "#4caf50" : route.totalScore >= 40 ? "#ff9800" : "#f44336" }}>
                  {route.totalScore?.toFixed(1) || "0.0"}
                </div>
              </div>
              <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
                <button
                  onClick={(e) => { e.stopPropagation(); onSelectRoute(route, idx); }}
                  style={{
                    padding: "8px 16px",
                    background: selectedIdx === idx ? "#388e3c" : "rgb(6, 171, 74)",
                    color: "white",
                    border: "none",
                    borderRadius: "6px",
                    cursor: "pointer",
                    fontWeight: "bold",
                    fontSize: "13px"
                  }}
                >
                  {selectedIdx === idx ? "Selected" : "Select"}
                </button>
                {onSaveRoute && (
                  <button
                    onClick={(e) => { e.stopPropagation(); onSaveRoute(route); }}
                    style={{
                      padding: "6px 16px",
                      background: "transparent",
                      color: theme?.text || "#666",
                      border: `1px solid ${theme?.border || "#ccc"}`,
                      borderRadius: "6px",
                      cursor: "pointer",
                      fontSize: "12px"
                    }}
                  >
                    Save Route
                  </button>
                )}
                {onShareRoute && (
                  <button
                    onClick={(e) => { e.stopPropagation(); onShareRoute(route); }}
                    style={{
                      padding: "6px 16px",
                      background: "transparent",
                      color: theme?.text || "#666",
                      border: `1px solid ${theme?.border || "#ccc"}`,
                      borderRadius: "6px",
                      cursor: "pointer",
                      fontSize: "12px"
                    }}
                  >
                    Share
                  </button>
                )}
              </div>
            </div>

            {/* US-14: route recommendation explanation */}
            <p style={{ fontSize: "12px", color: theme?.subtext || "#777", margin: "0 0 8px 0", fontStyle: "italic", lineHeight: "1.4" }}>
              {getRouteExplanation(route)}
            </p>

            {/* score breakdown bars */}
            <ScoreBar label="Noise" score={route.noiseScore || 0} />
            <ScoreBar label="Pollution" score={route.pollutionScore || 0} />
            <ScoreBar label="Lighting" score={route.lightingScore || 0} />
            <ScoreBar label="Wheelchair" score={route.wheelchairScore || 0} />
          </div>
        ))}
    </div>
    );
  };

function App() {

  const [darkMode, setDarkMode] = useState(false);

  const theme = {
    bg: darkMode ? "#1a1a1a" : "#eee",
    panel: darkMode ? "#2d2d2d" : "white",
    text: darkMode ? "white" : "black",
    border: darkMode ? "#444" : "#ccc",
    subtext: darkMode ? "#aaa" : "#555",
    inputBg: darkMode ? "#3a3a3a" : "white",
    dropdownBg: darkMode ? "#2d2d2d" : "white",
    hoverBg: darkMode ? "#4a4a4a" : "#f0f0f0",
    hoverLeave: darkMode ? "#2d2d2d" : "white",
  };

  // centering map when it loads
  const center = [52.4862, -1.8904];

  // route data from backend
  const [routePoints, setRoutePoints] = useState([]);
  const [routeOptions, setRouteOptions] = useState([]);
  const [selectedRouteIdx, setSelectedRouteIdx] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  // high contrast mode for accessibility (F-3)
  const [highContrast, setHighContrast] = useState(false);


  // Login
  const [isLoginOpen, setisLoginOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(() => {
      return localStorage.getItem("isLoggedIn") === "true";
  });
  const [userName, setUserName] = useState(() => {
    return localStorage.getItem("userName") || "";
  });


  const handleLogin = async (e) => {
    e.preventDefault();
    const username = new FormData(e.target).get("username").toString();

    // signup|login
    const endpoint = e.nativeEvent.submitter.getAttribute("formaction") === "signup" ? "new" : "login";

    try {
      const response = await fetch(`/api/users/${endpoint}`, {
        method: "POST",
        body: username,
      });

      if (!response.ok) {
        alert(endpoint === "login" ? "User not found. Try signing up first." : "Signup failed. Username may already exist.");
        return;
      }

      let userid = await response.json();
      localStorage.setItem("user_id", userid);
      localStorage.setItem("userName", username);
      localStorage.setItem("isLoggedIn", "true");
      setUserName(username);
      setIsLoggedIn(true);
      setisLoginOpen(false);
    } catch (err) {
      alert("Connection failed. Is the backend running?");
    }
  }

  const handleLogout = () => {
    setIsLoggedIn(false);

    localStorage.removeItem("userName");
    localStorage.removeItem("isLoggedIn");
  };
  const [noise, setNoise] = useState(() => {
    const saved = localStorage.getItem("pref_noise");
    return saved !== null ? Number(saved) : 5;
  });
  const [pollution, setPollution] = useState(() => {
    const saved = localStorage.getItem("pref_pollution");
    return saved !== null ? Number(saved) : 5;
  });
  const [lighting, setLighting] = useState(() => {
    const saved = localStorage.getItem("pref_lighting");
    return saved !== null ? Number(saved) : 5;
  });

  // Add this line to define 'toggled' and 'setToggled'
  const [toggled, setToggled] = useState(() => {
    return localStorage.getItem("pref_wheelchair") === "true";
  });

  // Persist preferences to localStorage
  useEffect(() => {
    localStorage.setItem("pref_noise", noise);
    localStorage.setItem("pref_pollution", pollution);
    localStorage.setItem("pref_lighting", lighting);
    localStorage.setItem("pref_wheelchair", toggled);
  }, [noise, pollution, lighting, toggled]);

  // Flag to trigger route regeneration after preference change
  const [regenFlag, setRegenFlag] = useState(false);
  const regenerateIfNeeded = () => {
    setRegenFlag(true);
  };
const [hazardOpen, setHazardOpen] = useState(false);
const [hazardType, setHazardType] = useState("");
const [description, setDescription] = useState("");
  // User's current GPS position
  const [startPos, setStartPos] = useState(null);

  // Destination chosen from search
  const [destination, setDestination] = useState(null);
  const [destName, setDestName] = useState("");

  const [mapTarget, setMapTarget] = useState(null);

  // Hazard markers from API
  const [hazardMarkers, setHazardMarkers] = useState([]);

  // Saved routes (F-7 view)
  const [savedRoutes, setSavedRoutes] = useState([]);
  const [savedRoutesOpen, setSavedRoutesOpen] = useState(false);

  // Fetch nearby hazards for a given lat/lon
  const fetchNearbyHazards = async (lat, lon) => {
    try {
      const res = await fetch(`/api/hazards/nearby?lat=${lat}&long=${lon}&distance=50`);
      if (res.ok) {
        const data = await res.json();
        setHazardMarkers(data);
      }
    } catch (err) {
      console.error("Failed to fetch hazards:", err);
    }
  };

  // Fetch saved routes for logged-in user
  const fetchSavedRoutes = async () => {
    try {
      const res = await fetch("/api/saved-routes/", { credentials: "include" });
      if (res.ok) {
        const data = await res.json();
        setSavedRoutes(data);
      }
    } catch (err) {
      console.error("Failed to fetch saved routes:", err);
    }
  };

  // Search bar state
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);

  // F-7: Load shared route from URL parameters
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    if (params.has("dlat") && params.has("dlon")) {
      const dlat = parseFloat(params.get("dlat"));
      const dlon = parseFloat(params.get("dlon"));
      setDestination([dlat, dlon]);
      setDestName("Shared destination");
      setSearchQuery("Shared destination");
      if (params.has("slat") && params.has("slon")) {
        setStartPos([parseFloat(params.get("slat")), parseFloat(params.get("slon"))]);
      }
      if (params.has("noise")) setNoise(Number(params.get("noise")));
      if (params.has("pollution")) setPollution(Number(params.get("pollution")));
      if (params.has("lighting")) setLighting(Number(params.get("lighting")));
      if (params.has("wheelchair")) setToggled(params.get("wheelchair") === "1" || params.get("wheelchair") === "true");
      setPendingRouteLoad(true);
      window.history.replaceState({}, "", window.location.pathname);
    }
  }, []);

  // get users current location on page load, fallback to Birmingham centre
  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setStartPos([pos.coords.latitude, pos.coords.longitude]);
        },
        (err) => {
          console.warn("Location access denied, using default.", err);
          setStartPos([52.4862, -1.8904]);
        }
      );
    } else {
      setStartPos([52.4862, -1.8904]);
    }
  }, []);

  // Flag to trigger route generation after loading a saved route
  const [pendingRouteLoad, setPendingRouteLoad] = useState(false);
  useEffect(() => {
    if (pendingRouteLoad && startPos && destination) {
      setPendingRouteLoad(false);
      handleGenerateRoutes();
    }
  }, [pendingRouteLoad, startPos, destination]);

  // Fetch hazards when startPos becomes available
  useEffect(() => {
    if (startPos) {
      fetchNearbyHazards(startPos[0], startPos[1]);
    }
  }, [startPos]);

  // debounce timer for search
  const searchTimer = useRef(null);

  // search nominatim when user types (debounced 500ms)
  const handleSearch = (query) => {
    setSearchQuery(query);
    if (query.length < 3) {
      setSearchResults([]);
      return;
    }
    if (searchTimer.current) clearTimeout(searchTimer.current);
    searchTimer.current = setTimeout(async () => {
      setIsSearching(true);
      try {
        const res = await fetch(
          `/nominatim/search?format=json&q=${encodeURIComponent(query)}&limit=5&addressdetails=1&viewbox=-2.05,52.55,-1.73,52.38&bounded=1`,
          { headers: { "Accept-Language": "en" } }
        );
        const data = await res.json();
        setSearchResults(data);
      } catch (e) {
        console.error("Search failed", e);
      }
      setIsSearching(false);
    }, 500);
  };

  // when user clicks a result
  const selectDestination = (result) => {
    const coords = [parseFloat(result.lat), parseFloat(result.lon)];
    setDestination(coords);
    setDestName(result.display_name);
    setSearchQuery(result.display_name);
    setSearchResults([]); // close dropdown
    setMapTarget(coords);
  };

  const handleGenerateRoutes = async () => {
    if (!startPos) {
      alert("Please allow the website to get your current location.");
      return;
    }

    if (!destination) {
      alert("Please enter a destination before generating routes.");
      return;
    }

    setIsLoading(true);
    try {
      const res = await fetch("/api/scored-route", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          start: { lat: startPos[0], lon: startPos[1] },
          dest: { lat: destination[0], lon: destination[1] },
          noiseWeight: parseInt(noise),
          pollutionWeight: parseInt(pollution),
          lightingWeight: parseInt(lighting),
          wheelchairWeight: toggled ? 10 : 0
        })
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Failed to generate route");
      }

      const data = await res.json();
      // backend returns List<Route> (array of routes)
      const routes = Array.isArray(data) ? data : [data];
      setRouteOptions(routes);
      setSelectedRouteIdx(0);

      // draw first route on map
      if (routes.length > 0 && routes[0].points) {
        const pts = routes[0].points.map(p => [p.lat, p.lon]);
        setRoutePoints(pts);
        if (pts.length > 0) {
          setMapTarget(pts[Math.floor(pts.length / 2)]);
        }
      }

      // Fetch hazards near the destination after generating routes
      if (destination) {
        fetchNearbyHazards(destination[0], destination[1]);
      }
    } catch (err) {
      console.error("Route generation failed:", err);
      alert("Failed to generate route: " + err.message);
    }
    setIsLoading(false);
  };

  // Auto-regenerate when preferences change
  useEffect(() => {
    if (regenFlag && routeOptions.length > 0 && startPos && destination && !isLoading) {
      setRegenFlag(false);
      handleGenerateRoutes();
    }
  }, [regenFlag, routeOptions, startPos, destination, isLoading]);

  const reportHazard = async (data) => {
    const response = await fetch("/api/hazards/new", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      throw new Error(`Failed to report hazard, status: ${response.status}`);
    }

    const res = await response.json()
    console.log(res)
  }

  // US-11: Upvote a hazard
  const handleUpvoteHazard = async (hazardId) => {
    try {
      const res = await fetch(`/api/hazards/upvote/${hazardId}`, {
        method: "PATCH",
        credentials: "include"
      });
      if (res.ok) {
        if (startPos) fetchNearbyHazards(startPos[0], startPos[1]);
        if (destination) fetchNearbyHazards(destination[0], destination[1]);
      } else {
        const text = await res.text();
        if (text.includes("own report")) {
          alert("You cannot confirm your own hazard report.");
        } else {
          alert(text || "Failed to confirm hazard.");
        }
      }
    } catch (err) {
      console.error("Upvote failed:", err);
    }
  };

  // F-7: Share route via URL
  const handleShareRoute = (route) => {
    if (!startPos || !destination) return;
    const params = new URLSearchParams({
      slat: startPos[0], slon: startPos[1],
      dlat: destination[0], dlon: destination[1],
      noise, pollution, lighting,
      wheelchair: toggled ? 1 : 0
    });
    const url = `${window.location.origin}?${params.toString()}`;
    navigator.clipboard.writeText(url).then(() => {
      alert("Route link copied to clipboard!");
    }).catch(() => {
      prompt("Copy this link:", url);
    });
  };

  // save route to backend (F-7)
  const handleSaveRoute = async (route) => {
    if (!isLoggedIn) {
      alert("Please log in to save routes.");
      return;
    }
    try {
      const res = await fetch("/api/saved-routes/", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          routeName: `Route to ${destName.split(",")[0]}`,
          start: { lat: startPos[0], lon: startPos[1] },
          dest: { lat: destination[0], lon: destination[1] },
          noiseWeight: parseInt(noise),
          pollutionWeight: parseInt(pollution),
          lightingWeight: parseInt(lighting),
          wheelchairWeight: toggled ? 10 : 0
        })
      });
      if (res.ok) {
        alert("Route saved!");
      } else {
        alert("Failed to save route.");
      }
    } catch (err) {
      console.error("Save route failed:", err);
    }
  };

  return (
    <div className={darkMode ? "dark" : ""}>

      {/* header bar */}
      <div
        style={{
          height: "56px",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          paddingLeft: "20px",
          paddingRight: "20px",
          background: "linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)",
          color: "white",
          fontSize: "18px",
          fontWeight: "bold",
          boxShadow: "0 2px 8px rgba(0,0,0,0.3)"
        }}
      >
        <h3 style={{ margin: 0, cursor: "pointer", letterSpacing: "0.5px" }} onClick={() => window.location.reload()}>TranquilPath</h3>

        {isLoggedIn && (
          <p className="hello">Hello, {userName}</p>
        )}

        <button onClick={() => setHighContrast(!highContrast)} className="login">
          {highContrast ? "Normal Map" : "High Contrast"}
        </button>
        <button onClick={() => setDarkMode(!darkMode)} className="login">
          {darkMode ? "Light Mode" : "Dark Mode"}
        </button>

        {!isLoggedIn ? (
            <>
              <button onClick={() => setisLoginOpen(true)} className="login">Login</button>
            </>
        ) : (
          <button onClick={handleLogout} className="login">Logout</button>
        )}
      </div>

      {/* main container using flex layout */}
      {/* this creates the structure: Map | Control Panel */}
      <div style={{ display: "flex", height: "calc(100vh - 56px)" }}>

        {/* map container in  centre */}
        <div style={{ flex: 1 }}>

          {/* leaflet map component */}
          <MapContainer
            center={startPos || center}
            zoom={13}
            style={{ height: "100%", width: "100%" }}
          >

            {/* Map tile layer - switches between normal and high-contrast (F-3) */}
            <TileLayer
              key={highContrast ? "hc" : "normal"}
              attribution="&copy; OpenStreetMap contributors"
              url={highContrast
                ? "https://tiles.stadiamaps.com/tiles/stamen_toner/{z}/{x}/{y}{r}.png"
                : "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              }
            />
            <MapController center={mapTarget} />


            {/* marker representing start location */}
            {startPos && <Marker position={startPos} />}

            {/* marker representing  destination */}
            {destination && <Marker position={destination} />}

            {/* draw unselected routes in grey (behind selected) */}
            {routeOptions.map((route, idx) => {
              if (!route.points || idx === selectedRouteIdx) return null;
              const pts = route.points.map(p => [p.lat, p.lon]);
              return (
                <React.Fragment key={`route-${idx}`}>
                  <Polyline positions={pts} color="#666" weight={8} opacity={0.6} />
                  <Polyline positions={pts} color="#aaa" weight={5} opacity={0.7} lineCap="round" />
                </React.Fragment>
              );
            })}
            {/* draw selected route on top in yellow */}
            {routePoints.length > 0 && (
              <>
                <Polyline positions={routePoints} color="#8C6B00" weight={10} />
                <Polyline positions={routePoints} color="#FFC107" weight={6} lineCap="round" />
              </>
            )}

            {/* Hazard markers — different color per type (US-11) */}
            {hazardMarkers.map((hazard, idx) => {
              const typeStyle = {
                Physical:       { color: "#e53935", fillColor: "#e53935", radius: 14 },
                Environmental:  { color: "#ff9800", fillColor: "#ff9800", radius: 14 },
                Infrastructure: { color: "#7b1fa2", fillColor: "#7b1fa2", radius: 14 },
              };
              const style = typeStyle[hazard.hazardType] || { color: "#e53935", fillColor: "#e53935", radius: 14 };
              const typeLabel = {
                Physical: "Physical Obstruction",
                Environmental: "Environmental",
                Infrastructure: "Infrastructure Failure",
              };
              return (
                <CircleMarker
                  key={`hazard-${idx}`}
                  center={[hazard.latitude, hazard.longitude]}
                  radius={style.radius}
                  pathOptions={{ color: style.color, fillColor: style.fillColor, fillOpacity: 0.75, weight: 2 }}
                >
                  <Popup>
                    <div style={{ minWidth: "160px" }}>
                      <div style={{ display: "flex", alignItems: "center", gap: "6px", marginBottom: "4px" }}>
                        <span style={{ display: "inline-block", width: "10px", height: "10px", borderRadius: "50%", background: style.fillColor }} />
                        <strong style={{ fontSize: "13px" }}>{typeLabel[hazard.hazardType] || "Hazard"}</strong>
                      </div>
                      <div style={{ fontSize: "12px", color: "#333" }}>{hazard.description}</div>
                      {hazard.authorUserName && (
                        <div style={{ fontSize: "11px", color: "#888", marginTop: "4px" }}>
                          Reported by {hazard.authorUserName}
                        </div>
                      )}
                      <div style={{ fontSize: "11px", color: "#666", marginTop: "2px" }}>
                        Confirmed by {hazard.upvotes || 0} user{hazard.upvotes !== 1 ? "s" : ""}
                      </div>
                      {isLoggedIn && (
                        <button
                          onClick={() => handleUpvoteHazard(hazard.id)}
                          style={{ marginTop: "6px", padding: "4px 10px", fontSize: "12px", background: style.color, color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}
                        >
                          Confirm Hazard
                        </button>
                      )}
                    </div>
                  </Popup>
                </CircleMarker>
              );
            })}

          </MapContainer>
        </div>

        {/* right side control panel - scrollable */}
        <div style={{ width: "400px", display: "flex", flexDirection: "column", background: theme.bg, overflowY: "auto", borderLeft: `1px solid ${theme.border}`, transition: "background 0.3s", ...(highContrast ? { fontSize: "16px" } : {}) }}>

          {/* destination section */}
          <div style={{ padding: "16px", borderBottom: `1px solid ${theme.border}`, background: theme.panel, color: theme.text, transition: "all 0.3s" }}>
            <h3 style={{ margin: "0 0 10px 0", fontSize: "15px", textTransform: "uppercase", letterSpacing: "0.5px", color: theme.subtext }}>Destination</h3>
            <div style={{ position: "relative" }}>
              <input
                type="text"
                placeholder="Search for a destination..."
                value={searchQuery}
                onChange={(e) => handleSearch(e.target.value)}
                style={{
                  width: "100%",
                  padding: "10px 12px",
                  fontSize: "14px",
                  border: `2px solid ${theme.border}`,
                  borderRadius: "8px",
                  boxSizing: "border-box",
                  background: theme.inputBg,
                  color: theme.text,
                  outline: "none",
                  transition: "border-color 0.2s"
                }}
                onFocus={(e) => e.target.style.borderColor = "#4caf50"}
                onBlur={(e) => e.target.style.borderColor = theme.border}
              />
              {isSearching && <p style={{ fontSize: "12px", color: "#888" }}>Searching...</p>}
              {searchResults.length > 0 && (
                <ul style={{
                  position: "absolute",
                  top: "100%",
                  left: 0,
                  right: 0,
                  background: theme.dropdownBg,
                  border: `1px solid ${theme.border}`,
                  borderRadius: "4px",
                  zIndex: 1000,
                  maxHeight: "200px",
                  overflowY: "auto",
                  listStyle: "none",
                  margin: 0,
                  padding: 0
                }}>
                  {searchResults.map((result) => (
                    <li
                      key={result.place_id}
                      onClick={() => selectDestination(result)}
                      style={{
                        padding: "8px",
                        cursor: "pointer",
                        fontSize: "13px",
                        borderBottom: "1px solid #eee"
                      }}
                      onMouseEnter={(e) => e.target.style.background = theme.hoverBg}
                      onMouseLeave={(e) => e.target.style.background = theme.hoverLeave}
                    >
                      {result.display_name}
                    </li>
                  ))}
                </ul>
              )}
            </div>
            {destName && (
              <p style={{ fontSize: "12px", color: theme.subtext, marginTop: "6px" }}>
                📍 {destName.split(",").slice(0, 3).join(",")}
              </p>
            )}
            <div>
            {/*generate routes button*/}
              <Button
                variant="text"
                size="small"
                onClick={handleGenerateRoutes}
                disabled={isLoading}
                sx={{
                marginTop: "15px",
                width: "100%",
                backgroundColor: isLoading ? "#999" : "rgb(6, 171, 74)",
                color: "white",
                fontWeight: "bold",
                textTransform: "none", // Stops Material UI from making the text ALL CAPS
                padding: "10px",
                transition: "transform 0.2s ease-in-out",
                "&:hover": { //hover settings
                  backgroundColor: "rgb(3, 136, 87)", // darker green on hover
                  transform: "scale(1.03)", //englarges button on hover
                  cursor: "pointer" //changes cursor from pointer to hand when hovering over button
                }
              }}
              >
                {isLoading ? "Generating Routes..." : "Generate Routes"}
              </Button>
            </div>

          </div>

          {/* preferences section */}
          <div style={{ padding: "16px", borderBottom: `1px solid ${theme.border}`, background: theme.panel, color: theme.text, transition: "all 0.3s" }}>
            <h3 style={{ margin: "0 0 8px 0", fontSize: "15px", textTransform: "uppercase", letterSpacing: "0.5px", color: theme.subtext }}>Preferences</h3>

            {/* Noise Slider */}
            <div className="slidecontainer">
              <p>Noise Priority: <span style={{ color: "#4caf50", fontWeight: "bold" }}>{noise}</span></p>
              <input
                type="range" min="0" max="10"
                value={noise}
                className="slider"
                onChange={(e) => setNoise(e.target.value)}
                onMouseUp={regenerateIfNeeded}
                onTouchEnd={regenerateIfNeeded}
              />
            </div>

            {/* Pollution Slider */}
            <div className="slidecontainer">
              <p>Pollution Priority: <span style={{ color: "#4caf50", fontWeight: "bold" }}>{pollution}</span></p>
              <input
                type="range" min="0" max="10"
                value={pollution}
                className="slider"
                onChange={(e) => setPollution(e.target.value)}
                onMouseUp={regenerateIfNeeded}
                onTouchEnd={regenerateIfNeeded}
              />
            </div>

            {/* Lighting Slider */}
            <div className="slidecontainer">
              <p>Lighting Priority: <span style={{ color: "#4caf50", fontWeight: "bold" }}>{lighting}</span></p>
              <input
                type="range" min="0" max="10"
                value={lighting}
                className="slider"
                onChange={(e) => setLighting(e.target.value)}
                onMouseUp={regenerateIfNeeded}
                onTouchEnd={regenerateIfNeeded}
              />
            </div>

            {/* Wheelchair access toggle */}
            <div style={{ marginTop: "12px" }}>
              <p style={{ marginBottom: "8px", fontSize: "13px", fontWeight: 500 }}>Wheelchair Access:</p>

              <button
                className={`toggle-btn ${toggled ? 'toggled' : ''}`}
                onClick={() => { setToggled(!toggled); regenerateIfNeeded(); }}
              >
                <div className="thumb"></div>
              </button>
            </div>
          </div>

          {/* route comparison section */}
          <RouteList
            routeOptions={routeOptions}
            selectedIdx={selectedRouteIdx}
            theme={theme}
            onSelectRoute={(route, idx) => {
              setSelectedRouteIdx(idx);
              const pts = route.points.map(p => [p.lat, p.lon]);
              setRoutePoints(pts);
              if (pts.length > 0) setMapTarget(pts[Math.floor(pts.length / 2)]);
            }}
            onSaveRoute={handleSaveRoute}
            onShareRoute={handleShareRoute}
          />

          {/* Saved Routes section (logged in only) */}
          {isLoggedIn && (
            <div style={{ padding: "12px 16px", borderBottom: `1px solid ${theme.border}`, background: theme.panel, color: theme.text }}>
              <div
                style={{ display: "flex", justifyContent: "space-between", alignItems: "center", cursor: "pointer" }}
                onClick={() => {
                  setSavedRoutesOpen(!savedRoutesOpen);
                  if (!savedRoutesOpen) fetchSavedRoutes();
                }}
              >
                <h3 style={{ margin: 0, fontSize: "15px", textTransform: "uppercase", letterSpacing: "0.5px", color: theme.subtext }}>Saved Routes</h3>
                <span style={{ fontSize: "12px", color: theme.subtext }}>{savedRoutesOpen ? "▲" : "▼"}</span>
              </div>
              {savedRoutesOpen && (
                <div style={{ marginTop: "10px" }}>
                  {savedRoutes.length === 0 && <p style={{ color: "#aaa", fontSize: "13px" }}>No saved routes yet.</p>}
                  {savedRoutes.map((sr, idx) => (
                    <div key={idx} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "8px", marginBottom: "6px", borderRadius: "6px", border: `1px solid ${theme.border}`, background: theme.panel === "#2d2d2d" ? "#3a3a3a" : "#fafafa" }}>
                      <span style={{ fontSize: "13px", flex: 1, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{sr.routeName || `Route ${idx + 1}`}</span>
                      <button
                        onClick={() => {
                          setStartPos([sr.start.lat, sr.start.lon]);
                          setDestination([sr.dest.lat, sr.dest.lon]);
                          setDestName(sr.routeName || "Saved destination");
                          setSearchQuery(sr.routeName || "Saved destination");
                          setMapTarget([sr.dest.lat, sr.dest.lon]);
                          setPendingRouteLoad(true);
                        }}
                        style={{ padding: "4px 12px", fontSize: "12px", background: "rgb(6, 171, 74)", color: "white", border: "none", borderRadius: "4px", cursor: "pointer", marginLeft: "8px" }}
                      >
                        Load
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* hazard reporting - compact */}
          <div style={{ padding: "10px 15px", background: theme.panel, color: theme.text, display: "flex", alignItems: "center", gap: "10px" }}>
              <img src={hazardimage} alt="Hazard" style={{ width: "40px", height: "40px" }} />
              <Button
                variant="contained"
                color="error"
                size="small"
                onClick={() => setHazardOpen(true)}
                sx={{ textTransform: "none", fontWeight: "bold", flex: 1 }}
              >
                Report a Hazard
              </Button>
          </div>



        </div>

      </div>

      {/* login dialog */}
      <Dialog open={isLoginOpen} onClose={() => setisLoginOpen(false)} fullWidth maxWidth="xs">
        <form onSubmit={handleLogin}>
          <DialogTitle style={{ fontWeight: "bold" }}>Login / Sign Up</DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              name="username"
              label="Username"
              fullWidth
              required
              variant="outlined"
              style={{ marginTop: "8px" }}
            />
          </DialogContent>
          <DialogActions style={{ padding: "16px", gap: "8px" }}>
            <Button onClick={() => setisLoginOpen(false)}>Cancel</Button>
            <Button type="submit" formAction="signup" variant="outlined">Sign Up</Button>
            <Button type="submit" formAction="login" variant="contained" color="primary">Login</Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* hazard reporting window popup */}
      <Dialog //using dialog because it allows reporting box to block rest of ui, centered position and has more overall behaviour for a window
        open={hazardOpen}
        onClose={() => setHazardOpen(false)}
        fullWidth
      >
        <DialogTitle style={{ fontWeight: "bold", backgroundColor: "rgb(245, 245, 245)" }}>
          Report a Hazard
        </DialogTitle>

        <DialogContent style={{ display: 'flex', flexDirection: 'column', gap: 10}}>

          {/* hazard type dropdown menu */}
          <FormControl fullWidth>
            <label>Hazard Type</label>
            <Select
              value={hazardType}
              label="Hazard Type"
              onChange={(e) => setHazardType(e.target.value)}//used ai to help with this
            > {/*these are the dropdown options*/}
              <MenuItem value="Physical">Physical Obstruction</MenuItem>
              <MenuItem value="Environmental">Environmental Obstruction (Ice, puddles, etc.)</MenuItem>
              <MenuItem value="Infrastructure">Infrastructure Failure (Broken lamp, etc.)</MenuItem>
            </Select>
          </FormControl>

          {/* description box */}
          <TextField
            label="Description"
            multiline
            rows={2}
            fullWidth
            placeholder="Describe the hazard"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />

          {/* time + loco */}
          <div style={{ padding: '15px', backgroundColor: 'rgb(245, 245, 245)', borderRadius: '5px', border: '1px solid rgb(221, 221, 221)' }}>
            <p style={{ margin: '0', fontSize: '14px', color: 'rgb(100, 100, 100)' }}>
              <strong>Time of Reporting:</strong> {new Date().toLocaleTimeString()}
            </p>
            <p style={{ margin: '0', fontSize: '14px', color: 'rgb(100, 100, 100)' }}>
              <strong>Location:</strong> Current Location
            </p>
          </div>

        </DialogContent>

        <DialogActions style={{ padding: '16px' }}>
          <Button onClick={() => setHazardOpen(false)} color= 'rgb(100, 100, 100)' >
            Cancel
          </Button>
          <Button
            onClick={() => {
              //submits hazard info to console (hazard type, description, time of submission, and coords of hazard)
              // console.log({ hazardType, description, submitTime, startPos });
              reportHazard({
                "latitude": startPos[0],
                "longitude": startPos[1],
                "locationDescription": description,
                "description": description,
                "hazardType": hazardType,
                "reportedAt": new Date().toISOString()
              }).then(() => {
                fetchNearbyHazards(startPos[0], startPos[1]);
                if (destination) fetchNearbyHazards(destination[0], destination[1]);
                alert("Hazard Reported Successfully!");
              }).catch((err) => {
                console.error(err);
                alert("Failed to report hazard.");
              });
              setHazardOpen(false);
            }}
            variant="contained"
            color="error"
            disabled={!hazardType} // doesnt allow user to submit if they havent selected a hazard type
          >
            Submit Report
          </Button>
        </DialogActions>
      </Dialog>

    </div>
  );
}

export default App;
