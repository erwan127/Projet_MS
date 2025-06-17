import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../api/apiService';

function AddVehiclePage() {
    const navigate = useNavigate();
    const [newVehicle, setNewVehicle] = useState({
        id: '',
        marque: '',
        modele: '',
        nombrePlaces: '',
        kilometrage: 0,
        niveauCharge: 100,
        etat: 'OPERATIONNEL_EN_STATION', // Valeur par défaut de l'enum
        position: { x: 0, y: 0 }, // Position par défaut
        tempStationId: '', // Champ temporaire pour la sélection de la station
    });
    const [stations, setStations] = useState([]);
    const [loadingStations, setLoadingStations] = useState(true);
    const [errorStations, setErrorStations] = useState(null);

    useEffect(() => {
        const fetchStations = async () => {
            try {
                setLoadingStations(true);
                const data = await apiService.getStations();
                setStations(data);
            } catch (err) {
                setErrorStations('Erreur lors du chargement des stations: ' + err.message);
            } finally {
                setLoadingStations(false);
            }
        };
        fetchStations();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === "positionX" || name === "positionY") {
            setNewVehicle(prev => ({
                ...prev,
                position: {
                    ...prev.position,
                    [name === "positionX" ? "x" : "y"]: parseFloat(value) || 0
                }
            }));
        } else {
            setNewVehicle(prev => ({ ...prev, [name]: value }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Préparer le véhicule pour l'envoi (retirer tempStationId)
            const vehicleToCreate = { ...newVehicle };
            delete vehicleToCreate.tempStationId;

            // 1. Appeler le service véhicule pour créer le véhicule
            const addedVehicle = await apiService.addVehicule(vehicleToCreate);

            // 2. Si le véhicule est opérationnel et qu'une station a été choisie, l'ajouter à cette station
            if (addedVehicle.etat === 'OPERATIONNEL_EN_STATION' && newVehicle.tempStationId) {
                const addVehicleToStationResponse = await apiService.addVehicleToStation(newVehicle.tempStationId, addedVehicle.id);
                if (!addVehicleToStationResponse.success) {
                    alert(`Véhicule ${addedVehicle.id} ajouté, mais ÉCHEC de l'ajout à la station ${newVehicle.tempStationId}: ${addVehicleToStationResponse.message}`);
                    // Décision: on pourrait appeler la suppression du véhicule ici si l'ajout à la station est critique,
                    // mais pour l'instant, on affiche juste l'erreur et on navigue.
                    navigate('/vehicles');
                    return;
                }
            }

            alert(`Véhicule ${addedVehicle.id} ajouté avec succès !`);
            navigate('/vehicles');
        } catch (err) {
            alert('Erreur lors de l\'ajout du véhicule: ' + err.message);
        }
    };

    if (loadingStations) return <p>Chargement des stations pour le choix de localisation...</p>;
    if (errorStations) return <p style={{ color: 'red' }}>{errorStations}</p>;

    return (
        <div>
            <h2>Déclarer un nouveau véhicule</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>ID du Véhicule:</label>
                    <input type="text" name="id" value={newVehicle.id} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Marque:</label>
                    <input type="text" name="marque" value={newVehicle.marque} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Modèle:</label>
                    <input type="text" name="modele" value={newVehicle.modele} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Nombre de places:</label>
                    <input type="number" name="nombrePlaces" value={newVehicle.nombrePlaces} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Kilométrage (initial):</label>
                    <input type="number" name="kilometrage" value={newVehicle.kilometrage} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Niveau de charge (initial):</label>
                    <input type="number" name="niveauCharge" value={newVehicle.niveauCharge} onChange={handleChange} min="0" max="100" required />
                </div>
                <div className="form-group">
                    <label>Statut initial:</label>
                    <select name="etat" value={newVehicle.etat} onChange={handleChange} required>
                        <option value="OPERATIONNEL_EN_STATION">Opérationnel (en station)</option>
                        <option value="EN_MAINTENANCE">En maintenance</option>
                    </select>
                </div>
                <div className="form-group">
                    <label>Position X (initiale):</label>
                    <input type="number" name="positionX" value={newVehicle.position.x} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Position Y (initiale):</label>
                    <input type="number" name="positionY" value={newVehicle.position.y} onChange={handleChange} required />
                </div>
                {newVehicle.etat === 'OPERATIONNEL_EN_STATION' && (
                    <div className="form-group">
                        <label>Station de base (où le garer initialement):</label>
                        <select name="tempStationId" value={newVehicle.tempStationId} onChange={handleChange} required={newVehicle.etat === 'OPERATIONNEL_EN_STATION'}>
                            <option value="">Sélectionner une station</option>
                            {stations.map(station => (
                                <option key={station.id} value={station.id}>{station.name || `Station ${station.id}`} (Capacité: {station.capaciteGlobale})</option>
                            ))}
                        </select>
                    </div>
                )}
                <button type="submit" className="btn">Ajouter le véhicule</button>
            </form>
        </div>
    );
}

export default AddVehiclePage;