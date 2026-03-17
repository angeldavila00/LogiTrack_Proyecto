const API = 'http://192.168.1.79:8080';

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
function saveBodega() {
    const id = val('bodega-id');
    const body = { nombre: val('bodega-nombre'), ubicacion: val('bodega-ubicacion'), capacidad: int('bodega-capacidad'), usuarioId: int('bodega-usuarioid') };
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API}/api/bodega/${id}` : `${API}/api/bodega`;

    apiFetch(url, { method, body: JSON.stringify(body) })
        .then(() => {
            closeModal('modal-bodega');
            toast(id ? 'Bodega actualizada' : 'Bodega creada');
            loadBodegas(); loadStats();
        })
        .catch(e => toast(e.message, 'error'));
}

function editBodega(id, nombre, ubicacion, capacidad, usuarioId) {
    $('bodega-id').value = id; $('bodega-nombre').value = nombre; $('bodega-ubicacion').value = ubicacion;
    $('bodega-capacidad').value = capacidad; $('bodega-usuarioid').value = usuarioId;
    $('modal-bodega-title').textContent = 'Editar bodega';
    openModal('modal-bodega');
}

function deleteBodega(id) {
    if (!confirm(`¿Eliminar la bodega #${id}?`)) return;
    apiFetch(`${API}/api/bodega/${id}`, { method: 'DELETE' })
        .then(() => { toast('Bodega eliminada'); loadBodegas(); loadStats(); })
        .catch(e => toast(e.message, 'error'));
}

// ===== PRODUCTOS =====
function loadProductos() {
    hideTable('productos');
    apiFetch(`${API}/api/producto`)
        .then(data => {
            $('tbody-productos').innerHTML = data.map(p => {
                const stockStyle = p.stock === 0
                    ? 'color:var(--red);font-weight:700'
                    : p.stock < 10
                        ? 'color:var(--amber);font-weight:600'
                        : 'color:var(--green);font-weight:600';
                return `<tr>
          <td class="id">${p.id}</td>
          <td style="font-weight:500">${p.nombre}</td>
          <td>${badge(p.categoria, 'bb')}</td>
          <td style="font-weight:500">$${p.precio.toLocaleString('es-CO')}</td>
          <td><span style="${stockStyle}">${p.stock}</span> <span style="font-size:11px;color:var(--gray-400)">uds</span></td>
          <td>${p.bodega?.nombre || '—'}</td>
          <td style="font-size:12px;color:var(--gray-500)">${p.bodega?.ubicacion || '—'}</td>
          <td style="font-size:12px;color:var(--gray-500)">${p.bodega?.usuario?.nombre || '—'}</td>
          <td>${rowActions(`editProducto(${p.id},'${p.nombre}','${p.categoria}',${p.precio},${p.stock},${p.bodega?.id})`, `deleteProducto(${p.id})`)}</td>
        </tr>`;
            }).join('');
            showTable('productos');
        })
        .catch(e => toast(e.message, 'error'));
}

function saveProducto() {
    const id = val('producto-id');
    const body = { nombre: val('producto-nombre'), categoria: val('producto-categoria'), precio: flt('producto-precio'), stock: int('producto-stock'), bodegaId: int('producto-bodegaid') };
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API}/api/producto/${id}` : `${API}/api/producto`;

    apiFetch(url, { method, body: JSON.stringify(body) })
        .then(() => {
            closeModal('modal-producto');
            toast(id ? 'Producto actualizado' : 'Producto creado');
            loadProductos(); loadStats();
        })
        .catch(e => toast(e.message, 'error'));
}

function editProducto(id, nombre, categoria, precio, stock, bodegaId) {
    $('producto-id').value = id; $('producto-nombre').value = nombre; $('producto-categoria').value = categoria;
    $('producto-precio').value = precio; $('producto-stock').value = stock; $('producto-bodegaid').value = bodegaId;
    $('modal-producto-title').textContent = 'Editar producto';
    openModal('modal-producto');
}

function deleteProducto(id) {
    if (!confirm(`¿Eliminar el producto #${id}?`)) return;
    apiFetch(`${API}/api/producto/${id}`, { method: 'DELETE' })
        .then(() => { toast('Producto eliminado'); loadProductos(); loadStats(); })
        .catch(e => toast(e.message, 'error'));
}

