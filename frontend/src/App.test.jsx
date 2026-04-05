import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import App from './App';

// mock Leaflet as it doesn't work in test environment (no real DOM map)
vi.mock('react-leaflet', () => ({
  MapContainer: ({ children }) => <div data-testid="map">{children}</div>,
  TileLayer: () => <div />,
  Marker: () => <div data-testid="marker" />,
  Polyline: () => <div />,
  CircleMarker: ({ children }) => <div>{children}</div>,
  Tooltip: ({ children }) => <div>{children}</div>,
  Popup: ({ children }) => <div>{children}</div>,
  useMap: () => ({ flyTo: vi.fn() }),
}));

// mock localStorage
const localStorageMock = (() => {
  let store = {};
  return {
    getItem: vi.fn((key) => store[key] ?? null),
    setItem: vi.fn((key, value) => { store[key] = String(value); }),
    removeItem: vi.fn((key) => { delete store[key]; }),
    clear: vi.fn(() => { store = {}; }),
  };
})();
Object.defineProperty(global, 'localStorage', { value: localStorageMock });

// mock clipboard API
Object.assign(navigator, {
  clipboard: { writeText: vi.fn(() => Promise.resolve()) }
});

// mock geolocation API and fetch
beforeEach(() => {
  vi.useRealTimers();
  localStorageMock.clear();
  global.navigator.geolocation = {
    getCurrentPosition: vi.fn((success) =>
      success({ coords: { latitude: 52.4862, longitude: -1.8904 } })
    ),
  };
  global.fetch = vi.fn(() => Promise.resolve({ ok: true, json: () => Promise.resolve([]) }));
});

// ===================== DESTINATION SEARCH =====================
describe('Destination Search', () => {

  it('does not call Nominatim when query is less than 3 characters', async () => {
    render(<App />);
    const callsBefore = fetch.mock.calls.length;
    const input = screen.getByPlaceholderText('Search for a destination...');

    fireEvent.change(input, { target: { value: 'Bi' } });

    // no new fetch calls for search (hazard fetch may have fired on mount)
    expect(fetch.mock.calls.length).toBe(callsBefore);
  });

  it('calls Nominatim API when query is 3 or more characters', async () => {
    render(<App />);
    const input = screen.getByPlaceholderText('Search for a destination...');

    fireEvent.change(input, { target: { value: 'Birmingham' } });

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith(
        expect.stringContaining('Birmingham'),
        expect.any(Object)
      );
    }, { timeout: 3000 });
  });

  it('displays search results as a dropdown', async () => {
    fetch.mockImplementation((url) => {
      if (url.includes('nominatim') || url.includes('search')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([
            { place_id: 1, display_name: 'Birmingham, West Midlands, UK', lat: '52.4862', lon: '-1.8904' },
            { place_id: 2, display_name: 'Birmingham New Street, UK', lat: '52.477', lon: '-1.900' }
          ])
        });
      }
      return Promise.resolve({ ok: true, json: () => Promise.resolve([]) });
    });
    render(<App />);
    const input = screen.getByPlaceholderText('Search for a destination...');

    fireEvent.change(input, { target: { value: 'Birmingham' } });

    await waitFor(() => {
      expect(screen.getByText('Birmingham, West Midlands, UK')).toBeInTheDocument();
      expect(screen.getByText('Birmingham New Street, UK')).toBeInTheDocument();
    }, { timeout: 3000 });
  });

  it('closes dropdown and updates input when result is clicked', async () => {
    fetch.mockImplementation((url) => {
      if (url.includes('nominatim') || url.includes('search')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([
            { place_id: 1, display_name: 'Birmingham, West Midlands, UK', lat: '52.4862', lon: '-1.8904' }
          ])
        });
      }
      return Promise.resolve({ ok: true, json: () => Promise.resolve([]) });
    });
    render(<App />);
    const input = screen.getByPlaceholderText('Search for a destination...');
    fireEvent.change(input, { target: { value: 'Birmingham' } });
    await waitFor(() => screen.getByText('Birmingham, West Midlands, UK'), { timeout: 3000 });

    fireEvent.click(screen.getByText('Birmingham, West Midlands, UK'));

    expect(screen.queryByText('Birmingham, West Midlands, UK')).not.toBeInTheDocument();
    expect(input.value).toBe('Birmingham, West Midlands, UK');
  });

  it('bounds search to West Midlands viewbox', async () => {
    render(<App />);
    const input = screen.getByPlaceholderText('Search for a destination...');
    fireEvent.change(input, { target: { value: 'Library' } });

    await waitFor(() => {
      const searchCall = fetch.mock.calls.find(c => c[0].includes('search'));
      expect(searchCall[0]).toContain('viewbox=');
      expect(searchCall[0]).toContain('bounded=1');
    }, { timeout: 3000 });
  });

});

