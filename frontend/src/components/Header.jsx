import React from "react";

export default function Header({ isLoggedIn, userName, darkMode, highContrast, onToggleDark, onToggleContrast, onLoginClick, onLogout }) {
  return (
    <header className="app-header">
      <h1 className="logo" onClick={() => window.location.reload()}>
        RouteMind
      </h1>

      <div className="header-right">
        {isLoggedIn && <span className="hello">Hello, {userName}</span>}
        <button className="header-btn" onClick={onToggleContrast}>
          {highContrast ? "Normal Map" : "High Contrast"}
        </button>
        <button className="header-btn" onClick={onToggleDark}>
          {darkMode ? "Light" : "Dark"}
        </button>
        {!isLoggedIn ? (
          <button className="header-btn" onClick={onLoginClick}>Login</button>
        ) : (
          <button className="header-btn" onClick={onLogout}>Logout</button>
        )}
      </div>
    </header>
  );
}
