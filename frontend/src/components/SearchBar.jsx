import React, { useRef } from "react";

export default function SearchBar({ searchQuery, onSearch, searchResults, isSearching, onSelectResult, destName }) {
  const inputRef = useRef(null);

  return (
    <div className="search-bar">
      <div className="search-input-wrapper">
        <svg className="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
        <input
          ref={inputRef}
          type="text"
          placeholder="Search destination in England..."
          value={searchQuery}
          onChange={(e) => onSearch(e.target.value)}
        />
        {searchQuery && (
          <button className="search-clear" onClick={() => onSearch("")}>&times;</button>
        )}
      </div>

      {isSearching && <div className="search-status">Searching...</div>}

      {searchResults.length > 0 && (
        <ul className="search-dropdown">
          {searchResults.map((result) => (
            <li key={result.place_id} onClick={() => onSelectResult(result)}>
              {result.display_name}
            </li>
          ))}
        </ul>
      )}

      {destName && (
        <p className="dest-label">
          {destName.split(",").slice(0, 3).join(",")}
        </p>
      )}
    </div>
  );
}
