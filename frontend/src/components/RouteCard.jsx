import React from "react";
import ScoreBar from "./ScoreBar";

function getRouteExplanation(route) {
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
}

export default function RouteCard({ route, idx, isSelected, onSelect, onSave, onShare, darkMode }) {
  const scoreColor = route.totalScore >= 70 ? "#4caf50" : route.totalScore >= 40 ? "#ff9800" : "#f44336";

  return (
    <div
      className={`route-card ${isSelected ? "route-card-selected" : ""}`}
      onClick={() => onSelect(route, idx)}
    >
      <div className="route-card-header">
        <span className="route-card-name">Route {idx + 1}</span>
        <span className="route-card-meta">
          {route.distanceKm?.toFixed(1) || "?"} km · {route.durationMin?.toFixed(0) || "?"} min
        </span>
      </div>

      <div className="route-card-score-row">
        <div>
          <span className="route-card-score-label">Overall Score</span>
          <div className="route-card-score-value" style={{ color: scoreColor }}>
            {route.totalScore?.toFixed(1) || "0.0"}
          </div>
        </div>
        <div className="route-card-actions">
          <button
            className={`btn ${isSelected ? "btn-selected" : "btn-primary"}`}
            onClick={(e) => { e.stopPropagation(); onSelect(route, idx); }}
          >
            {isSelected ? "Selected" : "Select"}
          </button>
          {onSave && (
            <button className="btn btn-outline" onClick={(e) => { e.stopPropagation(); onSave(route); }}>
              Save
            </button>
          )}
          {onShare && (
            <button className="btn btn-outline" onClick={(e) => { e.stopPropagation(); onShare(route); }}>
              Share
            </button>
          )}
        </div>
      </div>

      <p className="route-card-explanation">{getRouteExplanation(route)}</p>

      <ScoreBar label="Noise" score={route.noiseScore || 0} />
      <ScoreBar label="Pollution" score={route.pollutionScore || 0} />
      <ScoreBar label="Lighting" score={route.lightingScore || 0} />
      <ScoreBar label="Wheelchair" score={route.wheelchairScore || 0} />
    </div>
  );
}