// ===== MOVIMIENTOS =====
function loadMovimientos() {
    hideTable('movimientos');
    apiFetch(`${API}/api/movimiento`)
        .then(data => {
            $('tbody-movimientos').innerHTML = data.map(m => `<tr>
        <td class="id">${m.id}</td>
        <td>${new Date(m.fecha).toLocaleDateString('es-CO')}</td>
        <td>${tipoBadge(m.tipoMovimiento)}</td>
        <td>${m.usuario?.nombre || '—'}</td>
        <td>${m.bodegaOrigen?.nombre || '—'}</td>
        <td>${m.bodegaDestino?.nombre || '—'}</td>
        <td>${rowActions(null, `deleteMovimiento(${m.id})`)}</td>
      </tr>`).join('');
            showTable('movimientos');
        })
        .catch(e => toast(e.message, 'error'));
}

function agregarFilaDetalle() {
    const lista = $('detalles-lista');
    const fila = document.createElement('div');
    fila.style.cssText = 'display:flex;gap:8px;align-items:center';
    fila.innerHTML = `
    <select class="det-producto" style="flex:2;border:1px solid var(--gray-300);border-radius:7px;padding:8px 10px;font-family:var(--sans);font-size:13px;outline:none;background:var(--white)">
      <option value="">— Producto —</option>
    </select>
    <input type="number" placeholder="Cantidad" min="1"
      style="flex:1;border:1px solid var(--gray-300);border-radius:7px;padding:8px 10px;font-family:var(--sans);font-size:13px;outline:none"
      class="det-cantidad">
    <button type="button" onclick="this.parentElement.remove()"
      style="background:none;border:none;color:var(--gray-400);cursor:pointer;font-size:18px;line-height:1;padding:4px"
      onmouseover="this.style.color='var(--red)'" onmouseout="this.style.color='var(--gray-400)'">×</button>
  `;
    lista.appendChild(fila);

    apiFetch(`${API}/api/producto`)
        .then(data => {
            const sel = fila.querySelector('.det-producto');
            sel.innerHTML = '<option value="">— Seleccione producto —</option>' +
                data.map(p => `<option value="${p.id}">${p.nombre} (stock: ${p.stock})</option>`).join('');
        })
        .catch(() => { });
}

function saveMovimiento() {
    const filas = $('detalles-lista').querySelectorAll('div');
    const detalles = [];
    for (const fila of filas) {
        const productoId = parseInt(fila.querySelector('.det-producto').value);
        const cantidad = parseInt(fila.querySelector('.det-cantidad').value);
        if (!productoId || !cantidad || cantidad < 1) {
            toast('Selecciona un producto y una cantidad válida en cada fila', 'error');
            return;
        }
        detalles.push({ productoId, cantidad });
    }

    // ✅ Verifica que la fecha no esté vacía
    const fechaRaw = val('movimiento-fecha');
    if (!fechaRaw) {
        toast('La fecha es obligatoria', 'error');
        return;
    }

    // ✅ Agrega segundos si el input no los incluye: "2026-03-10T10:30" → "2026-03-10T10:30:00"
    const fecha = fechaRaw.length === 16 ? fechaRaw + ':00' : fechaRaw;

    const body = {
        fecha: fecha,
        tipoMovimiento: val('movimiento-tipo'),
        usuarioId: int('movimiento-usuarioid'),
        bodegaOrigenId: int('movimiento-origenid'),
        bodegaDestinoId: int('movimiento-destinoid'),
        detalles
    };

    apiFetch(`${API}/api/movimiento`, { method: 'POST', body: JSON.stringify(body) })
        .then(() => {
            closeModal('modal-movimiento');
            $('detalles-lista').innerHTML = '';
            toast('Movimiento registrado con ' + detalles.length + ' producto(s)');
            loadMovimientos(); loadDetalles(); loadStats();
        })
        .catch(e => toast(e.message, 'error'));
}

function deleteMovimiento(id) {
    if (!confirm(`¿Eliminar el movimiento #${id}?`)) return;
    apiFetch(`${API}/api/movimiento/${id}`, { method: 'DELETE' })
        .then(() => { toast('Movimiento eliminado'); loadMovimientos(); loadStats(); })
        .catch(e => toast(e.message, 'error'));
}

