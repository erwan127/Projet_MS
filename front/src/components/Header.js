import React from 'react';
import { Link } from 'react-router-dom';

function Header() {
    return (
        <header className="header">
            <h1>EV'MIAGE - V1</h1>
            <nav>
                <ul>
                    <li><Link to="/">Accueil</Link></li>
                    <li><Link to="/vehicles">Véhicules</Link></li>
                    <li><Link to="/stations">Stations</Link></li>
                </ul>
            </nav>
        </header>
    );
}

export default Header;