let intervaloRadar = null;
let vueloActual = null;

const LIMA_LAT = -12.0464;
const LIMA_LON = -77.0428;
const SVG_SIZE = 1000;
const CENTER = 500;
const SCALE = 11.5; 

const ciudades = [
    { nombre: "Bogotá", lat: 4.7110, lon: -74.0721 },
    { nombre: "Quito", lat: -0.1807, lon: -78.4678 },
    { nombre: "Cusco", lat: -13.5319, lon: -71.9675 },
    { nombre: "Santiago", lat: -33.4489, lon: -70.6693 },
    { nombre: "Río de Janeiro", lat: -21.9068, lon: -43.1729 },
    { nombre: "Buenos Aires", lat: -34.6037, lon: -58.3816 },
    { nombre: "La Paz", lat: -16.4897, lon: -68.1193 },
    { nombre: "Brasilia", lat: -15.8267, lon: -47.9218 },
    { nombre: "Sao Paulo", lat: -23.5505, lon: -46.6333 },
    { nombre: "Montevideo", lat: -35.9011, lon: -56.1645 },
    { nombre: "Barranquilla", lat: 10.9685, lon: -74.7813 },
    { nombre: "Caracas", lat: 10.4806, lon: -66.9036 },
    { nombre: "Asunción", lat: -25.2637, lon: -57.5759 }
];

document.addEventListener('DOMContentLoaded', () => {
    cargarVuelosActivos();
    dibujarCiudadesReferencia();
});

function gpsToSvg(lat, lon) {
    let x = CENTER + ((lon - LIMA_LON) * SCALE);
    let y = CENTER - ((lat - LIMA_LAT) * SCALE);
    return { x: x, y: y };
}

function dibujarCiudadesReferencia() {
    const grupo = document.getElementById('ciudades-ref');
    let html = '';
    ciudades.forEach(c => {
        let pos = gpsToSvg(c.lat, c.lon);
        html += `<circle cx="${pos.x}" cy="${pos.y}" r="4" fill="#4a7b85"/>`;
        html += `<text x="${pos.x + 8}" y="${pos.y + 4}">${c.nombre}</text>`;
    });
    grupo.innerHTML = html;
}

function cargarVuelosActivos() {
    const select = document.getElementById('selectVuelo');
    const btnRastrear = document.getElementById('btnRastrear');
    
    fetch('/Aero/api/vuelos')
        .then(res => res.json())
        .then(vuelos => {
            const vuelosActivos = vuelos.filter(v => v.estado === 'EN_VUELO' || v.estado === 'PROGRAMADO');
            if (vuelosActivos.length === 0) {
                select.innerHTML = '<option value="">No hay vuelos activos</option>';
                return;
            }
            select.innerHTML = '<option value="">-- Seleccione un Vuelo --</option>';
            vuelosActivos.forEach(v => {
                select.innerHTML += `<option value="${v.idVuelo}">Vuelo ${v.idVuelo}: ${v.origen} ➔ ${v.destino}</option>`;
            });
            btnRastrear.disabled = false;
        });
}

function iniciarRastreo() {
    vueloActual = document.getElementById('selectVuelo').value;
    if (!vueloActual) return;

    if (intervaloRadar) clearInterval(intervaloRadar);
    document.getElementById('ruta-vuelo').setAttribute('points', '');
    document.getElementById('avion-marcador').style.display = 'none';
    document.getElementById('sweep-anim').style.display = 'block';

    // UI de Barra de Estado -> Buscando
    const panelEstado = document.getElementById('panel-estado-global');
    panelEstado.className = "notification status-panel p-5 has-text-centered is-warning is-light";
    document.getElementById('estado-titulo').innerHTML = '<i class="fa-solid fa-satellite-dish fa-beat-fade"></i> ESTABLECIENDO CONEXIÓN';
    document.getElementById('estado-sub').innerHTML = 'Sincronizando con la caja negra...';

    cargarHistorial();
    intervaloRadar = setInterval(actualizarPosicionActual, 3000);
}

function cargarHistorial() {
    fetch(`/Aero/api/cajanegra/${vueloActual}/historial`)
        .then(res => res.ok ? res.json() : [])
        .then(data => {
            let pointsString = "";
            data.forEach(coord => {
                let svgPos = gpsToSvg(coord.latitud, coord.longitud);
                pointsString += `${svgPos.x},${svgPos.y} `;
            });
            document.getElementById('ruta-vuelo').setAttribute('points', pointsString.trim());
        });
}

// ESTA FUNCIÓN CONTIENE LOS CAMBIOS PRINCIPALES
function actualizarPosicionActual() {
    Promise.all([
        fetch(`/Aero/api/cajanegra/${vueloActual}`).then(r => r.ok ? r.json() : null),
        fetch(`/Aero/api/vuelos/${vueloActual}`).then(r => r.ok ? r.json() : null)
    ])
    .then(([coord, vuelo]) => {
        
        const panelEstado = document.getElementById('panel-estado-global');
        const tituloEstado = document.getElementById('estado-titulo');
        const subEstado = document.getElementById('estado-sub');

        // Lógica de Estados Visuales en el Panel Inferior
        if (vuelo && vuelo.estado === 'ATERRIZADO') {
            panelEstado.className = "notification status-panel p-5 has-text-centered is-info is-light";
            tituloEstado.innerHTML = '<i class="fa-solid fa-plane-arrival"></i> ATERRIZADO';
            subEstado.innerHTML = 'El vuelo ha llegado a su destino exitosamente.';
            document.getElementById('sweep-anim').style.display = 'none'; // Apagamos radar
            if (intervaloRadar) clearInterval(intervaloRadar); 
        } else if (vuelo && vuelo.estado === 'EN_VUELO') {
            panelEstado.className = "notification status-panel p-5 has-text-centered is-success is-light";
            tituloEstado.innerHTML = '<i class="fa-solid fa-plane"></i> EN VUELO';
            subEstado.innerHTML = `Vuelo ${vuelo.idVuelo}: ${vuelo.origen} ➔ ${vuelo.destino}`;
        }

        // Pintar Coordenadas
        if (coord) {
            document.getElementById('data-lat').textContent = coord.latitud.toFixed(4);
            document.getElementById('data-lon').textContent = coord.longitud.toFixed(4);
            document.getElementById('data-alt').textContent = coord.altitud.toFixed(0) + " ft";

            let posSvg = gpsToSvg(coord.latitud, coord.longitud);
            let avionG = document.getElementById('avion-marcador');
            
            avionG.style.display = 'block';
            avionG.style.transform = `translate(${posSvg.x}px, ${posSvg.y}px)`;

            let ruta = document.getElementById('ruta-vuelo');
            let points = ruta.getAttribute('points') || "";
            ruta.setAttribute('points', points + ` ${posSvg.x},${posSvg.y}`);
        }
    });
}