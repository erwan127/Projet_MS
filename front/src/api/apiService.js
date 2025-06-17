// src/api/apiService.js
const API_BASE_URL = 'http://localhost:8080/api'; // Votre API Gateway URL

const apiService = {
    // --- VEHICULE SERVICE ---
    getVehicules: async () => {
        const response = await fetch(`${API_BASE_URL}/vehicules`);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to fetch vehicles: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    },
    getVehiculeById: async (id) => {
        const response = await fetch(`${API_BASE_URL}/vehicules/${id}`);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to fetch vehicle ${id}: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    },
    addVehicule: async (vehicule) => {
        // Les noms des champs dans 'vehicule' doivent correspondre à l'entité Java
        const response = await fetch(`${API_BASE_URL}/vehicules`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(vehicule),
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to add vehicle: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    },
    removeVehicule: async (id) => {
        const response = await fetch(`${API_BASE_URL}/vehicules/${id}`, {
            method: 'DELETE',
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to remove vehicle ${id}: ${response.status} ${response.statusText} - ${errorText}`);
        }
        // Le contrôleur renvoie un booléen, fetch.json() le parse
        return response.json();
    },

    // --- STATION SERVICE ---
    getStations: async () => {
        const response = await fetch(`${API_BASE_URL}/stations`);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to fetch stations: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    },
    getStationById: async (id) => {
        const response = await fetch(`${API_BASE_URL}/stations/${id}`);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to fetch station ${id}: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    },
    addStation: async (station) => {
        // Les noms des champs dans 'station' doivent correspondre à l'entité Java
        const response = await fetch(`${API_BASE_URL}/stations`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(station),
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to add station: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    },
    removeStation: async (id) => {
        const response = await fetch(`${API_BASE_URL}/stations/${id}`, {
            method: 'DELETE',
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to remove station ${id}: ${response.status} ${response.statusText} - ${errorText}`);
        }
        // Le contrôleur renvoie un booléen
        return response.json();
    },
    // Endpoints StationService pour la gestion des véhicules dans les stations
    removeVehicleFromStation: async (stationId, vehicleId) => {
        const response = await fetch(`${API_BASE_URL}/stations/${stationId}/vehicles/${vehicleId}/remove`, {
            method: 'POST',
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to remove vehicle ${vehicleId} from station ${stationId}: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    },
    addVehicleToStation: async (stationId, vehicleId) => {
        const response = await fetch(`${API_BASE_URL}/stations/${stationId}/vehicles/${vehicleId}/add`, {
            method: 'POST',
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to add vehicle ${vehicleId} to station ${stationId}: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    },
    findStationWithVehicle: async (vehicleId) => {
        const response = await fetch(`${API_BASE_URL}/stations/vehicles/${vehicleId}/station`);
        if (!response.ok) {
            // Si non trouvé (404), le véhicule pourrait être en location ou maintenance, ce n'est pas toujours une erreur
            if (response.status === 404) return null;
            const errorText = await response.text();
            throw new Error(`Failed to find station for vehicle ${vehicleId}: ${response.status} ${response.statusText} - ${errorText}`);
        }
        return response.json();
    }
};

export default apiService;