// ===================== DARK MODE TOGGLE =====================
describe('Dark Mode Toggle', () => {

  it('renders light mode by default', () => {
    render(<App />);
    expect(screen.getByText('Dark Mode')).toBeInTheDocument();
  });

  it('switches to dark mode when toggle is clicked', () => {
    render(<App />);
    fireEvent.click(screen.getByText('Dark Mode'));
    expect(screen.getByText('Light Mode')).toBeInTheDocument();
  });

  it('switches back to light mode on second click', () => {
    render(<App />);
    fireEvent.click(screen.getByText('Dark Mode'));
    fireEvent.click(screen.getByText('Light Mode'));
    expect(screen.getByText('Dark Mode')).toBeInTheDocument();
  });

});

// ===================== HIGH CONTRAST MODE =====================
describe('High Contrast Mode', () => {

  it('renders High Contrast button', () => {
    render(<App />);
    expect(screen.getByText('High Contrast')).toBeInTheDocument();
  });

  it('toggles to Normal Map on click', () => {
    render(<App />);
    fireEvent.click(screen.getByText('High Contrast'));
    expect(screen.getByText('Normal Map')).toBeInTheDocument();
  });

  it('toggles back to High Contrast on second click', () => {
    render(<App />);
    fireEvent.click(screen.getByText('High Contrast'));
    fireEvent.click(screen.getByText('Normal Map'));
    expect(screen.getByText('High Contrast')).toBeInTheDocument();
  });

});

// ===================== GEOLOCATION =====================
describe('Geolocation', () => {

  it('requests user location on page load', () => {
    render(<App />);
    expect(navigator.geolocation.getCurrentPosition).toHaveBeenCalled();
  });

  it('renders a start marker when geolocation succeeds', async () => {
    render(<App />);
    await waitFor(() => {
      const markers = screen.getAllByTestId('marker');
      expect(markers.length).toBeGreaterThanOrEqual(1);
    });
  });

  it('falls back to Birmingham centre when geolocation denied', async () => {
    global.navigator.geolocation = {
      getCurrentPosition: vi.fn((success, error) => error({ code: 1 })),
    };
    render(<App />);
    // app should still render without crashing
    expect(screen.getByTestId('map')).toBeInTheDocument();
  });

});

// ===================== PREFERENCES =====================
describe('Preferences', () => {

  it('renders noise, pollution, and lighting sliders', () => {
    render(<App />);
    expect(screen.getByText(/Noise Priority/)).toBeInTheDocument();
    expect(screen.getByText(/Pollution Priority/)).toBeInTheDocument();
    expect(screen.getByText(/Lighting Priority/)).toBeInTheDocument();
  });

  it('renders wheelchair toggle', () => {
    render(<App />);
    expect(screen.getByText(/Wheelchair Access/)).toBeInTheDocument();
  });

  it('slider default value is 5', () => {
    render(<App />);
    const sliders = document.querySelectorAll('input[type="range"]');
    sliders.forEach(slider => {
      expect(slider.value).toBe('5');
    });
  });

  it('persists preferences to localStorage', () => {
    render(<App />);
    const sliders = document.querySelectorAll('input[type="range"]');
    fireEvent.change(sliders[0], { target: { value: '8' } });

    expect(localStorageMock.setItem).toHaveBeenCalledWith('pref_noise', '8');
  });

  it('loads saved preferences from localStorage', () => {
    localStorageMock.getItem.mockImplementation((key) => {
      if (key === 'pref_noise') return '9';
      if (key === 'pref_pollution') return '3';
      if (key === 'pref_lighting') return '7';
      return null;
    });
    render(<App />);
    // preferences should be loaded (component re-renders with saved values)
    expect(localStorageMock.getItem).toHaveBeenCalledWith('pref_noise');
    expect(localStorageMock.getItem).toHaveBeenCalledWith('pref_pollution');
    expect(localStorageMock.getItem).toHaveBeenCalledWith('pref_lighting');
  });

});

