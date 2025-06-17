import React, { useState, useEffect } from 'react';
import StationCard from '../components/StationCard';
import { Link } from 'react-router-dom';
import apiService from '../api/apiService';

function StationsPage() {
    const [stations, setStations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchStations = async () => {
            try {
                setLoading(true);
                const data = await apiService.getStations();
                setStations(data);
            } catch (err) {
                setError('Erreur lors du chargement des stations: ' + err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchStations();
    }, []);

    if (loading) return <p>Chargement des stations...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div>
            <h2>Liste des Stations</h2>
            <Link to="/stations/add" className="btn" style={{ marginBottom: '20px', display: 'inline-block' }}>Déclarer une nouvelle station</Link>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                {stations.length > 0 ? (
                    stations.map(station => (
                        <StationCard key={station.id} station={station} />
                    ))
                ) : (
                    <p>Aucune station disponible.</p>
                )}
            </div>
        </div>
    );
}

export default StationsPage;