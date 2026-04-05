import React, { useState, useEffect, useRef } from "react";
import "leaflet/dist/leaflet.css";
import "./styles/app.css";

import Header from "./components/Header";
import SearchBar from "./components/SearchBar";
import MapView from "./components/MapView";
import SidePanel from "./components/SidePanel";
import LoginDialog from "./components/LoginDialog";
import HazardDialog from "./components/HazardDialog";

// US-14: Generate a short explanation of why a route is recommended
export const getRouteExplanation = (route) => {
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

function App() {
  const [darkMode, setDarkMode] = useState(false);
  const [highContrast, setHighContrast] = useState(false);

  // Route state
  const [routePoints, setRoutePoints] = useState([]);
  const [routeOptions, setRouteOptions] = useState([]);
  const [selectedRouteIdx, setSelectedRouteIdx] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // Auth state
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(() => localStorage.getItem("isLoggedIn") === "true");
  const [userName, setUserName] = useState(() => localStorage.getItem("userName") || "");

  // Preference state (persisted to localStorage)
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
  const [toggled, setToggled] = useState(() => localStorage.getItem("pref_wheelchair") === "true");

  useEffect(() => {
    localStorage.setItem("pref_noise", noise);
    localStorage.setItem("pref_pollution", pollution);
    localStorage.setItem("pref_lighting", lighting);
    localStorage.setItem("pref_wheelchair", toggled);
  }, [noise, pollution, lighting, toggled]);

  // Preference change triggers route regeneration
  const [regenFlag, setRegenFlag] = useState(false);
  const regenerateIfNeeded = () => setRegenFlag(true);

  // Location state
  const [startPos, setStartPos] = useState(null);
  const [destination, setDestination] = useState(null);
  const [destName, setDestName] = useState("");
  const [mapTarget, setMapTarget] = useState(null);

  // Hazard state
  const [hazardOpen, setHazardOpen] = useState(false);
  const [hazardMarkers, setHazardMarkers] = useState([]);

  // Saved routes
  const [savedRoutes, setSavedRoutes] = useState([]);
  const [savedRoutesOpen, setSavedRoutesOpen] = useState(false);

  // Search state
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const searchTimer = useRef(null);

  // Side panel
  const [panelOpen, setPanelOpen] = useState(false);

  // Pending route load flag (for shared routes / saved routes)
  const [pendingRouteLoad, setPendingRouteLoad] = useState(false);

  // --- Auth handlers ---
  const handleLogin = async (e) => {
    e.preventDefault();
    const username = new FormData(e.target).get("username").toString();
    const endpoint = e.nativeEvent.submitter.getAttribute("formaction") === "signup" ? "new" : "login";
    try {
      const response = await fetch(`/api/users/${endpoint}`, { method: "POST", body: username });
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
      setIsLoginOpen(false);
    } catch (err) {
      alert("Connection failed. Is the backend running?");
    }
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    localStorage.removeItem("userName");
    localStorage.removeItem("isLoggedIn");
  };

  // --- Data fetchers ---
  const fetchNearbyHazards = async (lat, lon) => {
    try {
      const res = await fetch(`/api/hazards/nearby?lat=${lat}&long=${lon}&distance=50`);
      if (res.ok) setHazardMarkers(await res.json());
    } catch (err) {
      console.error("Failed to fetch hazards:", err);
    }
  };

  const fetchSavedRoutes = async () => {
    try {
      const res = await fetch("/api/saved-routes/", { credentials: "include" });
      if (res.ok) setSavedRoutes(await res.json());
    } catch (err) {
      console.error("Failed to fetch saved routes:", err);
    }
  };

  // --- Search ---
  const handleSearch = (query) => {
    setSearchQuery(query);
    if (query.length < 3) { setSearchResults([]); return; }
    if (searchTimer.current) clearTimeout(searchTimer.current);
    searchTimer.current = setTimeout(async () => {
      setIsSearching(true);
      try {
        const res = await fetch(
          `/nominatim/search?format=json&q=${encodeURIComponent(query)}&limit=5&addressdetails=1&viewbox=-6.0,55.8,1.8,49.9&bounded=0`,
          { headers: { "Accept-Language": "en" } }
        );
        setSearchResults(await res.json());
      } catch (e) {
        console.error("Search failed", e);
      }
      setIsSearching(false);
    }, 500);
  };

  const selectDestination = (result) => {
    const coords = [parseFloat(result.lat), parseFloat(result.lon)];
    setDestination(coords);
    setDestName(result.display_name);
    setSearchQuery(result.display_name);
    setSearchResults([]);
    setMapTarget(coords);
  };

  // --- Route generation ---
  const handleGenerateRoutes = async () => {
    if (!startPos) { alert("Please allow the website to get your current location."); return; }
    if (!destination) { alert("Please enter a destination before generating routes."); return; }

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
      if (!res.ok) throw new Error((await res.text()) || "Failed to generate route");

      const data = await res.json();
      const routes = Array.isArray(data) ? data : [data];
      setRouteOptions(routes);
      setSelectedRouteIdx(0);

      if (routes.length > 0 && routes[0].points) {
        const pts = routes[0].points.map(p => [p.lat, p.lon]);
        setRoutePoints(pts);
        if (pts.length > 0) setMapTarget(pts[Math.floor(pts.length / 2)]);
      }
      if (destination) fetchNearbyHazards(destination[0], destination[1]);
    } catch (err) {
      console.error("Route generation failed:", err);
      alert("Failed to generate route: " + err.message);
    }
    setIsLoading(false);
  };

  // --- Hazard handlers ---
  const reportHazard = async (data) => {
    const response = await fetch("/api/hazards/new", {
      method: "POST", credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });
    if (!response.ok) throw new Error(`Failed to report hazard, status: ${response.status}`);
    return await response.json();
  };

  const handleHazardSubmit = (data) => {
    reportHazard(data).then(() => {
      fetchNearbyHazards(startPos[0], startPos[1]);
      if (destination) fetchNearbyHazards(destination[0], destination[1]);
      alert("Hazard Reported Successfully!");
    }).catch((err) => {
      console.error(err);
      alert("Failed to report hazard.");
    });
    setHazardOpen(false);
  };

  const handleUpvoteHazard = async (hazardId) => {
    try {
      const res = await fetch(`/api/hazards/upvote/${hazardId}`, { method: "PATCH", credentials: "include" });
      if (res.ok) {
        if (startPos) fetchNearbyHazards(startPos[0], startPos[1]);
        if (destination) fetchNearbyHazards(destination[0], destination[1]);
      } else {
        const text = await res.text();
        alert(text.includes("own report") ? "You cannot confirm your own hazard report." : (text || "Failed to confirm hazard."));
      }
    } catch (err) {
      console.error("Upvote failed:", err);
    }
  };

  // --- Route save/share/load ---
  const handleShareRoute = (route) => {
    if (!startPos || !destination) return;
    const params = new URLSearchParams({
      slat: startPos[0], slon: startPos[1],
      dlat: destination[0], dlon: destination[1],
      noise, pollution, lighting, wheelchair: toggled ? 1 : 0
    });
    const url = `${window.location.origin}?${params.toString()}`;
    navigator.clipboard.writeText(url).then(() => alert("Route link copied to clipboard!")).catch(() => prompt("Copy this link:", url));
  };

  const handleSaveRoute = async (route) => {
    if (!isLoggedIn) { alert("Please log in to save routes."); return; }
    try {
      const res = await fetch("/api/saved-routes/", {
        method: "POST", credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          routeName: `Route to ${destName.split(",")[0]}`,
          start: { lat: startPos[0], lon: startPos[1] },
          dest: { lat: destination[0], lon: destination[1] },
          noiseWeight: parseInt(noise), pollutionWeight: parseInt(pollution),
          lightingWeight: parseInt(lighting), wheelchairWeight: toggled ? 10 : 0
        })
      });
      alert(res.ok ? "Route saved!" : "Failed to save route.");
    } catch (err) {
      console.error("Save route failed:", err);
    }
  };

  const handleLoadSavedRoute = (sr) => {
    setStartPos([sr.start.lat, sr.start.lon]);
    setDestination([sr.dest.lat, sr.dest.lon]);
    setDestName(sr.routeName || "Saved destination");
    setSearchQuery(sr.routeName || "Saved destination");
    setMapTarget([sr.dest.lat, sr.dest.lon]);
    setPendingRouteLoad(true);
  };

  const handleSelectRoute = (route, idx) => {
    setSelectedRouteIdx(idx);
    const pts = route.points.map(p => [p.lat, p.lon]);
    setRoutePoints(pts);
    if (pts.length > 0) setMapTarget(pts[Math.floor(pts.length / 2)]);
  };

  // --- Effects ---

  // Geolocation on mount
  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => setStartPos([pos.coords.latitude, pos.coords.longitude]),
        () => setStartPos([52.4862, -1.8904])
      );
    } else {
      setStartPos([52.4862, -1.8904]);
    }
  }, []);

  // Load shared route from URL
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    if (params.has("dlat") && params.has("dlon")) {
      setDestination([parseFloat(params.get("dlat")), parseFloat(params.get("dlon"))]);
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

  // Trigger route generation after loading saved/shared route
  useEffect(() => {
    if (pendingRouteLoad && startPos && destination) {
      setPendingRouteLoad(false);
      handleGenerateRoutes();
    }
  }, [pendingRouteLoad, startPos, destination]);

  // Fetch hazards when position available
  useEffect(() => {
    if (startPos) fetchNearbyHazards(startPos[0], startPos[1]);
  }, [startPos]);

  // Auto-regenerate when preferences change
  useEffect(() => {
    if (regenFlag && routeOptions.length > 0 && startPos && destination && !isLoading) {
      setRegenFlag(false);
      handleGenerateRoutes();
    }
  }, [regenFlag, routeOptions, startPos, destination, isLoading]);

  return (
    <div className={`app-root ${darkMode ? "dark" : ""}`}>
      <Header
        userName={userName}
        isLoggedIn={isLoggedIn}
        darkMode={darkMode}
        highContrast={highContrast}
        onToggleDark={() => setDarkMode(!darkMode)}
        onToggleContrast={() => setHighContrast(!highContrast)}
        onLoginClick={() => setIsLoginOpen(true)}
        onLogout={handleLogout}
      />

      <div className="app-body">
        <SearchBar
          searchQuery={searchQuery}
          searchResults={searchResults}
          isSearching={isSearching}
          destName={destName}
          onSearch={handleSearch}
          onSelectResult={selectDestination}
        />

        <MapView
          startPos={startPos}
          destination={destination}
          mapTarget={mapTarget}
          routePoints={routePoints}
          routeOptions={routeOptions}
          selectedRouteIdx={selectedRouteIdx}
          hazardMarkers={hazardMarkers}
          highContrast={highContrast}
          onUpvoteHazard={handleUpvoteHazard}
          isLoggedIn={isLoggedIn}
        />

        <button className="fab-panel-toggle" onClick={() => setPanelOpen(!panelOpen)}>
          {panelOpen ? "\u2715" : "\u2630"}
        </button>

        <SidePanel
          open={panelOpen}
          onClose={() => setPanelOpen(false)}
          noise={noise} setNoise={setNoise}
          pollution={pollution} setPollution={setPollution}
          lighting={lighting} setLighting={setLighting}
          toggled={toggled} setToggled={setToggled}
          onPreferenceChange={regenerateIfNeeded}
          routeOptions={routeOptions}
          selectedRouteIdx={selectedRouteIdx}
          onSelectRoute={handleSelectRoute}
          onSaveRoute={handleSaveRoute}
          onShareRoute={handleShareRoute}
          isLoggedIn={isLoggedIn}
          savedRoutes={savedRoutes}
          savedRoutesOpen={savedRoutesOpen}
          setSavedRoutesOpen={setSavedRoutesOpen}
          onFetchSavedRoutes={fetchSavedRoutes}
          onLoadSavedRoute={handleLoadSavedRoute}
          onGenerateRoutes={handleGenerateRoutes}
          isLoading={isLoading}
          darkMode={darkMode}
          onHazardOpen={() => setHazardOpen(true)}
        />
      </div>

      <LoginDialog
        open={isLoginOpen}
        onClose={() => setIsLoginOpen(false)}
        onLogin={handleLogin}
      />

      <HazardDialog
        open={hazardOpen}
        onClose={() => setHazardOpen(false)}
        startPos={startPos || [52.4862, -1.8904]}
        onSubmit={handleHazardSubmit}
      />
    </div>
  );
}

export default App;
