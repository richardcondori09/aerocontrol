const API_RESERVAS = '/Aero/api/reservas';
const API_VUELOS = '/Aero/api/vuelos';
const API_PASAJEROS = '/Aero/api/pasajeros';
const API_AVIONES = '/Aero/api/aviones'; // Vital para conocer la capacidad

let vuelosData = [];
let pasajerosData = [];
let avionesData = [];
let reservasData = [];

document.addEventListener('DOMContentLoaded', () => {
    cargarDatosGlobales();
});

// ==================================================
// 1. CARGA MASIVA (JOIN VIRTUAL)
// ==================================================
function cargarDatosGlobales() {
    document.getElementById('tabla-reservas').innerHTML = '<tr><td colspan="6" class="has-text-centered"><i class="fa-solid fa-spinner fa-spin has-text-info"></i> Sincronizando Sistema de Reservas...</td></tr>';
    
    Promise.all([
        fetch(API_RESERVAS).then(r => r.json()),
        fetch(API_VUELOS).then(r => r.json()),
        fetch(API_PASAJEROS).then(r => r.json()),
        fetch(API_AVIONES).then(r => r.json())
    ]).then(([reservas, vuelos, pasajeros, aviones]) => {
        reservasData = reservas;
        vuelosData = vuelos;
        pasajerosData = pasajeros;
        avionesData = aviones;

        renderizarTabla();
        llenarSelectores();
    }).catch(err => {
        console.error("Error sincronizando DB:", err);
        document.getElementById('tabla-reservas').innerHTML = '<tr><td colspan="6" class="has-text-centered has-text-danger">Error conectando con el servidor.</td></tr>';
    });
}

