import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import apiService from '../api/apiService';

function VehicleDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [vehicle, setVehicle] = useState(null);
    const [stationLocation, setStationLocation] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchVehicleAndLocation = async () => {
            try {
                setLoading(true);
                const vehicleData = await apiService.getVehiculeById(id);
                setVehicle(vehicleData);

                // Si le véhicule est en station, trouver quelle station via station-service
                if (vehicleData && vehicleData.etat === 'OPERATIONNEL_EN_STATION') {
                    try {
                        const station = await apiService.findStationWithVehicle(id);
                        if (station) {
                            setStationLocation(station);
                        } else {
                            setStationLocation(null);
                        }
                    } catch (stationErr) {
                        console.error("Erreur lors de la recherche de la station du véhicule:", stationErr);
                        setStationLocation(null);
                    }
                } else {
                    setStationLocation(null);
                }

            } catch (err) {
                setError('Erreur lors du chargement du véhicule: ' + err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchVehicleAndLocation();
    }, [id]);

    const handleRemoveVehicle = async () => {
        if (!vehicle) return;

        if (window.confirm(`Êtes-vous sûr de vouloir retirer le véhicule ${vehicle.id} ?`)) {
            try {
                // Pré-vérification ou action nécessaire avant suppression
                if (vehicle.etat === 'OPERATIONNEL_EN_LOCATION') {
                    alert("Le véhicule est en cours de location. Il ne peut pas être retiré.");
                    return;
                }

                // Si le véhicule est en station, il est préférable de le retirer de la station avant de le supprimer du service véhicule
                // Cela dépend de votre logique métier et de la cascade dans votre base de données.
                // Ici, nous faisons un appel explicite si le véhicule est en station.
                if (vehicle.etat === 'OPERATIONNEL_EN_STATION' && stationLocation) {
                    const removeStationResponse = await apiService.removeVehicleFromStation(stationLocation.id, vehicle.id);
                    if (!removeStationResponse.success) {
                        alert(`Échec du retrait du véhicule de la station ${stationLocation.id}: ${removeStationResponse.message}. Annulation de la suppression.`);
                        return;
                    }
                }

                const success = await apiService.removeVehicule(id);
                if (success) {
                    alert('Véhicule retiré avec succès.');
                    navigate('/vehicles');
                } else {
                    alert('Échec du retrait du véhicule. Il est peut-être en cours de location ou n\'existe pas.');
                }
            } catch (err) {
                alert('Erreur lors du retrait du véhicule: ' + err.message);
            }
        }
    };

    if (loading) return <p>Chargement du véhicule...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;
    if (!vehicle) return <p>Véhicule non trouvé.</p>;

    return (
        <div>
            <h2>Détails du Véhicule: {vehicle.id}</h2>
            <p><strong>Marque:</strong> {vehicle.marque}</p>
            <p><strong>Modèle:</strong> {vehicle.modele}</p>
            <p><strong>Nombre de places:</strong> {vehicle.nombrePlaces}</p>
            <p><strong>Kilométrage:</strong> {vehicle.kilometrage} km</p>
            <p><strong>Niveau de charge:</strong> {vehicle.niveauCharge}%</p>
            <p><strong>Statut:</strong> {vehicle.etat}</p>
            {vehicle.position && <p><strong>Position Actuelle:</strong> ({vehicle.position.x}, {vehicle.position.y})</p>}
            {vehicle.etat === 'OPERATIONNEL_EN_STATION' && stationLocation ? (
                <p><strong>Garé à la station:</strong> <Link to={`/stations/${stationLocation.id}`}>{stationLocation.name || `Station ${stationLocation.id}`}</Link></p>
            ) : (
                <p><strong>Localisation:</strong> {
                    vehicle.etat === 'OPERATIONNEL_EN_LOCATION' ? 'En cours de location' :
                        vehicle.etat === 'EN_MAINTENANCE' ? 'En maintenance' : 'Statut inconnu'
                }</p>
            )}

            <button className="btn" onClick={() => navigate('/vehicles')} style={{ marginRight: '10px' }}>Retour à la liste</button>
            <button className="btn" onClick={handleRemoveVehicle} style={{ backgroundColor: '#dc3545' }}>Retirer ce véhicule</button>
        </div>
    );
}

export default VehicleDetailPage;