// ===== DETALLES =====
function loadDetalles() {
    hideTable('detalles');
    apiFetch(`${API}/api/movimiento_detalle`)
        .then(data => {
            $('tbody-detalles').innerHTML = data.map(d => `<tr>
        <td class="id">${d.id}</td>
        <td style="font-weight:600">${d.cantidad} <span style="font-size:11px;color:var(--gray-400)">uds</span></td>
        <td>${tipoBadge(d.movimiento?.tipoMovimiento)}</td>
        <td style="font-size:12px;color:var(--gray-500)">${d.movimiento?.fecha ? new Date(d.movimiento.fecha).toLocaleDateString('es-CO') : '—'}</td>
        <td style="font-weight:500">${d.producto?.nombre || '—'}</td>
        <td>${d.producto?.categoria ? badge(d.producto.categoria, 'bb') : '—'}</td>
        <td><span style="font-size:12px;font-weight:600;color:var(--gray-700)">${d.producto?.stock ?? '—'}</span> <span style="font-size:11px;color:var(--gray-400)">uds</span></td>
        <td>${rowActions(null, `deleteDetalle(${d.id})`)}</td>
      </tr>`).join('');
            showTable('detalles');
        })
        .catch(e => toast(e.message, 'error'));
}

function saveDetalle() {
    const body = { cantidad: int('detalle-cantidad'), movimientoId: int('detalle-movimientoid'), productoId: int('detalle-productoid') };

    apiFetch(`${API}/api/movimiento_detalle`, { method: 'POST', body: JSON.stringify(body) })
        .then(() => {
            closeModal('modal-detalle');
            toast('Detalle registrado');
            loadDetalles();
        })
        .catch(e => toast(e.message, 'error'));
}

function deleteDetalle(id) {
    if (!confirm(`¿Eliminar el detalle #${id}?`)) return;
    apiFetch(`${API}/api/movimiento_detalle/${id}`, { method: 'DELETE' })
        .then(() => { toast('Detalle eliminado'); loadDetalles(); })
        .catch(e => toast(e.message, 'error'));
}

// ===== AUDITORÍA =====
let _auditoriaData = [];

function loadAuditoria() {
    hideTable('auditoria');
    apiFetch(`${API}/api/auditoria`)
        .then(data => {
            _auditoriaData = data;
            renderAuditoria(data);
        })
        .catch(e => toast(e.message, 'error'));
}

function renderAuditoria(data) {
    const opCls = { INSERT: 'bg', UPDATE: 'by', DELETE: 'br' };
    $('tbody-auditoria').innerHTML = data.length === 0
        ? `<tr><td colspan="7" style="text-align:center;padding:32px;color:var(--gray-400)">No hay registros que coincidan con los filtros</td></tr>`
        : data.map(a => {
            const valAntes = a.valorAnterior || a.valor_anterior || null;
            const valDespues = a.valorNuevo || a.valor_nuevo || null;
            const usuario = a.usuarioNombre || a.usuario?.nombre || '—';
            return `<tr>
        <td class="id">${a.id}</td>
        <td>${badge(a.entidad, 'bb')}</td>
        <td>${badge(a.operacion, opCls[a.operacion] || 'bb')}</td>
        <td style="font-size:12px;white-space:nowrap">${a.fecha ? new Date(a.fecha).toLocaleString('es-CO') : '—'}</td>
        <td style="font-size:12px;color:var(--gray-500)">
          ${valAntes
                    ? `<div style="background:var(--red-bg);border:1px solid #FECACA;border-radius:6px;padding:6px 10px;line-height:1.6;word-break:break-word">${valAntes}</div>`
                    : '<span style="color:var(--gray-300)">—</span>'}
        </td>
        <td style="font-size:12px">
          ${valDespues
                    ? `<div style="background:var(--green-bg);border:1px solid #6EE7B7;border-radius:6px;padding:6px 10px;line-height:1.6;word-break:break-word">${valDespues}</div>`
                    : '<span style="color:var(--gray-300)">—</span>'}
        </td>
        <td style="font-size:12px;font-weight:500">${usuario}</td>
      </tr>`;
        }).join('');
    showTable('auditoria');
}

function filtrarAuditoria() {
    const idFiltro = val('auditoria-buscar-id').trim();
    const entidadFiltro = val('auditoria-filtro-entidad');
    const operacionFiltro = val('auditoria-filtro-operacion');

    const resultado = _auditoriaData.filter(a => {
        const coincideId = !idFiltro || String(a.id) === idFiltro;
        const coincideEntidad = !entidadFiltro || a.entidad === entidadFiltro;
        const coincideOperacion = !operacionFiltro || a.operacion === operacionFiltro;
        return coincideId && coincideEntidad && coincideOperacion;
    });

    renderAuditoria(resultado);
}

