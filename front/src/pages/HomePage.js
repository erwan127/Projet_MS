import React from 'react';
// Importez Link depuis react-router-dom
import { Link } from 'react-router-dom';

function HomePage() {
    return (
        <div>
            <h2>Bienvenue sur EV'MIAGE - Version 1</h2>
            <p>Cette plateforme permet la gestion des véhicules électriques et de leurs stations de recharge.</p>
            <p>Utilisez le menu de navigation ou les liens ci-dessous pour :</p>

            {/* Section Gestion des Véhicules */}
            <h3>Gestion des Véhicules :</h3>
            <ul>
                <li><Link to="/vehicules/liste">Lister tous les véhicules</Link></li>
                <li><Link to="/vehicules/ajouter">Déclarer un nouveau véhicule</Link></li>
                {/* Ajoutez d'autres liens pour consulter/retirer si vous avez des routes spécifiques */}
            </ul>

            {/* Section Gestion des Stations */}
            <h3>Gestion des Stations :</h3>
            <ul>
                <li><Link to="/stations/liste">Lister toutes les stations</Link></li>
                <li><Link to="/stations/ajouter">Déclarer une nouvelle station</Link></li>
                {/* Ajoutez d'autres liens pour consulter/retirer si vous avez des routes spécifiques */}
            </ul>

            {/* Ou des boutons si vous préférez */}
            {/* <button onClick={() => window.location.href='/vehicules/liste'}>Voir les Véhicules</button> */}
            {/* Attention : Utiliser Link est préférable pour la navigation interne avec React Router */}

        </div>
    );
}

export default HomePage;