// ==================================================
// 2. RENDERIZADO DE TABLA (MOSTRANDO NOMBRES REALES)
// ==================================================
function renderizarTabla() {
    const tbody = document.getElementById('tabla-reservas');
    tbody.innerHTML = ''; 

    if (reservasData.length === 0) return tbody.innerHTML = '<tr><td colspan="6" class="has-text-centered">No hay tickets emitidos.</td></tr>';

    reservasData.forEach(reserva => {
        // Cruce: Reserva -> Vuelo
        const vuelo = vuelosData.find(v => v.idVuelo === reserva.idVuelo);
        const textoVuelo = vuelo ? `V-${vuelo.idVuelo} (${vuelo.origen} ➔ ${vuelo.destino})` : `Vuelo ${reserva.idVuelo}`;
        
        // Cruce: Reserva -> Pasajero
        const pasajero = pasajerosData.find(p => p.idPasajero === reserva.idPasajero);
        const textoPasajero = pasajero ? `<i class="fa-solid fa-user has-text-grey mr-2"></i>${pasajero.nombre}` : `Pasajero ${reserva.idPasajero}`;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="is-vcentered has-text-weight-bold">TK-${reserva.idReserva}</td>
            <td class="is-vcentered has-text-info">${textoVuelo}</td>
            <td class="is-vcentered">${textoPasajero}</td>
            <td class="is-vcentered"><span class="tag is-dark is-medium"><i class="fa-solid fa-chair mr-1"></i> ${reserva.asiento}</span></td>
            <td class="is-vcentered is-size-7"><i class="fa-regular fa-calendar mr-1"></i> ${reserva.fechaReserva}</td>
            <td class="has-text-centered is-vcentered">
                <button class="button is-small is-danger is-light" onclick="eliminarReserva(${reserva.idReserva})" title="Anular Reserva">
                    <i class="fa-solid fa-ban"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// ==================================================
// 3. LOGICA DEL MODAL DE VENTA
// ==================================================
function llenarSelectores() {
    const selectVuelo = document.getElementById('selectVueloRes');
    selectVuelo.innerHTML = '<option value="" disabled selected>-- Seleccione Vuelo --</option>';
    vuelosData.filter(v => v.estado === 'PROGRAMADO').forEach(v => {
        selectVuelo.innerHTML += `<option value="${v.idVuelo}">Vuelo ${v.idVuelo}: ${v.origen} ➔ ${v.destino}</option>`;
    });

    const selectPax = document.getElementById('selectPasajeroRes');
    selectPax.innerHTML = '<option value="" disabled selected>-- Seleccione Pasajero --</option>';
    pasajerosData.forEach(p => {
        selectPax.innerHTML += `<option value="${p.idPasajero}">${p.pasaporte} - ${p.nombre}</option>`;
    });
}

function abrirModalReserva() { 
    document.getElementById('formReserva').reset(); 
    document.getElementById('inputAsiento').value = '';
    document.getElementById('mapaAsientosContainer').innerHTML = '<div class="has-text-centered has-text-grey-light mt-5"><i class="fa-solid fa-plane fa-3x mb-3"></i><br>Seleccione un vuelo para ver la disponibilidad.</div>';
    document.getElementById('modalReserva').classList.add('is-active'); 
}

function cerrarModalReserva() { document.getElementById('modalReserva').classList.remove('is-active'); }

// ==================================================
// 4. EL CEREBRO DEL MAPA DE ASIENTOS
// ==================================================
function renderizarAvion() {
    const idVuelo = parseInt(document.getElementById('selectVueloRes').value);
    const container = document.getElementById('mapaAsientosContainer');
    document.getElementById('inputAsiento').value = ''; 

    if (!idVuelo) return;

    // MAGIA DE CRUCE: Vuelo -> Avión -> Capacidad
    const vueloSeleccionado = vuelosData.find(v => v.idVuelo === idVuelo);
    if (!vueloSeleccionado) return;

    const avion = avionesData.find(a => a.idAvion === vueloSeleccionado.idAvion);
    if (!avion) {
        container.innerHTML = `<p class="has-text-danger">Error crítico: El vuelo V-${idVuelo} no tiene un avión válido en la BD.</p>`;
        return;
    }

    const capacidad = avion.capacidad;

    // Buscamos asientos ocupados para pintar de rojo
    const asientosOcupados = reservasData.filter(r => r.idVuelo === idVuelo).map(r => r.asiento);

    // Dibujamos el mapa dinámico
    let html = `<div class="plane-fuselage">`;
    let seatCount = 0;
    const letras = ['A', 'B', 'C', 'D', 'E', 'F'];
    const totalFilas = Math.ceil(capacidad / 6);

    for (let fila = 1; fila <= totalFilas; fila++) {
        html += `<div class="seat-row">`;
        for (let i = 0; i < 6; i++) {
            if (seatCount >= capacidad) break; 
            
            if (i === 3) html += `<div class="aisle"></div>`; 

            let idAsiento = `${fila}${letras[i]}`;
            let esOcupado = asientosOcupados.includes(idAsiento);
            let colorClase = esOcupado ? 'is-danger' : 'is-available';
            let onclickAttr = esOcupado ? '' : `onclick="seleccionarAsiento('${idAsiento}')"`;

            html += `<div class="seat ${colorClase}" id="seat-${idAsiento}" ${onclickAttr}>${idAsiento}</div>`;
            seatCount++;
        }
        html += `</div>`;
    }
    html += `</div>`;
    
    container.innerHTML = html;
}

function seleccionarAsiento(idAsiento) {
    const prevSelect = document.querySelector('.seat.is-selected');
    if (prevSelect) prevSelect.classList.remove('is-selected');

    document.getElementById(`seat-${idAsiento}`).classList.add('is-selected');
    document.getElementById('inputAsiento').value = idAsiento;
}

// ==================================================
// 5. GUARDAR Y ELIMINAR TICKET
// ==================================================
function guardarReserva() {
    const data = {
        idVuelo: parseInt(document.getElementById('selectVueloRes').value),
        idPasajero: parseInt(document.getElementById('selectPasajeroRes').value),
        asiento: document.getElementById('inputAsiento').value
    };

    if(!data.idVuelo || !data.idPasajero || !data.asiento) {
        alert("Seleccione un vuelo, un pasajero y un asiento verde en el mapa.");
        return;
    }

    fetch(API_RESERVAS, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(res => {
        if (res.ok) { 
            cerrarModalReserva(); 
            cargarDatosGlobales(); // Refresca para actualizar asientos rojos
            alert("¡Ticket vendido con éxito! El PDF del Boarding Pass se ha generado en tu carpeta local (AeroDatos/Tickets).");
        } else {
            alert("Error al procesar la venta. Verifique que el asiento no haya sido tomado.");
        }
    });
}

function eliminarReserva(id) {
    if(confirm("¿Seguro que desea anular este ticket? (El asiento volverá a ser verde)")) {
        fetch(`${API_RESERVAS}/${id}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) cargarDatosGlobales();
            else alert("Error al anular.");
        });
    }
}