function limpiarFiltrosAuditoria() {
    $('auditoria-buscar-id').value = '';
    $('auditoria-filtro-entidad').value = '';
    $('auditoria-filtro-operacion').value = '';
    renderAuditoria(_auditoriaData);
}

document.querySelectorAll('.modal-overlay').forEach(o => {
    o.addEventListener('click', e => { if (e.target === o) o.classList.remove('open'); });
});

// ===== BUSCAR POR ID =====
const endpointMap = {
    usuarios: { url: '/api/usuario', render: renderUsuarioRow },
    bodegas: { url: '/api/bodega', render: renderBodegaRow },
    productos: { url: '/api/producto', render: renderProductoRow },
    movimientos: { url: '/api/movimiento', render: renderMovimientoRow },
    detalles: { url: '/api/movimiento_detalle', render: renderDetalleRow }
};

function buscarPorId(seccion, valor) {
    if (!valor) { limpiarBusqueda(seccion); return; }
    const id = parseInt(valor);
    if (isNaN(id) || id < 1) return;
    const { url, render } = endpointMap[seccion];
    hideTable(seccion);
    apiFetch(`${API}${url}/${id}`)
        .then(data => {
            $(`tbody-${seccion}`).innerHTML = render(data);
            showTable(seccion);
        })
        .catch(() => {
            $(`tbody-${seccion}`).innerHTML = `<tr><td colspan="10" style="text-align:center;padding:32px;color:var(--gray-400)">No se encontró ningún registro con ID ${id}</td></tr>`;
            showTable(seccion);
        });
}

function limpiarBusqueda(seccion) {
    const input = $(`buscar-id-${seccion}`);
    if (input) input.value = '';
    loadPage(seccion);
}

function renderUsuarioRow(u) {
    return `<tr>
    <td class="id">${u.id}</td><td>${u.nombre}</td><td>${u.documento}</td><td>${u.username}</td>
    <td>${badge(u.rol, u.rol === 'ADMIN' ? 'by' : 'bb')}</td>
    <td>${isAdmin()
            ? rowActions(`editUsuario(${u.id},'${u.nombre}','${u.documento}','${u.username}','${u.rol}')`, `deleteUsuario(${u.id})`)
            : '<span style="font-size:12px;color:var(--gray-400)">Sin permisos</span>'}</td>
  </tr>`;
}

function renderBodegaRow(b) {
    return `<tr>
    <td class="id">${b.id}</td><td>${b.nombre}</td><td>${b.ubicacion}</td>
    <td>${b.capacidad.toLocaleString()}</td><td>${b.usuario?.nombre || '—'}</td>
    <td>${rowActions(`editBodega(${b.id},'${b.nombre}','${b.ubicacion}',${b.capacidad},${b.usuario?.id})`, `deleteBodega(${b.id})`)}</td>
  </tr>`;
}

function renderProductoRow(p) {
    const stockStyle = p.stock === 0 ? 'color:var(--red);font-weight:700'
        : p.stock < 10 ? 'color:var(--amber);font-weight:600'
            : 'color:var(--green);font-weight:600';
    return `<tr>
    <td class="id">${p.id}</td>
    <td style="font-weight:500">${p.nombre}</td>
    <td>${badge(p.categoria, 'bb')}</td>
    <td style="font-weight:500">$${p.precio.toLocaleString('es-CO')}</td>
    <td><span style="${stockStyle}">${p.stock}</span> <span style="font-size:11px;color:var(--gray-400)">uds</span></td>
    <td>${p.bodega?.nombre || '—'}</td>
    <td style="font-size:12px;color:var(--gray-500)">${p.bodega?.ubicacion || '—'}</td>
    <td style="font-size:12px;color:var(--gray-500)">${p.bodega?.usuario?.nombre || '—'}</td>
    <td>${rowActions(`editProducto(${p.id},'${p.nombre}','${p.categoria}',${p.precio},${p.stock},${p.bodega?.id})`, `deleteProducto(${p.id})`)}</td>
  </tr>`;
}

function renderMovimientoRow(m) {
    return `<tr>
    <td class="id">${m.id}</td>
    <td>${new Date(m.fecha).toLocaleDateString('es-CO')}</td>
    <td>${tipoBadge(m.tipoMovimiento)}</td>
    <td>${m.usuario?.nombre || '—'}</td>
    <td>${m.bodegaOrigen?.nombre || '—'}</td>
    <td>${m.bodegaDestino?.nombre || '—'}</td>
    <td>${rowActions(null, `deleteMovimiento(${m.id})`)}</td>
  </tr>`;
}

