/**
 * 
 */
const API_VUELOS = '/Aero/api/vuelos';
const API_AVIONES = '/Aero/api/aviones';
const API_TRIP = '/Aero/api/tripulacion';

const CIUDADES = [
    { id: "LIMA", nombre: "Lima" },
    { id: "BOGOTA", nombre: "Bogotá" },
    { id: "SANTIAGO", nombre: "Santiago" },
    { id: "RIO", nombre: "Río de Janeiro" },
    { id: "CUSCO", nombre: "Cusco" },
    { id: "QUITO", nombre: "Quito" },
    { id: "BUENOS AIRES", nombre: "Buenos Aires" },
    { id: "LA PAZ", nombre: "La Paz" },
    { id: "BRASILIA", nombre: "Brasilia" },
    { id: "SAO PAULO", nombre: "São Paulo" },
    { id: "MONTEVIDEO", nombre: "Montevideo" },
    { id: "BARRANQUILLA", nombre: "Barranquilla" },
    { id: "CARACAS", nombre: "Caracas" },
    { id: "ASUNCION", nombre: "Asunción" }
];

document.addEventListener('DOMContentLoaded', () => {
    cargarVuelos();
    cargarSelects();
    configurarLogicaRutas();
});

function configurarLogicaRutas() {
    const selOrigen = document.getElementById('selectOrigen');
    const selDestino = document.getElementById('selectDestino');

    selOrigen.innerHTML = '<option value="" disabled selected>-- Seleccione Origen --</option>';
    CIUDADES.forEach(c => {
        selOrigen.innerHTML += `<option value="${c.id}">${c.nombre}</option>`;
    });

    selOrigen.addEventListener('change', (e) => {
        const origen = e.target.value;
        selDestino.innerHTML = '';

        if (!origen) return;

        if (origen === 'LIMA') {
            selDestino.disabled = false;
            selDestino.innerHTML = '<option value="" disabled selected>-- Seleccione Destino --</option>';
            CIUDADES.filter(c => c.id !== 'LIMA').forEach(c => {
                selDestino.innerHTML += `<option value="${c.id}">${c.nombre}</option>`;
            });
        } else {
            selDestino.innerHTML = `<option value="LIMA" selected>Lima</option>`;
            selDestino.disabled = true;
        }
    });
}

function cargarVuelos() {
    fetch(API_VUELOS)
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('tabla-vuelos');
            tbody.innerHTML = ''; 
            if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="5" class="has-text-centered">No hay vuelos registrados.</td></tr>';

            data.forEach(vuelo => {
                let colorEstado = 'is-success'; 
                if (vuelo.estado === 'PROGRAMADO') colorEstado = 'is-warning';
                if (vuelo.estado === 'ATERRIZADO') colorEstado = 'is-info';
                if (vuelo.estado === 'CANCELADO') colorEstado = 'is-danger';

                let fechaFormateada = vuelo.fechaSalida.replace("T", " ");

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="is-vcentered has-text-weight-bold">V-${vuelo.idVuelo}</td>
                    <td class="is-vcentered">${vuelo.origen} <i class="fa-solid fa-arrow-right has-text-grey mx-2"></i> ${vuelo.destino}</td>
                    <td class="is-vcentered"><i class="fa-regular fa-clock mr-2"></i>${fechaFormateada}</td>
                    <td class="is-vcentered"><span class="tag ${colorEstado} is-light">${vuelo.estado}</span></td>
                    <td class="has-text-centered is-vcentered">
                        <button class="button is-small is-success is-light mr-1" onclick="despegarVuelo(${vuelo.idVuelo})" title="Autorizar Despegue" ${vuelo.estado !== 'PROGRAMADO' ? 'disabled' : ''}>
                            <i class="fa-solid fa-plane-up"></i>
                        </button>
                        <button class="button is-small is-warning is-light mr-1" onclick="exportarXML(${vuelo.idVuelo})" title="Exportar Manifiesto XML">
                            <i class="fa-solid fa-file-code"></i>
                        </button>
                        <button class="button is-small is-info is-light mr-1" onclick="editarVuelo(${vuelo.idVuelo})" title="Editar"><i class="fa-solid fa-pen"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        });
}

function cargarSelects() {
    const p1 = fetch(API_AVIONES).then(res => res.json()).then(aviones => {
        const select = document.getElementById('selectAvion');
        select.innerHTML = '<option value="" disabled selected>-- Seleccione Avión --</option>';
        aviones.filter(a => a.estado === 'ACTIVO').forEach(a => {
            select.innerHTML += `<option value="${a.idAvion}">${a.modelo} (${a.capacidad} pax)</option>`;
        });
    });

    const p2 = fetch(API_TRIP).then(res => res.json()).then(trip => {
		
        const pil = document.getElementById('selectPiloto');
        const cop = document.getElementById('selectCopiloto');
        const aza = document.getElementById('selectAzafata');
        
        pil.innerHTML = '<option value="" disabled selected>-- Piloto --</option>';
        cop.innerHTML = '<option value="" disabled selected>-- Copiloto --</option>';
        aza.innerHTML = '<option value="" disabled selected>-- Azafata --</option>';

        trip.filter(t => t.activo === true).forEach(t => {
            if (t.rol === 'PILOTO') pil.innerHTML += `<option value="${t.idTripulante}">${t.nombre}</option>`;
            if (t.rol === 'COPILOTO') cop.innerHTML += `<option value="${t.idTripulante}">${t.nombre}</option>`;
            if (t.rol === 'AZAFATA') aza.innerHTML += `<option value="${t.idTripulante}">${t.nombre}</option>`;
        });
    });
    
    return Promise.all([p1, p2]);
}

