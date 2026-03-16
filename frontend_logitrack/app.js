const API = 'http://172.16.41.25:8080';

document.getElementById('topbar-date').textContent = new Date().toLocaleDateString('es-CO', { weekday:'long', year:'numeric', month:'long', day:'numeric' });
document.addEventListener('keydown', e => { if (e.key==='Enter' && document.getElementById('login-screen').style.display!=='none') doLogin(); });

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
