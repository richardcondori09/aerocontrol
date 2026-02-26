// Función para listar aviones y pintarlos en la tabla de flota.html
function cargarFlota() {
    // Fíjate en la ruta /Aero/api/aviones
    fetch('/Aero/api/aviones')
        .then(response => response.json())
        .then(data => {
            console.log("Aviones recibidos:", data);
            // Aquí iría tu lógica para pintar los datos en el DOM (ej. document.getElementById...)
        })
        .catch(error => console.error('Error:', error));
}

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', cargarFlota);