function guardarVuelo() {
    const origenForm = document.getElementById('selectOrigen').value;
    const destinoForm = document.getElementById('selectDestino').disabled ? 'LIMA' : document.getElementById('selectDestino').value;
    
    // Al registrar, forzamos 'PROGRAMADO' si el select está deshabilitado
    const estadoForm = document.getElementById('selectEstadoVuelo').disabled ? 'PROGRAMADO' : document.getElementById('selectEstadoVuelo').value;

    const data = {
        origen: origenForm,
        destino: destinoForm,
        fechaSalida: document.getElementById('inputFecha').value,
        idAvion: document.getElementById('selectAvion').value,
        idPiloto: document.getElementById('selectPiloto').value,
        idCopiloto: document.getElementById('selectCopiloto').value,
        idAzafata: document.getElementById('selectAzafata').value,
        estado: estadoForm
    };
    
    if(!data.origen || !data.destino || !data.fechaSalida || !data.idAvion || !data.idPiloto || !data.idCopiloto || !data.idAzafata) {
        alert("Por favor complete todos los campos de programación.");
        return;
    }

    const id = document.getElementById('inputIdVuelo').value;
    const method = id ? 'PUT' : 'POST';
    const fetchUrl = id ? `${API_VUELOS}/${id}` : API_VUELOS;

    fetch(fetchUrl, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(res => {
        if (res.ok) { cerrarModalVuelo(); cargarVuelos(); }
        else alert("Error al guardar el vuelo.");
    });
}

function despegarVuelo(id) {
    if(confirm("¿Autorizar despegue? El vuelo pasará a EN_VUELO y se iniciará la simulación.")) {
        fetch(`${API_VUELOS}/${id}/despegar`, { method: 'PUT' })
        .then(res => {
            if (res.ok) {
                alert("Simulación de vuelo iniciada.");
                cargarVuelos();
            } else {
                alert("Error en torre de control al iniciar el despegue.");
            }
        });
    }
}

function exportarXML(id) {
    window.open(`/Aero/api/exportar/manifiesto/${id}`, '_blank');
}

function editarVuelo(id) {
    // Primero cargamos los selects, LUEGO asignamos los valores
    cargarSelects().then(() => {
        fetch(`${API_VUELOS}/${id}`).then(res => res.json()).then(v => {
            document.getElementById('inputIdVuelo').value = v.idVuelo;
            
            const selOrigen = document.getElementById('selectOrigen');
            selOrigen.value = v.origen;
            selOrigen.dispatchEvent(new Event('change'));
            
            document.getElementById('selectDestino').value = v.destino;
            
            // Format datetime-local requires YYYY-MM-DDThh:mm
            let formattedDate = v.fechaSalida.replace(" ", "T");
            // If the date string has seconds, we might need to truncate them for datetime-local input
            if (formattedDate.length > 16) {
                 formattedDate = formattedDate.substring(0, 16);
            }
            document.getElementById('inputFecha').value = formattedDate;           
            document.getElementById('selectAvion').value = v.idAvion;
            document.getElementById('selectPiloto').value = v.idPiloto || "";
            document.getElementById('selectCopiloto').value = v.idCopiloto || "";
            document.getElementById('selectAzafata').value = v.idAzafata || "";
            
            // Lógica del estado en edición
            const selEstado = document.getElementById('selectEstadoVuelo');
            selEstado.value = v.estado;
            if (v.estado === 'PROGRAMADO') {
                 // Permitir cambiar a CANCELADO si está PROGRAMADO
                 selEstado.disabled = false;
                 // Ocultar opciones de vuelo en curso
                 Array.from(selEstado.options).forEach(opt => {
                     if (opt.value === 'EN_VUELO' || opt.value === 'ATERRIZADO') {
                         opt.disabled = true;
                     } else {
                         opt.disabled = false;
                     }
                 });
            } else {
                 // Si ya despegó, no se puede cambiar el estado desde la UI
                 selEstado.disabled = true;
            }
            
            document.getElementById('divRutaVuelo').style.display = 'none';
            document.getElementById('divAsignarAvion').style.display = 'none';

            document.getElementById('modal-vuelo-titulo').textContent = "Modificar Vuelo " + v.idVuelo;
            document.getElementById('modalVuelo').classList.add('is-active');
        });
    });
}

function abrirModalVuelo() { 
    document.getElementById('formVuelo').reset(); 
    document.getElementById('inputIdVuelo').value = '';
    document.getElementById('selectDestino').innerHTML = '<option value="" disabled selected>-- Seleccione Origen primero --</option>';
    document.getElementById('selectDestino').disabled = false;
    
    // Forzar estado PROGRAMADO para nuevos vuelos
    const selEstado = document.getElementById('selectEstadoVuelo');
    selEstado.value = 'PROGRAMADO';
    selEstado.disabled = true;
    
    document.getElementById('divRutaVuelo').style.display = 'flex'; 
    document.getElementById('divAsignarAvion').style.display = 'block';

    cargarSelects().then(() => {
        document.getElementById('modal-vuelo-titulo').textContent = "Programar Nuevo Vuelo"; 
        document.getElementById('modalVuelo').classList.add('is-active'); 
    });
}

function cerrarModalVuelo() { 
	document.getElementById('modalVuelo').classList.remove('is-active'); 
}
