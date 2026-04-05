import React from "react";

export default function ScoreBar({ label, score }) {
  const color = score >= 70 ? "#4caf50" : score >= 40 ? "#ff9800" : "#f44336";
  return (
    <div className="score-bar">
      <div className="score-bar-header">
        <span>{label}</span>
        <span className="score-bar-value">{score.toFixed(0)}</span>
      </div>
      <div className="score-bar-track">
        <div className="score-bar-fill" style={{ width: `${score}%`, background: color }} />
      </div>
    </div>
  );
}
