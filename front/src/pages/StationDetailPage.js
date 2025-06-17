import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import apiService from '../api/apiService';

function StationDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [station, setStation] = useState(null);
    const [parkedVehicleDetails, setParkedVehicleDetails] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchStationAndVehicles = async () => {
            try {
                setLoading(true);
                const stationData = await apiService.getStationById(id);
                setStation(stationData);

                // Récupérer les détails de chaque véhicule garé
                if (stationData && stationData.vehiculeIds && stationData.vehiculeIds.length > 0) {
                    const vehiclePromises = stationData.vehiculeIds.map(async (vehicleId) => {
                        try {
                            return await apiService.getVehiculeById(vehicleId);
                        } catch (err) {
                            console.error(`Erreur lors du chargement du véhicule ${vehicleId}:`, err);
                            return null;
                        }
                    });
                    const details = (await Promise.all(vehiclePromises)).filter(Boolean); // Filtrer les null
                    setParkedVehicleDetails(details);
                } else {
                    setParkedVehicleDetails([]);
                }
            } catch (err) {
                setError('Erreur lors du chargement de la station ou de ses véhicules: ' + err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchStationAndVehicles();
    }, [id]);

    const handleRemoveStation = async () => {
        if (!station) return;

        // Vérifier si des véhicules sont garés avant de supprimer
        if (station.vehiculeIds && station.vehiculeIds.length > 0) {
            alert("Impossible de retirer cette station : des véhicules y sont garés. Veuillez d'abord les déplacer ou les retirer.");
            return;
        }

        if (window.confirm(`Êtes-vous sûr de vouloir retirer la station ${station.id} ?`)) {
            try {
                const success = await apiService.removeStation(id);
                if (success) {
                    alert('Station retirée avec succès.');
                    navigate('/stations');
                } else {
                    alert('Échec du retrait de la station. Elle n\'existe peut-être pas.');
                }
            } catch (err) {
                alert('Erreur lors du retrait de la station: ' + err.message);
            }
        }
    };

    if (loading) return <p>Chargement de la station...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;
    if (!station) return <p>Station non trouvée.</p>;

    const parkedCount = station.vehiculeIds ? station.vehiculeIds.length : 0;
    const freeSpots = station.capaciteGlobale - parkedCount;

    return (
        <div>
            <h2>Détails de la Station: {station.id}</h2>
            <p><strong>Nom:</strong> {station.name || `Station ${station.id}`}</p>
            <p><strong>Position:</strong> ({station.position.x}, {station.position.y})</p>
            <p><strong>Capacité globale:</strong> {station.capaciteGlobale}</p>
            <p><strong>Nombre de véhicules garés:</strong> {parkedCount}</p>
            <p><strong>Nombre de places libres:</strong> {freeSpots}</p>

            <h3>Véhicules garés à cette station:</h3>
            {parkedVehicleDetails.length > 0 ? (
                <ul>
                    {parkedVehicleDetails.map(vehicle => (
                        <li key={vehicle.id}>
                            <Link to={`/vehicles/${vehicle.id}`}>{vehicle.marque} {vehicle.modele} ({vehicle.id})</Link> - Charge: {vehicle.niveauCharge}%
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Aucun véhicule garé à cette station pour le moment.</p>
            )}

            <button className="btn" onClick={() => navigate('/stations')} style={{ marginRight: '10px' }}>Retour à la liste</button>
            <button className="btn" onClick={handleRemoveStation} style={{ backgroundColor: '#dc3545' }}>Retirer cette station</button>
        </div>
    );
}

export default StationDetailPage;