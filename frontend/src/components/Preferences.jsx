import React from "react";

export default function Preferences({ noise, setNoise, pollution, setPollution, lighting, setLighting, toggled, setToggled, onPreferenceChange }) {
  const handleSliderRelease = () => {
    if (onPreferenceChange) onPreferenceChange();
  };

  return (
    <div className="preferences">
      <h3 className="panel-title">Preferences</h3>

      <div className="slider-group">
        <div className="slider-label">
          <span>Noise Priority</span>
          <span className="slider-value">{noise}</span>
        </div>
        <input
          type="range" min="0" max="10" value={noise}
          className="slider"
          onChange={(e) => setNoise(Number(e.target.value))}
          onMouseUp={handleSliderRelease}
          onTouchEnd={handleSliderRelease}
        />
      </div>

      <div className="slider-group">
        <div className="slider-label">
          <span>Pollution Priority</span>
          <span className="slider-value">{pollution}</span>
        </div>
        <input
          type="range" min="0" max="10" value={pollution}
          className="slider"
          onChange={(e) => setPollution(Number(e.target.value))}
          onMouseUp={handleSliderRelease}
          onTouchEnd={handleSliderRelease}
        />
      </div>

      <div className="slider-group">
        <div className="slider-label">
          <span>Lighting Priority</span>
          <span className="slider-value">{lighting}</span>
        </div>
        <input
          type="range" min="0" max="10" value={lighting}
          className="slider"
          onChange={(e) => setLighting(Number(e.target.value))}
          onMouseUp={handleSliderRelease}
          onTouchEnd={handleSliderRelease}
        />
      </div>

      <div className="toggle-group">
        <span>Wheelchair Access</span>
        <button
          className={`toggle-btn ${toggled ? "toggled" : ""}`}
          onClick={() => { setToggled(!toggled); handleSliderRelease(); }}
        >
          <div className="thumb" />
        </button>
      </div>
    </div>
  );
}
