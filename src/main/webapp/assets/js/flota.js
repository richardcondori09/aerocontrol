/**
 * 
 */
const API_URL = '/Aero/api/aviones';

// Cargar la tabla al iniciar la página
document.addEventListener('DOMContentLoaded', cargarAviones);

// ==========================================
// 1. LEER (GET)
// ==========================================
function cargarAviones() {
    fetch(API_URL)
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('tabla-aviones');
            tbody.innerHTML = ''; // Limpiar tabla

            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="has-text-centered">No hay aviones registrados.</td></tr>';
                return;
            }

            data.forEach(avion => {
                // Darle color al estado usando las etiquetas (tags) de Bulma
                let colorEstado = 'is-success'; // ACTIVO por defecto
                if (avion.estado === 'MANTENIMIENTO') colorEstado = 'is-warning';
                if (avion.estado === 'BAJA') colorEstado = 'is-danger';

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="is-vcentered"><strong>${avion.idAvion}</strong></td>
                    <td class="is-vcentered">${avion.modelo}</td>
                    <td class="is-vcentered">${avion.capacidad} pax</td>
                    <td class="is-vcentered"><span class="tag ${colorEstado} is-light">${avion.estado}</span></td>
                    <td class="has-text-centered is-vcentered">
                        <button class="button is-small is-info is-light mr-1" onclick="editarAvion(${avion.idAvion})" title="Editar">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="button is-small is-danger is-light" onclick="eliminarAvion(${avion.idAvion})" title="Dar de Baja">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('tabla-aviones').innerHTML = '<tr><td colspan="5" class="has-text-centered has-text-danger">Error al cargar la base de datos.</td></tr>';
        });
}

// ==========================================
// 2. CREAR Y ACTUALIZAR (POST / PUT)
// ==========================================
function guardarAvion() {
    const id = document.getElementById('inputIdAvion').value;
    const modelo = document.getElementById('inputModelo').value;
    const capacidad = document.getElementById('inputCapacidad').value;
    const estado = document.getElementById('selectEstado').value;

    // Validación básica
    if (!modelo || !capacidad) {
        alert("Por favor, complete todos los campos.");
        return;
    }

    const avionData = {
        modelo: modelo,
        capacidad: parseInt(capacidad),
        estado: estado
    };

    // Si hay un ID en el formulario oculto, es un PUT (Actualizar), si no, es un POST (Crear)
    const method = id ? 'PUT' : 'POST';
    const fetchUrl = id ? `${API_URL}/${id}` : API_URL;

    fetch(fetchUrl, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(avionData)
    })
    .then(response => {
        if (response.ok) {
            cerrarModal();
            cargarAviones(); // Recargar la tabla
        } else {
            alert("Error al guardar el avión en el servidor.");
        }
    })
    .catch(error => console.error('Error:', error));
}

// ==========================================
// 3. EDITAR (GET by ID para llenar formulario)
// ==========================================
function editarAvion(id) {
    fetch(`${API_URL}/${id}`)
        .then(response => response.json())
        .then(avion => {
            document.getElementById('inputIdAvion').value = avion.idAvion;
            document.getElementById('inputModelo').value = avion.modelo;
            document.getElementById('inputCapacidad').value = avion.capacidad;
            document.getElementById('selectEstado').value = avion.estado;
            
            document.getElementById('modal-titulo').textContent = "Editar Avión N° " + avion.idAvion;
            
            // Mostrar modal
            document.getElementById('modalAvion').classList.add('is-active');
        })
        .catch(error => console.error('Error:', error));
}

// ==========================================
// 4. ELIMINAR (DELETE)
// ==========================================
function eliminarAvion(id) {
    if (confirm("¿Está seguro de que desea dar de baja este avión? (Eliminación lógica)")) {
        fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                cargarAviones(); // Recargar la tabla para ver el cambio de estado
            } else {
                alert("No se pudo dar de baja el avión.");
            }
        })
        .catch(error => console.error('Error:', error));
    }
}

// ==========================================
// UTILIDADES DEL MODAL BULMA
// ==========================================
function abrirModal() {
    // Limpiar formulario antes de abrir para un "Nuevo" registro
    document.getElementById('formAvion').reset();
    document.getElementById('inputIdAvion').value = '';
    document.getElementById('modal-titulo').textContent = "Registrar Nuevo Avión";
    
    document.getElementById('modalAvion').classList.add('is-active');
}

function cerrarModal() {
    document.getElementById('modalAvion').classList.remove('is-active');
}