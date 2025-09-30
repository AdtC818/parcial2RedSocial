// --- Aplicación Principal ---
const dashboardApp = {
    currentSection: 'amigos',
    eventsData: [],
    categories: [],
    initialUserHobbies: [], // Almacenar hobbies iniciales para la UI
    ui: {
        showSection: function(section, event) {
            if (event && event.preventDefault) event.preventDefault();
            // Ocultar todas las secciones
            document.querySelectorAll('[id^="seccion-"]').forEach(el => el.style.display = 'none');
            // Mostrar la sección solicitada
            const targetId = 'seccion-' + section;
            const targetEl = document.getElementById(targetId);
            if (targetEl) targetEl.style.display = 'block';
            dashboardApp.currentSection = section;

            // Actualizar active en el menú
            document.querySelectorAll('.list-group-item').forEach(item => item.classList.remove('active'));
            if (event && event.currentTarget) event.currentTarget.classList.add('active');

            // Cargar datos específicos de la sección
            switch(section) {
                case 'amigos':
                    dashboardApp.eventos.loadEventsForFilter(); // Cargar eventos para el filtro de amigos
                    setTimeout(() => dashboardApp.amigos.loadData(), 100);
                    break;
                case 'hobbies':
                    dashboardApp.hobbies.loadAvailableHobbies();
                    dashboardApp.hobbies.updateHobbyListUI(); // Actualizar UI de hobbies
                    break;
                case 'eventos':
                    setTimeout(() => {
                        dashboardApp.eventos.loadCategories();
                        dashboardApp.eventos.loadData();
                    }, 100);
                    break;
                case 'buscar-amigos':
                    // Limpiar resultados al mostrar la sección
                    document.getElementById('resultados-busqueda').innerHTML = `<div class="alert alert-info">Escribe un nombre en la barra de búsqueda para encontrar usuarios.</div>`;
                    break;
            }
        },
        showLoading: function(section, show) {
            const loading = document.getElementById(`loading-${section}`);
            if (!loading) return;
            loading.classList.toggle('d-none', !show);
        },
        getInitials: function(name) {
            if (!name) return '';
            return name.split(' ').map(n => n[0] || '').join('').slice(0, 3).toUpperCase();
        }
    },
    amigos: {
        loadData: async function() {
            dashboardApp.ui.showLoading('amigos', true);
            try {
                const response = await fetch('/api/amigos');
                const data = await response.json();
                dashboardApp.ui.showLoading('amigos', false);

                if (data && data.success) {
                    dashboardApp.graphs.createFriendsGraph(data);
                    dashboardApp.amigos.updateFriendsInfo(data);
                } else {
                    const err = data && data.error ? data.error : 'Respuesta inválida del servidor';
                    document.getElementById('amigos-info').innerHTML = `<div class="alert alert-warning"><i class="fas fa-exclamation-triangle me-2"></i>Error: ${err}</div>`;
                }
            } catch (error) {
                dashboardApp.ui.showLoading('amigos', false);
                document.getElementById('amigos-info').innerHTML = `<div class="alert alert-danger"><i class="fas fa-times-circle me-2"></i>Error cargando datos: ${error}</div>`;
            }
        },
        updateFriendsInfo: function(data) {
            let info = `<h6><i class="fas fa-network-wired me-2"></i>Tu Red de Amigos en Neo4j:</h6>`;
            info += `<p><strong>Tienes ${Array.isArray(data.amigos) ? data.amigos.length : 0} amigo(s) conectado(s)</strong></p>`;
            if (Array.isArray(data.amigos) && data.amigos.length > 0) {
                info += `<div class="row">`;
                data.amigos.forEach(amigo => {
                    info += `<div class="col-md-6 mb-3">
                        <div class="friend-card card">
                            <div class="card-body">
                                <div class="d-flex align-items-center">
                                    <div class="friend-avatar me-3">
                                        ${dashboardApp.ui.getInitials(amigo.nombre)}
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <h6 class="mb-1">${amigo.nombre}</h6>
                                            <small class="text-muted">
                                                <i class="fas fa-envelope me-1"></i>
                                                ${amigo.email || ''}
                                            </small>
                                        </div>
                                        <button class="btn btn-outline-danger btn-sm" onclick="dashboardApp.amigos.remove(${amigo.id}, this)">
                                            <i class="fas fa-user-minus"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>`;
                });
                info += `</div>`;
                info += `<div class="alert alert-info mt-3">
                    <i class="fas fa-info-circle me-2"></i>
                    <strong>Leyenda:</strong>
                    <span style="color: var(--user-color-start);"><i class="fas fa-circle"></i></span> Tú &nbsp;&nbsp;
                    <span style="color: var(--friend-color-start);"><i class="fas fa-circle"></i></span> Tus amigos &nbsp;&nbsp;
                    <small>Los nodos se pueden arrastrar para reorganizar el grafo</small>
                </div>`;
            }
            document.getElementById('amigos-info').innerHTML = info;
        },
        remove: async function(amigoId, boton) {
            if (!confirm("¿Estás seguro de que quieres eliminar a este amigo?")) {
                return;
            }
            const card = boton.closest('.col-md-6');
            boton.disabled = true;
            try {
                const response = await fetch(`/api/amigos/eliminar/${amigoId}`, { method: 'POST' });
                const data = await response.json();
                if (data.success) {
                    if (card) {
                        card.style.transition = 'opacity 0.5s ease';
                        card.style.opacity = '0';
                        setTimeout(() => card.remove(), 500);
                    }
                    dashboardApp.amigos.loadData(); // Recargar grafo
                } else {
                    alert("Error al eliminar el amigo: " + data.error);
                    boton.disabled = false;
                }
            } catch (err) {
                console.error('Error:', err);
                alert("Ocurrió un error de red. Inténtalo de nuevo.");
                boton.disabled = false;
            }
        },
        filterByEvent: async function() {
            const eventoId = document.getElementById('filtro-eventos-amigos').value;
            if (!eventoId) {
                dashboardApp.amigos.loadData();
                return;
            }
            dashboardApp.ui.showLoading('amigos', true);
            try {
                const response = await fetch(`/api/amigos/filtrar-por-evento?eventoId=${eventoId}`);
                const data = await response.json();
                dashboardApp.ui.showLoading('amigos', false);

                if (data && data.success) {
                    dashboardApp.graphs.createFriendsGraph(data);
                    dashboardApp.amigos.updateFriendsInfo(data);
                } else {
                    document.getElementById('amigos-info').innerHTML = `<div class="alert alert-warning">Error al filtrar: ${data.error || 'Respuesta inválida'}</div>`;
                }
            } catch (error) {
                dashboardApp.ui.showLoading('amigos', false);
                console.error("Error al filtrar amigos:", error);
            }
        }
    },
    hobbies: {
        loadAvailableHobbies: async function() {
            try {
                const response = await fetch('/api/hobbies-disponibles');
                const data = await response.json();
                if (data && data.success) {
                    const select = document.getElementById('filtro-hobbies');
                    select.innerHTML = '<option value="">Todos los hobbies</option>';
                    if (Array.isArray(data.hobbies)) {
                        data.hobbies.forEach(hobby => {
                            const option = document.createElement('option');
                            option.value = hobby;
                            option.textContent = hobby;
                            select.appendChild(option);
                        });
                    }
                    // Aplicar filtro actual o mostrar todos
                    dashboardApp.hobbies.filterByHobby();
                }
            } catch (error) {
                console.error('Error cargando hobbies:', error);
            }
        },
        filterByHobby: async function() {
            dashboardApp.ui.showLoading('hobbies', true);
            const filtro = document.getElementById('filtro-hobbies').value;
            try {
                const response = await fetch(`/api/amigos-hobbies${filtro ? '?filtroHobby=' + encodeURIComponent(filtro) : ''}`);
                const data = await response.json();
                dashboardApp.ui.showLoading('hobbies', false);

                if (data && data.success) {
                    dashboardApp.graphs.createHobbiesGraph(data);
                    dashboardApp.hobbies.updateHobbiesInfo(data);
                } else {
                    const err = data && data.error ? data.error : 'Respuesta inválida del servidor';
                    document.getElementById('hobbies-info').innerHTML = `<div class="alert alert-warning"><i class="fas fa-exclamation-triangle me-2"></i>Error: ${err}</div>`;
                }
            } catch (error) {
                dashboardApp.ui.showLoading('hobbies', false);
                console.error('Error:', error);
                document.getElementById('hobbies-info').innerHTML = `<div class="alert alert-danger"><i class="fas fa-times-circle me-2"></i>Error cargando datos: ${error}</div>`;
            }
        },
        updateHobbiesInfo: function(data) {
            let info = `<h6><i class="fas fa-heart me-2"></i>Red de Amigos por Hobbies:</h6>`;
            if (data.filtroAplicado) {
                info += `<div class="alert alert-info py-2">
                    <i class="fas fa-filter me-2"></i>
                    <strong>Filtro aplicado:</strong> "${data.filtroAplicado}" - 
                    Mostrando ${Array.isArray(data.amigosConHobbies) ? data.amigosConHobbies.length : 0} amigo(s)
                </div>`;
            }
            info += `<p><strong>${Array.isArray(data.amigosConHobbies) ? data.amigosConHobbies.length : 0} amigo(s) ${data.filtroAplicado ? 'con este hobby' : 'total'}</strong></p>`;
            if (Array.isArray(data.amigosConHobbies) && data.amigosConHobbies.length > 0) {
                info += `<div class="row">`;
                data.amigosConHobbies.forEach(amigo => {
                    info += `<div class="col-md-6 mb-3">
                        <div class="friend-card card">
                            <div class="card-body">
                                <div class="d-flex align-items-start">
                                    <div class="friend-avatar me-3">
                                        ${dashboardApp.ui.getInitials(amigo.nombre)}
                                    </div>
                                    <div class="flex-grow-1">
                                        <h6 class="mb-2">${amigo.nombre}</h6>
                                        <div class="d-flex flex-wrap">`;
                    (amigo.hobbies || []).forEach(hobby => {
                        const isFiltered = data.filtroAplicado && hobby.toLowerCase().includes((data.filtroAplicado || '').toLowerCase());
                        info += `<span class="hobby-badge ${isFiltered ? 'bg-warning' : ''}">${hobby}</span>`;
                    });
                    info += `</div></div></div>
                            </div>
                        </div>
                    </div>`;
                });
                info += `</div>`;
                info += `<div class="alert alert-info mt-3">
                    <i class="fas fa-info-circle me-2"></i>
                    <strong>Leyenda:</strong> 
                    <span style="color: var(--user-color-start);"><i class="fas fa-circle"></i></span> Tú &nbsp;&nbsp;
                    <span style="color: var(--friend-color-start);"><i class="fas fa-circle"></i></span> Tus amigos &nbsp;&nbsp;
                    <span class="hobby-badge bg-warning">Hobby filtrado</span>
                </div>`;
            }
            document.getElementById('hobbies-info').innerHTML = info;
        },
        updateHobbyListUI: function() {
            // Actualiza la UI de hobbies del usuario basado en `dashboardApp.initialUserHobbies`
            const hobbiesContainer = document.getElementById('hobbies-list');
            const noHobbiesMessage = document.getElementById('no-hobbies-message');

            hobbiesContainer.innerHTML = ''; // Limpiar contenedor

            if (dashboardApp.initialUserHobbies.length > 0 && (dashboardApp.initialUserHobbies.length !== 1 || dashboardApp.initialUserHobbies[0] !== 'Sin hobbies')) {
                noHobbiesMessage.style.display = 'none';
                dashboardApp.initialUserHobbies.forEach(hobby => {
                    const hobbyElement = document.createElement('div');
                    hobbyElement.className = 'hobby-badge d-flex align-items-center me-2 mb-2';
                    hobbyElement.innerHTML = `
                        <span>${hobby}</span>
                        <button type="button" class="btn-eliminar-hobby" data-hobby="${hobby}">&times;</button>
                    `;
                    hobbiesContainer.appendChild(hobbyElement);
                });
            } else {
                noHobbiesMessage.style.display = 'block';
            }
        },
        add: async function(hobby) {
            try {
                const response = await fetch('/hobbies/agregar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ hobby: hobby })
                });
                if (response.ok) {
                    // Actualizar hobbies iniciales y UI
                    dashboardApp.initialUserHobbies.push(hobby);
                    dashboardApp.hobbies.updateHobbyListUI();
                    document.getElementById('input-hobby').value = ''; // Limpiar input
                } else {
                    alert("Error al añadir hobby.");
                }
            } catch (err) {
                console.error("Error al añadir hobby:", err);
                alert("Ocurrió un error de red al añadir el hobby.");
            }
        },
        remove: async function(hobby) {
            try {
                const response = await fetch('/hobbies/eliminar', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ hobby: hobby })
                });
                if (response.ok) {
                    // Actualizar hobbies iniciales y UI
                    dashboardApp.initialUserHobbies = dashboardApp.initialUserHobbies.filter(h => h !== hobby);
                    dashboardApp.hobbies.updateHobbyListUI();
                    // Si se está filtrando por este hobby, recargar gráfico
                    if (document.getElementById('filtro-hobbies').value === hobby) {
                        dashboardApp.hobbies.filterByHobby();
                    }
                } else {
                    alert("Error al eliminar hobby.");
                }
            } catch (err) {
                console.error("Error al eliminar hobby:", err);
                alert("Ocurrió un error de red al eliminar el hobby.");
            }
        }
    },
    eventos: {
        loadData: async function() {
            dashboardApp.ui.showLoading('eventos', true);
            try {
                const [dataTodos, dataMisIds] = await Promise.all([
                    fetch('/api/eventos').then(res => res.json()),
                    fetch('/api/mis-eventos-ids').then(res => res.json())
                ]);
                dashboardApp.ui.showLoading('eventos', false);

                if (dataTodos.success && dataMisIds.success) {
                    dashboardApp.eventsData = dataTodos.eventos || [];
                    const misEventosIds = new Set(dataMisIds.eventoIds || []);
                    dashboardApp.eventos.showEvents(dashboardApp.eventsData, misEventosIds);
                    dashboardApp.eventos.updateStats(dashboardApp.eventsData.length, dashboardApp.eventsData.length, 0);
                    document.getElementById('eventos-stats').style.display = 'flex';
                } else {
                    document.getElementById('eventos-container').innerHTML = `<div class="alert alert-warning">Error cargando datos de eventos.</div>`;
                }
            } catch (error) {
                dashboardApp.ui.showLoading('eventos', false);
                console.error("Error en Promise.all de eventos:", error);
                document.getElementById('eventos-container').innerHTML = `<div class="alert alert-danger">Error de red al cargar eventos.</div>`;
            }
        },
        loadCategories: async function() {
            try {
                const response = await fetch('/api/eventos/categorias');
                const data = await response.json();
                if (data && data.success) {
                    dashboardApp.categories = data.categorias || [];
                    const select = document.getElementById('categoria-eventos');
                    select.innerHTML = '<option value="">Todas las categorías</option>';
                    dashboardApp.categories.forEach(categoria => {
                        const option = document.createElement('option');
                        option.value = categoria;
                        option.textContent = categoria;
                        select.appendChild(option);
                    });
                    document.getElementById('categorias-disponibles').textContent = dashboardApp.categories.length;
                }
            } catch (error) {
                console.error('Error cargando categorías:', error);
            }
        },
        loadEventsForFilter: async function() {
            try {
                const response = await fetch('/api/eventos');
                const data = await response.json();
                if (data && data.success) {
                    const select = document.getElementById('filtro-eventos-amigos');
                    const selectedValue = select.value;
                    select.length = 1; // Limpiar opciones excepto la primera
                    data.eventos.forEach(evento => {
                        const option = document.createElement('option');
                        option.value = evento.id;
                        option.textContent = evento.titulo;
                        select.appendChild(option);
                    });
                    select.value = selectedValue; // Restaurar valor seleccionado
                } else {
                    console.error("Error en la respuesta de la API al cargar eventos para filtro:", data.error);
                }
            } catch (error) {
                console.error('Error en fetch al cargar eventos para filtro:', error);
            }
        },
        applyFilters: function() {
            const busqueda = document.getElementById('busqueda-eventos').value.trim();
            const categoria = document.getElementById('categoria-eventos').value;
            const precio = document.getElementById('precio-eventos').value;

            const params = new URLSearchParams();
            if (busqueda) params.append('busqueda', busqueda);
            if (categoria) params.append('categoria', categoria);
            if (precio) params.append('precio', precio);

            dashboardApp.ui.showLoading('eventos', true);
            fetch(`/api/eventos/filtrar?${params.toString()}`)
                .then(response => response.json())
                .then(data => {
                    dashboardApp.ui.showLoading('eventos', false);
                    if (data && data.success) {
                        const eventosFiltrados = data.eventos || [];
                        // Se asume que `misEventosIds` ya está disponible o se recupera nuevamente
                        fetch('/api/mis-eventos-ids').then(res => res.json()).then(misIdsData => {
                            const misEventosIds = new Set(misIdsData.eventoIds || []);
                            dashboardApp.eventos.showEvents(eventosFiltrados, misEventosIds);
                            let filtrosActivos = 0;
                            if (busqueda) filtrosActivos++;
                            if (categoria) filtrosActivos++;
                            if (precio) filtrosActivos++;
                            dashboardApp.eventos.updateStats(dashboardApp.eventsData.length, eventosFiltrados.length, filtrosActivos);
                        });
                    } else {
                        const err = data && data.error ? data.error : 'Error aplicando filtros';
                        document.getElementById('eventos-container').innerHTML = `<div class="alert alert-warning"><i class="fas fa-exclamation-triangle me-2"></i>Error: ${err}</div>`;
                    }
                })
                .catch(error => {
                    dashboardApp.ui.showLoading('eventos', false);
                    document.getElementById('eventos-container').innerHTML = `<div class="alert alert-danger"><i class="fas fa-times-circle me-2"></i>Error filtrando eventos: ${error}</div>`;
                });
        },
        clearFilters: function() {
            document.getElementById('busqueda-eventos').value = '';
            document.getElementById('categoria-eventos').value = '';
            document.getElementById('precio-eventos').value = '';
            dashboardApp.eventos.showEvents(dashboardApp.eventsData, new Set()); // Mostrar todos, asumiendo `misEventosIds` se recupera al mostrar
            dashboardApp.eventos.updateStats(dashboardApp.eventsData.length, dashboardApp.eventsData.length, 0);
        },
        showEvents: function(eventos, misEventosIds) {
            const container = document.getElementById('eventos-container');
            if (!eventos || eventos.length === 0) {
                container.innerHTML = `
                    <div class="no-events">
                        <i class="fas fa-calendar-times"></i>
                        <h4>No se encontraron eventos</h4>
                        <p>No hay eventos disponibles con los filtros aplicados.</p>
                    </div>
                `;
                return;
            }
            let html = '<div class="row">';
            eventos.forEach(evento => {
                const fechaEvento = new Date(evento.fechaEvento);
                const fechaFormateada = fechaEvento.toLocaleDateString('es-CO', {
                    day: 'numeric', month: 'long', year: 'numeric'
                });
                const horaFormateada = fechaEvento.toLocaleTimeString('es-CO', {
                    hour: '2-digit', minute: '2-digit'
                });
                const esGratis = !evento.precio || evento.precio === 0;
                const precioTexto = esGratis ? 'GRATIS' : `${evento.precio.toLocaleString('es-CO')}`;
                const precioClass = esGratis ? 'gratis' : '';

                let botonAsistenciaHtml = '';
                if (misEventosIds.has(evento.id)) {
                    botonAsistenciaHtml = `
                        <button id="btn-asistir-${evento.id}" class="btn btn-secondary w-100" onclick="dashboardApp.eventos.cancelAttendance(${evento.id}, this)">
                            <i class="fas fa-times-circle me-1"></i> Cancelar Asistencia
                        </button>
                    `;
                } else {
                    botonAsistenciaHtml = `
                        <button id="btn-asistir-${evento.id}" class="btn btn-success w-100" onclick="dashboardApp.eventos.confirmAttendance(${evento.id}, this)">
                            <i class="fas fa-check-circle me-1"></i> Confirmar Asistencia
                        </button>
                    `;
                }

                html += `
                    <div class="col-lg-4 col-md-6 mb-4">
                        <div class="event-card card h-100">
                            <div class="position-relative">
                                <img src="${evento.imagenUrl || 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=500'}"
                                     class="event-image" alt="${evento.titulo}">
                                <div class="event-category">${evento.categoria || 'General'}</div>
                                <div class="event-price ${precioClass}">${precioTexto}</div>
                            </div>
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title mb-3">${evento.titulo}</h5>
                                <p class="event-date mb-2">
                                    <i class="fas fa-calendar me-2"></i> ${fechaFormateada} a las ${horaFormateada}
                                </p>
                                <p class="event-location mb-2">
                                    <i class="fas fa-map-marker-alt me-2"></i> ${evento.lugar}
                                </p>
                                <p class="event-organizer mb-3">
                                    <i class="fas fa-user-tie me-2"></i> ${evento.organizador}
                                </p>
                                <p class="card-text flex-grow-1 mb-3">
                                    ${evento.descripcion ? (evento.descripcion.length > 150 ?
                                        evento.descripcion.substring(0, 150) + '...' : evento.descripcion) :
                                        'Sin descripción disponible'}
                                </p>
                                <div class="mt-auto">
                                    ${evento.capacidadMaxima ?
                                        `<small class="text-muted d-block mb-2">
                                            <i class="fas fa-users me-1"></i> Capacidad máxima: ${evento.capacidadMaxima} personas
                                        </small>` : ''
                                    }
                                    <div class="d-flex flex-column gap-2">
                                        <div class="btn-group w-100" role="group">
                                            <a href="/eventos/editar/${evento.id}" class="btn btn-light btn-sm"><i class="fas fa-edit"></i> Editar</a>
                                            <a href="/eventos/eliminar/${evento.id}" class="btn btn-outline-danger btn-sm"
                                               onclick="return confirm('¿Estás seguro de que quieres eliminar el evento \\'${evento.titulo}\\'?');">
                                               <i class="fas fa-trash"></i> Eliminar
                                            </a>
                                        </div>
                                        ${botonAsistenciaHtml}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            });
            html += '</div>';
            container.innerHTML = html;
        },
        updateStats: function(total, mostrados, filtros) {
            document.getElementById('total-eventos').textContent = total;
            document.getElementById('eventos-mostrados').textContent = mostrados;
            document.getElementById('filtros-activos').textContent = filtros;
            document.getElementById('categorias-disponibles').textContent = dashboardApp.categories.length;
        },
        confirmAttendance: async function(eventoId, boton) {
            boton.disabled = true;
            boton.innerHTML = `<span class="spinner-border spinner-border-sm me-1"></span> Confirmando...`;
            try {
                const response = await fetch(`/api/eventos/${eventoId}/asistir`, { method: 'POST' });
                const data = await response.json();
                if (data.success) {
                    boton.className = "btn btn-info w-100";
                    boton.innerHTML = `<i class="fas fa-star"></i> ¡Asistirás!`;
                } else {
                    boton.innerHTML = `Error al confirmar`;
                    boton.className = "btn btn-danger w-100";
                    setTimeout(() => {
                        boton.disabled = false;
                        boton.className = "btn btn-success w-100";
                        boton.innerHTML = `<i class="fas fa-check-circle me-1"></i> Confirmar Asistencia`;
                    }, 2000);
                }
            } catch (err) {
                console.error('Fetch error:', err);
                boton.disabled = false;
            }
        },
        cancelAttendance: async function(eventoId, boton) {
            boton.disabled = true;
            boton.innerHTML = `<span class="spinner-border spinner-border-sm me-1"></span> Cancelando...`;
            try {
                const response = await fetch(`/api/eventos/${eventoId}/cancelar`, { method: 'POST' });
                const data = await response.json();
                if (data.success) {
                    boton.className = "btn btn-success w-100";
                    boton.innerHTML = `<i class="fas fa-check-circle me-1"></i> Confirmar Asistencia`;
                    boton.onclick = () => dashboardApp.eventos.confirmAttendance(eventoId, boton);
                    boton.disabled = false;
                } else {
                    alert("Error al cancelar la asistencia: " + data.error);
                    boton.className = "btn btn-secondary w-100";
                    boton.innerHTML = `<i class="fas fa-times-circle me-1"></i> Cancelar Asistencia`;
                    boton.disabled = false;
                }
            } catch (err) {
                console.error('Fetch error:', err);
                boton.disabled = false;
            }
        }
    },
    busquedaAmigos: {
        search: async function() {
            const searchTerm = document.getElementById('termino-busqueda').value;
            const resultadosDiv = document.getElementById('resultados-busqueda');
            if (searchTerm.trim().length < 2) {
                resultadosDiv.innerHTML = `<div class="alert alert-warning">Escribe al menos 2 letras para buscar.</div>`;
                return;
            }
            resultadosDiv.innerHTML = `<div class="text-center"><div class="spinner-border"></div></div>`;
            try {
                const response = await fetch(`/api/usuarios/buscar?q=${encodeURIComponent(searchTerm)}`);
                const usuarios = await response.json();
                dashboardApp.busquedaAmigos.showResults(usuarios);
            } catch (err) {
                resultadosDiv.innerHTML = `<div class="alert alert-danger">Error en la búsqueda.</div>`;
                console.error(err);
            }
        },
        showResults: function(usuarios) {
            const resultadosDiv = document.getElementById('resultados-busqueda');
            if (!usuarios || usuarios.length === 0) {
                resultadosDiv.innerHTML = `<div class="alert alert-info">No se encontraron usuarios con ese nombre.</div>`;
                return;
            }
            let html = '<ul class="list-group">';
            usuarios.forEach(usuario => {
                html += `
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-0">${usuario.nombre}</h6>
                            <small class="text-muted">${usuario.email}</small>
                        </div>
                        <button id="btn-amigo-${usuario.id}" class="btn btn-sm btn-outline-primary" onclick="dashboardApp.busquedaAmigos.add(${usuario.id}, this)">
                            <i class="fas fa-user-plus me-1"></i> Agregar Amigo
                        </button>
                    </li>
                `;
            });
            html += '</ul>';
            resultadosDiv.innerHTML = html;
        },
        add: async function(amigoId, boton) {
            boton.disabled = true;
            boton.innerHTML = `<span class="spinner-border spinner-border-sm"></span>`;
            try {
                const response = await fetch(`/api/amigos/agregar/${amigoId}`, { method: 'POST' });
                const data = await response.json();
                if (data.success) {
                    boton.className = "btn btn-sm btn-success";
                    boton.innerHTML = `<i class="fas fa-check"></i> Amigo Agregado`;
                } else {
                    boton.className = "btn btn-sm btn-danger";
                    boton.innerHTML = `Error`;
                    boton.disabled = false;
                }
            } catch (err) {
                console.error(err);
                boton.disabled = false;
            }
        }
    },
    graphs: {
        createGradients: function(svg) {
            const defs = svg.append("defs");
            const userGradient = defs.append("linearGradient").attr("id", "userGradient");
            userGradient.append("stop").attr("offset", "0%").attr("stop-color", "var(--user-color-start)");
            userGradient.append("stop").attr("offset", "100%").attr("stop-color", "var(--user-color-end)");

            const friendGradient = defs.append("linearGradient").attr("id", "friendGradient");
            friendGradient.append("stop").attr("offset", "0%").attr("stop-color", "var(--friend-color-start)");
            friendGradient.append("stop").attr("offset", "100%").attr("stop-color", "var(--friend-color-end)");

            const linkGradient = defs.append("linearGradient").attr("id", "linkGradient");
            linkGradient.append("stop").attr("offset", "0%").attr("stop-color", "var(--link-color-start)").attr("stop-opacity", 0.6);
            linkGradient.append("stop").attr("offset", "100%").attr("stop-color", "var(--link-color-end)").attr("stop-opacity", 0.6);
        },
        createFriendsGraph: function(data) {
            d3.select("#graph-container").selectAll("*").remove();
            const container = document.getElementById('graph-container');
            const width = container.offsetWidth || 600;
            const height = 450;
            const svg = d3.select("#graph-container").append("svg").attr("width", width).attr("height", height);
            dashboardApp.graphs.createGradients(svg);

            const nodes = [
                { id: data.usuarioActual.id, name: data.usuarioActual.nombre, email: data.usuarioActual.email, isCurrentUser: true }
            ];
            if (Array.isArray(data.amigos)) {
                data.amigos.forEach(amigo => {
                    nodes.push({ id: amigo.id, name: amigo.nombre, email: amigo.email, isCurrentUser: false });
                });
            }

            const links = (Array.isArray(data.amigos) ? data.amigos : []).map(amigo => ({ source: data.usuarioActual.id, target: amigo.id }));

            if (nodes.length === 1) {
                svg.append("text").attr("x", width / 2).attr("y", height / 2).attr("text-anchor", "middle").attr("class", "node-label").style("font-size", "18px").style("fill", "#636e72").text("No tienes amigos conectados aún");
                return;
            }

            const simulation = d3.forceSimulation(nodes)
                .force("link", d3.forceLink(links).id(d => d.id).distance(150))
                .force("charge", d3.forceManyBody().strength(-400))
                .force("center", d3.forceCenter(width / 2, height / 2))
                .force("collision", d3.forceCollide().radius(50));

            const link = svg.append("g").attr("class", "links").selectAll("line").data(links).join("line").attr("class", "link");

            const nodeGroup = svg.append("g").attr("class", "nodes").selectAll("g").data(nodes).join("g").call(d3.drag().on("start", dragstarted).on("drag", dragged).on("end", dragended));

            nodeGroup.append("circle").attr("class", d => d.isCurrentUser ? "node current-user" : "node").attr("r", 35);
            nodeGroup.append("text").attr("class", "node-label").attr("y", 5).style("font-size", "16px").style("font-weight", "bold").style("fill", "#2d3436").text(d => dashboardApp.ui.getInitials(d.name));
            nodeGroup.append("text").attr("class", "node-label").attr("y", 55).style("font-size", "14px").style("font-weight", d => d.isCurrentUser ? "bold" : "600").text(d => d.name);
            nodeGroup.append("title").text(d => d.email ? `${d.name}\n${d.email}` : d.name);

            function dragstarted(event, d) { if (!event.active) simulation.alphaTarget(0.3).restart(); d.fx = d.x; d.fy = d.y; }
            function dragged(event, d) { d.fx = event.x; d.fy = event.y; }
            function dragended(event, d) { if (!event.active) simulation.alphaTarget(0); d.fx = null; d.fy = null; }

            simulation.on("tick", () => {
                link.attr("x1", d => d.source.x).attr("y1", d => d.source.y).attr("x2", d => d.target.x).attr("y2", d => d.target.y);
                nodeGroup.attr("transform", d => `translate(${d.x},${d.y})`);
            });
        },
        createHobbiesGraph: function(data) {
            d3.select("#hobbies-graph-container").selectAll("*").remove();
            const container = document.getElementById('hobbies-graph-container');
            const width = container.offsetWidth || 600;
            const height = 450;
            const svg = d3.select("#hobbies-graph-container").append("svg").attr("width", width).attr("height", height);
            dashboardApp.graphs.createGradients(svg);

            const nodes = [
                { id: data.usuarioActual.id, name: data.usuarioActual.nombre, hobbies: Array.isArray(data.usuarioActual.hobbies) ? data.usuarioActual.hobbies : [], isCurrentUser: true }
            ];
            if (Array.isArray(data.amigosConHobbies)) {
                data.amigosConHobbies.forEach(amigo => {
                    nodes.push({ id: amigo.id, name: amigo.nombre, hobbies: Array.isArray(amigo.hobbies) ? amigo.hobbies : [], isCurrentUser: false });
                });
            }

            const links = (Array.isArray(data.amigosConHobbies) ? data.amigosConHobbies : []).map(amigo => ({ source: data.usuarioActual.id, target: amigo.id }));

            if (nodes.length === 1) {
                svg.append("text").attr("x", width / 2).attr("y", height / 2).attr("text-anchor", "middle").attr("class", "node-label").style("font-size", "18px").style("fill", "#636e72").text(data.filtroAplicado ? `No hay amigos con el hobby "${data.filtroAplicado}"` : "No tienes amigos conectados");
                return;
            }

            const simulation = d3.forceSimulation(nodes)
                .force("link", d3.forceLink(links).id(d => d.id).distance(180))
                .force("charge", d3.forceManyBody().strength(-500))
                .force("center", d3.forceCenter(width / 2, height / 2))
                .force("collision", d3.forceCollide().radius(60));

            const link = svg.append("g").attr("class", "links").selectAll("line").data(links).join("line").attr("class", "link");

            const nodeGroup = svg.append("g").attr("class", "nodes").selectAll("g").data(nodes).join("g").call(d3.drag().on("start", dragstarted).on("drag", dragged).on("end", dragended));

            nodeGroup.append("circle").attr("class", d => d.isCurrentUser ? "node current-user" : "node").attr("r", 40);
            nodeGroup.append("text").attr("class", "node-label").attr("y", 5).style("font-size", "18px").style("font-weight", "bold").style("fill", "#2d3436").text(d => dashboardApp.ui.getInitials(d.name));
            nodeGroup.append("text").attr("class", "node-label").attr("y", 60).style("font-size", "14px").style("font-weight", d => d.isCurrentUser ? "bold" : "600").text(d => d.name);

            nodeGroup.each(function(d) {
                const group = d3.select(this);
                (d.hobbies || []).slice(0, 2).forEach((hobby, index) => {
                    group.append("text").attr("y", 78 + (index * 14)).attr("class", "node-hobby").style("font-weight", "500").text(hobby);
                });
                if ((d.hobbies || []).length > 2) {
                    group.append("text").attr("y", 78 + (2 * 14)).attr("class", "node-hobby").style("font-style", "italic").text(`+${d.hobbies.length - 2} más`);
                }
            });

            nodeGroup.append("title").text(d => `${d.name}\nHobbies: ${(d.hobbies || []).join(', ') || 'Sin hobbies'}`);

            function dragstarted(event, d) { if (!event.active) simulation.alphaTarget(0.3).restart(); d.fx = d.x; d.fy = d.y; }
            function dragged(event, d) { d.fx = event.x; d.fy = event.y; }
            function dragended(event, d) { if (!event.active) simulation.alphaTarget(0); d.fx = null; d.fy = null; }

            simulation.on("tick", () => {
                link.attr("x1", d => d.source.x).attr("y1", d => d.source.y).attr("x2", d => d.target.x).attr("y2", d => d.target.y);
                nodeGroup.attr("transform", d => `translate(${d.x},${d.y})`);
            });
        }
    }
};

document.addEventListener('DOMContentLoaded', function() {
    if (typeof initialUserName !== 'undefined' && initialUserName) {
        document.getElementById('welcome-user-name').textContent = `Hola, ${initialUserName}`;
    } else {
        console.warn("Nombre de usuario no encontrado.");
    }

    if (typeof initialUserHobbies !== 'undefined' && Array.isArray(initialUserHobbies)) {
        dashboardApp.initialUserHobbies = initialUserHobbies;
        dashboardApp.hobbies.updateHobbyListUI(); 
    } else {
        console.warn("Hobbies del usuario no encontrados o no son un array.");
        dashboardApp.initialUserHobbies = []; 
    }

    dashboardApp.ui.showSection('amigos', {
        preventDefault: () => {},
        currentTarget: document.querySelector('.list-group-item.active')
    });

    document.getElementById('form-add-hobby').addEventListener('submit', function(e) {
        e.preventDefault();
        const hobbyInput = document.getElementById('input-hobby');
        const hobby = hobbyInput.value.trim();
        if (hobby) {
            dashboardApp.hobbies.add(hobby);
        }
    });

    document.getElementById('hobbies-list').addEventListener('click', function(e) {
        if (e.target.classList.contains('btn-eliminar-hobby')) {
            const hobby = e.target.getAttribute('data-hobby');
            if (hobby) {
                dashboardApp.hobbies.remove(hobby);
            }
        }
    });

    window.addEventListener('resize', () => {
        if (dashboardApp.currentSection === 'amigos') {
            dashboardApp.amigos.loadData();
        } else if (dashboardApp.currentSection === 'hobbies') {
            dashboardApp.hobbies.filterByHobby(); 
        } else if (dashboardApp.currentSection === 'eventos') {
        }
    });
});