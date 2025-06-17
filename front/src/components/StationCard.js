import React from 'react';
import { Link } from 'react-router-dom';

function StationCard({ station }) {
    const parkedCount = station.vehiculeIds ? station.vehiculeIds.length : 0;
    const freeSpots = station.capaciteGlobale - parkedCount;
    return (
        <div className="card">
            <h3>{station.name || `Station ${station.id}`} ({station.id})</h3> {/* 'name' n'est pas dans l'entité, ajout d'un fallback */}
            <p>Position: ({station.position.x}, {station.position.y})</p>
            <p>Capacité: {station.capaciteGlobale}</p>
            <p>Véhicules garés: {parkedCount}</p>
            <p>Places libres: {freeSpots}</p>
            <Link to={`/stations/${station.id}`} className="btn">Détails</Link>
        </div>
    );
}

export default StationCard;