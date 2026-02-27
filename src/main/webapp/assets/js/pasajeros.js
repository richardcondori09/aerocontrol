/**
 * 
 */
const API_URL = '/Aero/api/pasajeros';

// Cargar la tabla al iniciar la página
document.addEventListener('DOMContentLoaded', cargarPasajeros);

// ==========================================
// 1. LEER (GET)
// ==========================================
function cargarPasajeros() {
    fetch(API_URL)
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('tabla-pasajeros');
            tbody.innerHTML = ''; 

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="has-text-centered">No hay pasajeros registrados.</td></tr>';
                return;
            }

            data.forEach(pasajero => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="is-vcentered"><strong>${pasajero.idPasajero || pasajero.id}</strong></td>
                    <td class="is-vcentered"><i class="fa-solid fa-passport has-text-grey-light mr-2"></i> ${pasajero.pasaporte}</td>
                    <td class="is-vcentered">${pasajero.nombre}</td>
                    <td class="is-vcentered"><a href="mailto:${pasajero.email}">${pasajero.email}</a></td>
                    <td class="has-text-centered is-vcentered">
                        <button class="button is-small is-info is-light mr-1" onclick="editarPasajero(${pasajero.idPasajero || pasajero.id})" title="Editar">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="button is-small is-danger is-light" onclick="eliminarPasajero(${pasajero.idPasajero || pasajero.id})" title="Eliminar">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('tabla-pasajeros').innerHTML = '<tr><td colspan="5" class="has-text-centered has-text-danger">Error al cargar la base de datos.</td></tr>';
        });
}

// ==========================================
// 2. CREAR Y ACTUALIZAR (POST / PUT)
// ==========================================
function guardarPasajero() {
    const id = document.getElementById('inputIdPasajero').value;
    const pasaporte = document.getElementById('inputPasaporte').value;
    const nombre = document.getElementById('inputNombre').value;
    const email = document.getElementById('inputEmail').value;

    if (!pasaporte || !nombre || !email) {
        alert("Por favor, complete todos los campos.");
        return;
    }

    const pasajeroData = {
        pasaporte: pasaporte,
        nombre: nombre,
        email: email
    };

    const method = id ? 'PUT' : 'POST';
    const fetchUrl = id ? `${API_URL}/${id}` : API_URL;

    fetch(fetchUrl, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(pasajeroData)
    })
    .then(response => {
        if (response.ok) {
            cerrarModal();
            cargarPasajeros(); 
        } else {
            alert("Error al guardar en el servidor.");
        }
    })
    .catch(error => console.error('Error:', error));
}

// ==========================================
// 3. EDITAR (GET by ID para llenar formulario)
// ==========================================
function editarPasajero(id) {
    fetch(`${API_URL}/${id}`)
        .then(response => response.json())
        .then(pasajero => {
            document.getElementById('inputIdPasajero').value = pasajero.idPasajero || pasajero.id;
            document.getElementById('inputPasaporte').value = pasajero.pasaporte;
            document.getElementById('inputNombre').value = pasajero.nombre;
            document.getElementById('inputEmail').value = pasajero.email;
            
            document.getElementById('modal-titulo').textContent = "Editar Pasajero";
            document.getElementById('modalPasajero').classList.add('is-active');
        })
        .catch(error => console.error('Error:', error));
}

// ==========================================
// 4. ELIMINAR (DELETE)
// ==========================================
function eliminarPasajero(id) {
    if (confirm("¿Está seguro de que desea eliminar a este pasajero del sistema?")) {
        fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                cargarPasajeros(); 
            } else {
                alert("No se pudo eliminar al pasajero.");
            }
        })
        .catch(error => console.error('Error:', error));
    }
}

// ==========================================
// UTILIDADES DEL MODAL BULMA
// ==========================================
function abrirModal() {
    document.getElementById('formPasajero').reset();
    document.getElementById('inputIdPasajero').value = '';
    document.getElementById('modal-titulo').textContent = "Registrar Nuevo Pasajero";
    
    document.getElementById('modalPasajero').classList.add('is-active');
}

function cerrarModal() {
    document.getElementById('modalPasajero').classList.remove('is-active');
}