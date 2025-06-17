import React, { useState, useEffect } from 'react';
import VehicleCard from '../components/VehicleCard';
import { Link } from 'react-router-dom';
import apiService from '../api/apiService';

function VehiclesPage() {
    const [vehicles, setVehicles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchVehicles = async () => {
            try {
                setLoading(true);
                const data = await apiService.getVehicules();
                setVehicles(data);
            } catch (err) {
                setError('Erreur lors du chargement des véhicules: ' + err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchVehicles();
    }, []);

    if (loading) return <p>Chargement des véhicules...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div>
            <h2>Liste des Véhicules</h2>
            <Link to="/vehicles/add" className="btn" style={{ marginBottom: '20px', display: 'inline-block' }}>Déclarer un nouveau véhicule</Link>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                {vehicles.length > 0 ? (
                    vehicles.map(vehicle => (
                        <VehicleCard key={vehicle.id} vehicle={vehicle} />
                    ))
                ) : (
                    <p>Aucun véhicule disponible.</p>
                )}
            </div>
        </div>
    );
}

export default VehiclesPage;