function renderDetalleRow(d) {
    return `<tr>
    <td class="id">${d.id}</td>
    <td style="font-weight:600">${d.cantidad} <span style="font-size:11px;color:var(--gray-400)">uds</span></td>
    <td>${tipoBadge(d.movimiento?.tipoMovimiento)}</td>
    <td style="font-size:12px;color:var(--gray-500)">${d.movimiento?.fecha ? new Date(d.movimiento.fecha).toLocaleDateString('es-CO') : '—'}</td>
    <td style="font-weight:500">${d.producto?.nombre || '—'}</td>
    <td>${d.producto?.categoria ? badge(d.producto.categoria, 'bb') : '—'}</td>
    <td><span style="font-size:12px;font-weight:600;color:var(--gray-700)">${d.producto?.stock ?? '—'}</span> <span style="font-size:11px;color:var(--gray-400)">uds</span></td>
    <td>${rowActions(null, `deleteDetalle(${d.id})`)}</td>
  </tr>`;
}

// ===== STOCK BAJO =====
let _stockBajoActivo = false;

function toggleStockBajo() {
    _stockBajoActivo = !_stockBajoActivo;
    const btn = $('btn-stock-bajo');
    if (_stockBajoActivo) {
        btn.style.background = 'var(--amber)';
        btn.style.color = '#fff';
        btn.style.borderColor = 'var(--amber)';
        loadStockBajo();
    } else {
        btn.style.background = 'var(--amber-bg)';
        btn.style.color = 'var(--amber)';
        btn.style.borderColor = 'var(--amber)';
        loadProductos();
    }
}

function loadStockBajo() {
    hideTable('productos');
    apiFetch(`${API}/api/producto/stock_bajo`)
        .then(data => {
            if (data.length === 0) {
                $('tbody-productos').innerHTML = `<tr><td colspan="9" style="text-align:center;padding:32px;color:var(--gray-400)">
          <span style="font-size:24px">✓</span><br>No hay productos con stock bajo. ¡Todo en orden!
        </td></tr>`;
            } else {
                $('tbody-productos').innerHTML = data.map(p => {
                    const stockStyle = p.stock === 0
                        ? 'color:var(--red);font-weight:700'
                        : 'color:var(--amber);font-weight:600';
                    return `<tr style="background:var(--amber-bg)">
            <td class="id">${p.id}</td>
            <td style="font-weight:500">${p.nombre}</td>
            <td>${badge(p.categoria, 'bb')}</td>
            <td style="font-weight:500">$${p.precio.toLocaleString('es-CO')}</td>
            <td><span style="${stockStyle}">${p.stock}</span> <span style="font-size:11px;color:var(--gray-400)">uds</span>
              ${p.stock === 0 ? '<span class="badge br" style="margin-left:4px">Sin stock</span>' : '<span class="badge by" style="margin-left:4px">Bajo</span>'}
            </td>
            <td>${p.bodega?.nombre || '—'}</td>
            <td style="font-size:12px;color:var(--gray-500)">${p.bodega?.ubicacion || '—'}</td>
            <td style="font-size:12px;color:var(--gray-500)">${p.bodega?.usuario?.nombre || '—'}</td>
            <td>${rowActions(`editProducto(${p.id},'${p.nombre}','${p.categoria}',${p.precio},${p.stock},${p.bodega?.id})`, `deleteProducto(${p.id})`)}</td>
          </tr>`;
                }).join('');
            }
            showTable('productos');
            toast(`${data.length} producto(s) con stock bajo`, data.length > 0 ? 'error' : 'ok');
        })
        .catch(e => toast(e.message, 'error'));
}

//Nuevo
// ===== SIDEBAR MÓVIL =====
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.getElementById('sidebar-overlay');
    sidebar.classList.toggle('open');
    overlay.classList.toggle('open');
    document.body.style.overflow = sidebar.classList.contains('open') ? 'hidden' : '';
}

function closeSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.getElementById('sidebar-overlay');
    sidebar.classList.remove('open');
    overlay.classList.remove('open');
    document.body.style.overflow = '';
}

// Cierra el sidebar al navegar en móvil
const _navigateOrig = navigate;
// Inyectar cierre automático en navigate
document.querySelectorAll('.sb-item').forEach(item => {
    item.addEventListener('click', () => {
        if (window.innerWidth <= 768) closeSidebar();
    });
});