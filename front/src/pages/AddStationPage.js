import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../api/apiService';

function AddStationPage() {
    const navigate = useNavigate();
    const [newStation, setNewStation] = useState({
        id: '',
        name: '', // Non présent dans l'entité Station Java, mais utile pour l'UI
        position: { x: 0, y: 0 },
        capaciteGlobale: '',
        vehiculeIds: [], // Initialement vide
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === "positionX" || name === "positionY") {
            setNewStation(prev => ({
                ...prev,
                position: {
                    ...prev.position,
                    [name === "positionX" ? "x" : "y"]: parseFloat(value) || 0
                }
            }));
        } else {
            setNewStation(prev => ({ ...prev, [name]: value }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Préparer l'objet station pour l'envoi, en s'assurant que les noms des champs correspondent
            const stationToCreate = {
                id: newStation.id,
                position: newStation.position,
                capaciteGlobale: parseInt(newStation.capaciteGlobale), // Convertir en nombre entier
                vehiculeIds: [] // Toujours vide à la création
            };

            const addedStation = await apiService.addStation(stationToCreate);
            alert(`Station ${newStation.name || newStation.id} ajoutée avec succès !`);
            navigate('/stations');
        } catch (err) {
            alert('Erreur lors de l\'ajout de la station: ' + err.message);
        }
    };

    return (
        <div>
            <h2>Déclarer une nouvelle station</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>ID de la Station:</label>
                    <input type="text" name="id" value={newStation.id} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Nom de la Station:</label> {/* Nom pour l'UI, ne sera pas envoyé au backend dans l'entité Station */}
                    <input type="text" name="name" value={newStation.name} onChange={handleChange} />
                </div>
                <div className="form-group">
                    <label>Position X:</label>
                    <input type="number" name="positionX" value={newStation.position.x} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Position Y:</label>
                    <input type="number" name="positionY" value={newStation.position.y} onChange={handleChange} required />
                </div>
                <div className="form-group">
                    <label>Capacité globale:</label>
                    <input type="number" name="capaciteGlobale" value={newStation.capaciteGlobale} onChange={handleChange} required />
                </div>
                <button type="submit" className="btn">Ajouter la station</button>
            </form>
        </div>
    );
}

export default AddStationPage;