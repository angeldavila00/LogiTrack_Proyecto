const API = 'http://172.16.41.25:8080';

document.getElementById('topbar-date').textContent = new Date().toLocaleDateString('es-CO', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
document.addEventListener('keydown', e => { if (e.key === 'Enter' && document.getElementById('login-screen').style.display !== 'none') doLogin(); });

// ===== DECODIFICA EL ROL DEL TOKEN JWT =====
function getRolFromToken(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.rol || payload.role || payload.authorities || '';
    } catch (e) {
        return '';
    }
}

// ===== APLICA PERMISOS SEGÚN ROL =====
function aplicarPermisosPorRol(rol) {
    const btnNuevoUsuario = $('btn-nuevo-usuario');
    if (btnNuevoUsuario) {
        btnNuevoUsuario.style.display = rol === 'ADMIN' ? 'inline-block' : 'none';
    }
}

// ===== HELPERS DE ROL =====
function isAdmin() { return localStorage.getItem('rol') === 'ADMIN'; }
// ===== AUTH =====
function doLogin() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    fetch(`${API}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })
        .then(response => {
            if (!response.ok) throw new Error('Credenciales inválidas');
            return response.json();
        })
        .then(data => {
            localStorage.setItem('token', data.token);

            const rol = getRolFromToken(data.token);
            localStorage.setItem('rol', rol);

            document.getElementById('login-screen').style.display = 'none';
            document.getElementById('dashboard').style.display = 'block';
            document.getElementById('user-display').textContent = username;
            document.getElementById('user-avatar').textContent = username[0].toUpperCase();


            aplicarPermisosPorRol(rol);

            loadStats();
            loadPage('usuarios');
            toast('Sesión iniciada correctamente');
        })
        .catch(() => {
            localStorage.removeItem('token');
            document.getElementById('login-error').style.display = 'block';
        });
}
function doLogout() {
    localStorage.removeItem('token');
    localStorage.removeItem('rol'); // ✅ limpia el rol
    document.getElementById('dashboard').style.display = 'none';
    document.getElementById('login-screen').style.display = 'flex';
    document.getElementById('login-error').style.display = 'none';
    document.getElementById('login-password').value = '';
}

// ===== REGISTRO =====
function switchTab(tab) {
    document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.auth-panel').forEach(p => p.classList.remove('active'));
    document.getElementById('tab-' + tab).classList.add('active');
    document.getElementById('panel-' + tab).classList.add('active');
    document.getElementById('login-error').style.display = 'none';
    document.getElementById('registro-error').style.display = 'none';
    document.getElementById('registro-success').style.display = 'none';
}

function doRegistro() {
    const nombre = document.getElementById('reg-nombre').value.trim();
    const documento = document.getElementById('reg-documento').value.trim();
    const username = document.getElementById('reg-username').value.trim();
    const password = document.getElementById('reg-password').value;
    const password2 = document.getElementById('reg-password2').value;

    const errEl = document.getElementById('registro-error');
    const okEl = document.getElementById('registro-success');
    errEl.style.display = 'none';
    okEl.style.display = 'none';

    if (!nombre || !documento || !username || !password) {
        errEl.textContent = 'Todos los campos son obligatorios.';
        errEl.style.display = 'block';
        return;
    }
    if (password !== password2) {
        errEl.textContent = 'Las contraseñas no coinciden.';
        errEl.style.display = 'block';
        return;
    }
    if (password.length < 6) {
        errEl.textContent = 'La contraseña debe tener al menos 6 caracteres.';
        errEl.style.display = 'block';
        return;
    }

    fetch(`${API}/auth/registro`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre, documento, username, password, rol: document.getElementById('reg-rol').value })
    })
        .then(response => {
            if (!response.ok) return response.json().then(e => { throw new Error(e.message || 'Error al registrar'); });
            return response.json();
        })
        .then(() => {
            okEl.style.display = 'block';
            document.getElementById('login-username').value = username;
            document.getElementById('login-password').value = '';
            setTimeout(() => switchTab('login'), 1500);
        })
        .catch(error => {
            errEl.textContent = error.message;
            errEl.style.display = 'block';
        });
}

// ===== apiFetch CORREGIDO =====
// Lee correctamente todos los formatos de error del GlobalExceptionHandler:
// - { errors: { campo: "msg" } }  → validaciones @NotBlank, @Positive, etc.
// - { message: "..." }            → RuntimeException, EntityNotFoundException
// - { error: "..." }              → otros formatos
function apiFetch(url, options = {}) {
    const token = localStorage.getItem('token');

    if (!token) {
        alert('Debes iniciar sesión primero');
        return Promise.reject('No token');
    }

    return fetch(url, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token,
            ...options.headers
        }
    })
        .then(response => {
            if (response.status === 401) {
                localStorage.removeItem('token');
                throw new Error('Sesión expirada. Inicia sesión nuevamente.');
            }
            if (response.status === 403) {
                throw new Error('No tienes permisos para esta acción.');
            }
            if (response.status === 204) return null;

            if (!response.ok) {
                return response.json().then(e => {

                    // Errores de validación: @NotBlank, @Positive, @NotNull, etc.
                    // GlobalExceptionHandler retorna: { errors: { campo: "mensaje" } }
                    if (e.errors && typeof e.errors === 'object') {
                        const mensajes = Object.entries(e.errors)
                            .map(([campo, msg]) => `• ${campo}: ${msg}`)
                            .join('\n');
                        throw new Error(mensajes);
                    }

                    // RuntimeException, EntityNotFoundException, BusinessRuleException
                    // GlobalExceptionHandler retorna: { message: "..." }
                    if (e.message) {
                        throw new Error(e.message);
                    }

                    // Fallback para otros formatos: { error: "..." }
                    if (e.error) {
                        throw new Error(e.error);
                    }

                    throw new Error('Error en la petición');
                });
            }

            return response.json();
        });
}

// ===== HELPERS =====
const $ = id => document.getElementById(id);
const val = id => $(id).value;
const int = id => parseInt($(id).value);
const flt = id => parseFloat($(id).value);

function toast(msg, type = 'ok') {
    const t = document.createElement('div');
    t.className = 'toast' + (type === 'error' ? ' error' : '');
    t.innerHTML = `<span class="toast-icon">${type === 'error' ? '⚠' : '✓'}</span><span>${msg}</span>`;
    $('toast-container').appendChild(t);
    // Los errores con múltiples líneas duran más
    const duracion = msg.includes('\n') ? 5000 : 3200;
    setTimeout(() => t.remove(), duracion);
}

function showTable(n) { $(`loading-${n}`).style.display = 'none'; $(`table-${n}`).style.display = 'table'; }
function hideTable(n) { $(`loading-${n}`).style.display = 'flex'; $(`table-${n}`).style.display = 'none'; }

function openNewModal(id) {
    if (id === 'modal-usuario') {
        $('usuario-id').value = '';
        $('usuario-nombre').value = '';
        $('usuario-documento').value = '';
        $('usuario-username').value = '';
        $('usuario-password').value = '';
        $('usuario-rol').value = 'EMPLEADO';
        $('modal-usuario-title').textContent = 'Nuevo usuario';
    }
    if (id === 'modal-bodega') {
        $('bodega-id').value = '';
        $('bodega-nombre').value = '';
        $('bodega-ubicacion').value = '';
        $('bodega-capacidad').value = '';
        $('modal-bodega-title').textContent = 'Nueva bodega';
    }
    if (id === 'modal-producto') {
        $('producto-id').value = '';
        $('producto-nombre').value = '';
        $('producto-categoria').value = '';
        $('producto-precio').value = '';
        $('producto-stock').value = '';
        $('modal-producto-title').textContent = 'Nuevo producto';
    }
    openModal(id);
}
function openModal(id) {
    $(id).classList.add('open');
    if (id === 'modal-bodega') poblarSelect('bodega-usuarioid', '/api/usuario', u => ({ v: u.id, t: u.nombre }));
    if (id === 'modal-producto') poblarSelect('producto-bodegaid', '/api/bodega', b => ({ v: b.id, t: b.nombre + ' — ' + b.ubicacion }));
    if (id === 'modal-movimiento') {
        poblarSelect('movimiento-usuarioid', '/api/usuario', u => ({ v: u.id, t: u.nombre }));
        poblarSelect('movimiento-origenid', '/api/bodega', b => ({ v: b.id, t: b.nombre + ' (' + b.ubicacion + ')' }));
        poblarSelect('movimiento-destinoid', '/api/bodega', b => ({ v: b.id, t: b.nombre + ' (' + b.ubicacion + ')' }));
    }
    if (id === 'modal-detalle') {
        poblarSelect('detalle-movimientoid', '/api/movimiento', m => ({ v: m.id, t: '#' + m.id + ' — ' + m.tipoMovimiento + ' (' + new Date(m.fecha).toLocaleDateString('es-CO') + ')' }));
        poblarSelect('detalle-productoid', '/api/producto', p => ({ v: p.id, t: p.nombre + ' (stock: ' + p.stock + ')' }));
    }
}

function poblarSelect(selectId, endpoint, mapper) {
    apiFetch(`${API}${endpoint}`)
        .then(data => {
            const sel = $(selectId);
            const valorActual = sel.value;
            sel.innerHTML = '<option value="">— Seleccione —</option>' +
                data.map(item => {
                    const { v, t } = mapper(item);
                    return `<option value="${v}">${t}</option>`;
                }).join('');
            if (valorActual) sel.value = valorActual;
        })
        .catch(() => { });
}

function closeModal(id) { $(id).classList.remove('open'); }
function badge(txt, cls) { return `<span class="badge ${cls}">${txt}</span>`; }
function tipoBadge(t) { return badge(t, { ENTRADA: 'bg', SALIDA: 'br', TRANSFERENCIA: 'bb' }[t] || 'bb'); }
function rowActions(editFn, deleteFn) {
    return `<div class="actions">${editFn ? `<button class="btn-action" onclick="${editFn}">Editar</button>` : ''}<button class="btn-action del" onclick="${deleteFn}">Eliminar</button></div>`;
}

// ===== NAVEGACIÓN =====
function navigate(page, el) {
    document.querySelectorAll('.sb-item').forEach(i => i.classList.remove('active'));
    el.classList.add('active');
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    $('page-' + page).classList.add('active');
    const titles = { usuarios: 'Usuarios del sistema', bodegas: 'Bodegas registradas', productos: 'Inventario de productos', movimientos: 'Registro de movimientos', detalles: 'Detalle de movimientos', auditoria: 'Registro de auditoría' };
    $('topbar-title').textContent = titles[page] || page;
    loadPage(page);
}

function loadPage(page) {
    if (page !== 'productos' && _stockBajoActivo) {
        _stockBajoActivo = false;
        const btn = $('btn-stock-bajo');
        if (btn) { btn.style.background = 'var(--amber-bg)'; btn.style.color = 'var(--amber)'; btn.style.borderColor = 'var(--amber)'; }
    }
    ({ usuarios: loadUsuarios, bodegas: loadBodegas, productos: loadProductos, movimientos: loadMovimientos, detalles: loadDetalles, auditoria: loadAuditoria })[page]?.();
}

// ===== STATS =====
function loadStats() {
    Promise.all([
        apiFetch(`${API}/api/usuario`),
        apiFetch(`${API}/api/bodega`),
        apiFetch(`${API}/api/producto`),
        apiFetch(`${API}/api/movimiento`)
    ])
        .then(([u, b, p, m]) => {
            $('stat-usuarios').textContent = u.length;
            $('stat-bodegas').textContent = b.length;
            $('stat-productos').textContent = p.length;
            $('stat-movimientos').textContent = m.length;
            $('stats-row').style.display = 'grid';
        })
        .catch(() => { });
}

// ===== USUARIOS =====
function loadUsuarios() {
    hideTable('usuarios');
    apiFetch(`${API}/api/usuario`)
        .then(data => {
            $('tbody-usuarios').innerHTML = data.map(u => `<tr>
        <td class="id">${u.id}</td><td>${u.nombre}</td><td>${u.documento}</td><td>${u.username}</td>
        <td>${badge(u.rol, u.rol === 'ADMIN' ? 'by' : 'bb')}</td>
        <td>${isAdmin()
                    ? rowActions(`editUsuario(${u.id},'${u.nombre}','${u.documento}','${u.username}','${u.rol}')`, `deleteUsuario(${u.id})`)
                    : '<span style="font-size:12px;color:var(--gray-400)">Sin permisos</span>'}</td>
      </tr>`).join('');
            showTable('usuarios');
        })
        .catch(e => toast(e.message, 'error'));
}

function saveUsuario() {
    if (!isAdmin()) { toast('No tienes permisos para esta acción', 'error'); return; }
    const id = val('usuario-id');
    const password = val('usuario-password');
    if (!id && !password) {
        toast('La contraseña es obligatoria al crear un usuario', 'error');
        return;
    }
    const body = { nombre: val('usuario-nombre'), documento: val('usuario-documento'), username: val('usuario-username'), rol: val('usuario-rol') };
    if (password) body.password = password;
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API}/api/usuario/${id}` : `${API}/api/usuario`;

    apiFetch(url, { method, body: JSON.stringify(body) })
        .then(() => {
            closeModal('modal-usuario');
            toast(id ? 'Usuario actualizado' : 'Usuario creado');
            loadUsuarios(); loadStats();
        })
        .catch(e => toast(e.message, 'error'));
}

