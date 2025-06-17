import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import HomePage from './pages/HomePage';
import VehiclesPage from './pages/VehiclesPage';
import VehicleDetailPage from './pages/VehicleDetailPage';
import StationsPage from './pages/StationsPage';
import StationDetailPage from './pages/StationDetailPage';
import AddVehiclePage from './pages/AddVehiclePage';
import AddStationPage from './pages/AddStationPage';
import './App.css'; // Styles généraux pour l'application

function App() {
  return (
      <Router>
        <Header />
        <div className="container">
          <Routes>
            <Route path="/" element={<HomePage />} />
            {/* Routes pour la gestion des véhicules (Version 1) */}
            <Route path="/vehicles" element={<VehiclesPage />} />
            <Route path="/vehicles/:id" element={<VehicleDetailPage />} />
            <Route path="/vehicles/add" element={<AddVehiclePage />} />

            {/* Routes pour la gestion des stations (Version 1) */}
            <Route path="/stations" element={<StationsPage />} />
            <Route path="/stations/:id" element={<StationDetailPage />} />
            <Route path="/stations/add" element={<AddStationPage />} />
          </Routes>
        </div>
      </Router>
  );
}

export default App;