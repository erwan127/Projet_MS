import React from 'react';
import { Link } from 'react-router-dom';

function VehicleCard({ vehicle }) {
    return (
        <div className="card">
            <h3>{vehicle.marque} {vehicle.modele} ({vehicle.id})</h3>
            <p>Statut: {vehicle.etat}</p>
            <p>Charge: {vehicle.niveauCharge}%</p>
            <p>Kilométrage: {vehicle.kilometrage} km</p>
            <p>Places: {vehicle.nombrePlaces}</p>
            <p>Position: {vehicle.position ? `(${vehicle.position.x}, ${vehicle.position.y})` : 'N/A'}</p>
            <Link to={`/vehicles/${vehicle.id}`} className="btn">Détails</Link>
        </div>
    );
}

export default VehicleCard;