/**
 * 
 */
const API_URL = '/Aero/api/tripulacion';

// Cargar la tabla al iniciar la página
document.addEventListener('DOMContentLoaded', cargarTripulacion);

// ==========================================
// 1. LEER (GET)
// ==========================================
function cargarTripulacion() {
    fetch(API_URL)
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('tabla-tripulacion');
            tbody.innerHTML = ''; 

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="has-text-centered">No hay tripulantes registrados.</td></tr>';
                return;
            }

            data.forEach(trip => {
                // Color para el estado
                let colorEstado = trip.activo === true ? 'is-success' : 'is-danger'; 
                let textoEstado = trip.activo === true ? 'ACTIVO' : 'DE BAJA';

                let iconoRol = 'fa-user-tie';
                if (trip.rol === 'PILOTO') iconoRol = 'fa-user-astronaut';
                if (trip.rol === 'AZAFATA') iconoRol = 'fa-user-nurse';

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="is-vcentered"><strong>${trip.idTripulante}</strong></td>
                    <td class="is-vcentered"><span class="has-text-grey is-size-7">${trip.licencia || 'S/N'}</span><br>${trip.nombre}</td>
                    <td class="is-vcentered"><i class="fa-solid ${iconoRol} has-text-grey mr-2"></i> ${trip.rol}</td>
                    <td class="is-vcentered"><span class="tag ${colorEstado} is-light">${textoEstado}</span></td>
                    <td class="has-text-centered is-vcentered">
                        <button class="button is-small is-info is-light mr-1" onclick="editarTripulante(${trip.idTripulante})" title="Editar">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="button is-small is-danger is-light" onclick="eliminarTripulante(${trip.idTripulante})" title="Dar de Baja">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('tabla-tripulacion').innerHTML = '<tr><td colspan="5" class="has-text-centered has-text-danger">Error al cargar la base de datos.</td></tr>';
        });
}

// ==========================================
// 2. CREAR Y ACTUALIZAR (POST / PUT)
// ==========================================
function guardarTripulante() {
    const id = document.getElementById('inputIdTripulante').value;
    const licencia = document.getElementById('inputLicencia').value;
    const nombre = document.getElementById('inputNombre').value;
    const rol = document.getElementById('selectRol').value;
    const estado = document.getElementById('selectEstado').value === 'true';

    if (!nombre) {
        alert("Por favor, ingrese el nombre del tripulante.");
        return;
    }

    const tripulanteData = {
		licencia: licencia,
        nombre: nombre,
        rol: rol,
        activo: estado
    };

    const method = id ? 'PUT' : 'POST';
    const fetchUrl = id ? `${API_URL}/${id}` : API_URL;

    fetch(fetchUrl, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(tripulanteData)
    })
    .then(response => {
        if (response.ok) {
            cerrarModal();
            cargarTripulacion(); 
        } else {
            alert("Error al guardar en el servidor.");
        }
    })
    .catch(error => console.error('Error:', error));
}

// ==========================================
// 3. EDITAR (GET by ID para llenar formulario)
// ==========================================
function editarTripulante(id) {
    fetch(`${API_URL}/${id}`)
        .then(response => response.json())
        .then(trip => {
            document.getElementById('inputIdTripulante').value = trip.idTripulante;
            document.getElementById('inputLicencia').value = trip.licencia;
            document.getElementById('inputNombre').value = trip.nombre;
            document.getElementById('selectRol').value = trip.rol;
            document.getElementById('selectEstado').value = trip.activo ? 'true' : 'false';
            
            document.getElementById('modal-titulo').textContent = "Editar Tripulante N° " + trip.idTripulante;
            document.getElementById('modalTripulacion').classList.add('is-active');
        })
        .catch(error => console.error('Error:', error));
}

// ==========================================
// 4. ELIMINAR (DELETE Lógico)
// ==========================================
function eliminarTripulante(id) {
    if (confirm("¿Está seguro de que desea dar de baja a este tripulante?")) {
        fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                cargarTripulacion(); 
            } else {
                alert("No se pudo dar de baja al tripulante.");
            }
        })
        .catch(error => console.error('Error:', error));
    }
}

// ==========================================
// UTILIDADES DEL MODAL BULMA
// ==========================================
function abrirModal() {
    document.getElementById('formTripulacion').reset();
    document.getElementById('inputIdTripulante').value = '';
    document.getElementById('modal-titulo').textContent = "Registrar Nuevo Tripulante";
    
    document.getElementById('modalTripulacion').classList.add('is-active');
}

function cerrarModal() {
    document.getElementById('modalTripulacion').classList.remove('is-active');
}