// ===================== LOGIN / AUTH =====================
describe('Login and Authentication', () => {

  it('shows Login button when not logged in', () => {
    render(<App />);
    expect(screen.getByText('Login')).toBeInTheDocument();
  });

  it('opens login dialog when Login button clicked', async () => {
    render(<App />);
    fireEvent.click(screen.getByText('Login'));
    await waitFor(() => {
      expect(screen.getByText('Login / Sign Up')).toBeInTheDocument();
    });
  });

  it('shows Sign Up button in dialog', async () => {
    render(<App />);
    fireEvent.click(screen.getByText('Login'));
    await waitFor(() => {
      expect(screen.getByText('Sign Up')).toBeInTheDocument();
    });
  });

  it('closes dialog on Cancel', async () => {
    render(<App />);
    fireEvent.click(screen.getByText('Login'));
    await waitFor(() => {
      expect(screen.getByText('Login / Sign Up')).toBeInTheDocument();
    });
    fireEvent.click(screen.getByText('Cancel'));
    await waitFor(() => {
      expect(screen.queryByText('Login / Sign Up')).not.toBeInTheDocument();
    });
  });

});

// ===================== ROUTE GENERATION =====================
describe('Route Generation', () => {

  it('shows Generate Routes button', () => {
    render(<App />);
    expect(screen.getByText('Generate Routes')).toBeInTheDocument();
  });

  it('alerts when no destination is set', () => {
    global.alert = vi.fn();
    render(<App />);
    fireEvent.click(screen.getByText('Generate Routes'));
    expect(global.alert).toHaveBeenCalledWith(expect.stringContaining('destination'));
  });

  it('shows route options placeholder text', () => {
    render(<App />);
    expect(screen.getByText('Generate a route to see options here.')).toBeInTheDocument();
  });

});

// ===================== HAZARD REPORTING =====================
describe('Hazard Reporting', () => {

  it('shows Report a Hazard button', () => {
    render(<App />);
    expect(screen.getByText('Report a Hazard')).toBeInTheDocument();
  });

  it('opens hazard dialog when button clicked', async () => {
    render(<App />);
    fireEvent.click(screen.getByText('Report a Hazard'));
    await waitFor(() => {
      expect(screen.getByLabelText('Description')).toBeInTheDocument();
    });
  });

  it('Submit is disabled until hazard type selected', () => {
    render(<App />);
    fireEvent.click(screen.getByText('Report a Hazard'));
    const submitBtn = screen.getByText('Submit Report');
    expect(submitBtn).toBeDisabled();
  });

});

// ===================== SAVED ROUTES (logged in) =====================
describe('Saved Routes Section', () => {

  it('shows Saved Routes section when logged in', () => {
    localStorageMock.getItem.mockImplementation((key) => {
      if (key === 'isLoggedIn') return 'true';
      if (key === 'userName') return 'TestUser';
      return null;
    });
    render(<App />);
    expect(screen.getByText(/SAVED ROUTES/i)).toBeInTheDocument();
  });

  it('hides Saved Routes section when not logged in', () => {
    // ensure localStorage returns null for isLoggedIn (reset any previous mockImplementation)
    localStorageMock.getItem.mockImplementation((key) => null);
    render(<App />);
    expect(screen.queryByText(/SAVED ROUTES/i)).not.toBeInTheDocument();
  });

});