function editUsuario(id, nombre, documento, username, rol) {
    if (!isAdmin()) { toast('No tienes permisos para esta acción', 'error'); return; }
    $('usuario-id').value = id; $('usuario-nombre').value = nombre; $('usuario-documento').value = documento;
    $('usuario-username').value = username; $('usuario-rol').value = rol;
    $('usuario-password').value = '';
    $('modal-usuario-title').textContent = 'Editar usuario';
    openModal('modal-usuario');
}

function deleteUsuario(id) {
    if (!isAdmin()) { toast('No tienes permisos para esta acción', 'error'); return; }
    if (!confirm(`¿Eliminar el usuario #${id}? Esta acción no se puede deshacer.`)) return;
    apiFetch(`${API}/api/usuario/${id}`, { method: 'DELETE' })
        .then(() => { toast('Usuario eliminado'); loadUsuarios(); loadStats(); })
        .catch(e => toast(e.message, 'error'));
}

// ===== BODEGAS =====
function loadBodegas() {
    hideTable('bodegas');
    apiFetch(`${API}/api/bodega`)
        .then(data => {
            $('tbody-bodegas').innerHTML = data.map(b => `<tr>
        <td class="id">${b.id}</td><td>${b.nombre}</td><td>${b.ubicacion}</td>
        <td>${b.capacidad.toLocaleString()}</td><td>${b.usuario?.nombre || '—'}</td>
        <td>${rowActions(`editBodega(${b.id},'${b.nombre}','${b.ubicacion}',${b.capacidad},${b.usuario?.id})`, `deleteBodega(${b.id})`)}</td>
      </tr>`).join('');
            showTable('bodegas');
        })
        .catch(e => toast(e.message, 'error'));
}
