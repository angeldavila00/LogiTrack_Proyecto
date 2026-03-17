# LogiTrack Frontend

### Panel de Administración — Sistema de Gestión de Logística

> Interfaz web para la administración de bodegas, productos, movimientos de inventario y auditoría del sistema LogiTrack.

![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6+-F7DF1E?logo=javascript&logoColor=black)
![JWT](https://img.shields.io/badge/JWT-Auth-5B21B6?logo=jsonwebtokens&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-API-6DB33F?logo=springboot&logoColor=white)

---

## Tabla de Contenido

1. [Introducción](#1-introducción)
2. [Caso de Uso del Frontend](#2-caso-de-uso-del-frontend)
3. [Descripción del Proyecto](#3-descripción-del-proyecto)
4. [Requerimientos Funcionales](#4-requerimientos-funcionales)
5. [Estructura de Archivos](#5-estructura-de-archivos)
6. [Arquitectura del Frontend](#6-arquitectura-del-frontend)
7. [Módulos del Sistema](#7-módulos-del-sistema)
8. [Configuración e Instalación](#8-configuración-e-instalación)
9. [Funcionalidades por Rol](#9-funcionalidades-por-rol)
10. [Endpoints Consumidos](#10-endpoints-consumidos)
11. [Conclusión](#11-conclusión)

---

## 1. Introducción

**LogiTrack Frontend** es una aplicación web de página única (SPA) desarrollada con HTML5, CSS3 y JavaScript vanilla, diseñada para consumir la API REST del sistema LogiTrack. Proporciona una interfaz de usuario moderna, limpia y empresarial para la gestión completa de bodegas, productos, movimientos de inventario y auditoría de operaciones.

El objetivo principal del frontend es ofrecer una experiencia de usuario fluida y eficiente, permitiendo a los administradores y empleados interactuar con el sistema sin necesidad de conocimientos técnicos. La interfaz se conecta directamente con el backend a través de peticiones HTTP autenticadas mediante tokens JWT.

---

## 2. Caso de Uso del Frontend

La empresa **LogiTrack S.A.** necesitaba una interfaz gráfica que permitiera a su equipo operar el sistema de gestión de bodegas sin acceder directamente a la API. El frontend resuelve los siguientes problemas:

- Visualización centralizada de todos los datos del sistema en un solo panel
- Control de acceso basado en roles: **ADMIN** y **EMPLEADO**
- Formularios intuitivos para crear, editar y eliminar registros
- Retroalimentación en tiempo real mediante notificaciones toast
- Filtrado y búsqueda rápida de registros por ID y atributos
- Registro automático de auditorías desde el backend al realizar operaciones

---

## 3. Descripción del Proyecto

Este proyecto es un cliente web de página única que se comunica con el backend LogiTrack. Está desarrollado sin frameworks de JavaScript, usando únicamente HTML5, CSS3 con variables personalizadas y JavaScript ES6+ con Fetch API.

La aplicación está compuesta por tres archivos principales:

| Archivo      | Descripción                 | Responsabilidad                            |
| ------------ | --------------------------- | ------------------------------------------ |
| `Index.html` | Estructura HTML del sistema | Layout, modales, tablas y formularios      |
| `app.js`     | Lógica de la aplicación     | Peticiones API, manejo de estado, permisos |
| `css.css`    | Estilos y diseño visual     | Variables CSS, componentes, responsividad  |

---

## 4. Requerimientos Funcionales

### 4.1 Autenticación

- Formulario de inicio de sesión con usuario y contraseña
- Registro de nuevas cuentas con validación de campos
- Almacenamiento del token JWT en `localStorage`
- Decodificación del token para obtener el rol del usuario
- Cierre de sesión con limpieza del `localStorage`

### 4.2 Gestión de Usuarios

- Listar todos los usuarios registrados en el sistema
- Crear nuevos usuarios _(solo ADMIN)_
- Editar información de usuarios existentes _(solo ADMIN)_
- Eliminar usuarios del sistema _(solo ADMIN)_
- Buscar usuario por ID

### 4.3 Gestión de Bodegas

- Listar bodegas con nombre, ubicación, capacidad y encargado
- Crear, editar y eliminar bodegas
- Selección de encargado desde lista desplegable con datos reales
- Buscar bodega por ID

### 4.4 Gestión de Productos

- Listar productos con nombre, categoría, precio, stock, bodega y encargado
- Filtro de productos con stock bajo (`stock < 10`)
- Crear, editar y eliminar productos
- Buscar producto por ID

### 4.5 Gestión de Movimientos

- Listar movimientos con tipo, fecha, usuario y bodegas
- Crear movimientos con lista dinámica de productos y cantidades
- Validación de stock antes de registrar el movimiento
- Eliminación de movimientos con recuperación automática de stock
- Buscar movimiento por ID

### 4.6 Detalle de Movimientos

- Listar detalles con cantidad, tipo de movimiento, fecha, producto y stock actual
- Eliminar detalles individualmente
- Buscar detalle por ID

### 4.7 Auditoría

- Visualización de todos los registros de auditoría
- Filtros por ID, entidad y tipo de operación
- Valores anteriores y nuevos con códigos de color
- **Solo lectura**: no permite crear, editar ni eliminar registros

---

## 5. Estructura de Archivos

```
logitrack-frontend/
├── Index.html        → Estructura principal de la aplicación
├── app.js            → Lógica JavaScript (API, permisos, navegación)
└── css.css           → Estilos globales y variables de diseño
```

El archivo `Index.html` referencia los archivos `app.js` y `css.css` mediante etiquetas `<link>` y `<script>` respectivamente. No requiere servidor de archivos estáticos; puede abrirse directamente en el navegador.

---

## 6. Arquitectura del Frontend

### 6.1 Flujo de la aplicación

```
Usuario abre Index.html
        |
        v
Pantalla de Login / Registro
        |
        v  (JWT guardado en localStorage)
Dashboard principal
        |
        v
Sidebar con módulos → carga datos desde API
        |
        v
Modales para CRUD → peticiones apiFetch()
        |
        v
Toast de confirmación / error
```

### 6.2 Función `apiFetch`

Todas las peticiones al backend pasan por la función `apiFetch()`, que centraliza:

- Lectura del token JWT desde `localStorage`
- Inclusión del header `Authorization: Bearer {token}`
- Manejo de errores HTTP (401, 403, 400, 500)
- Parseo de errores de validación del `GlobalExceptionHandler`
- Soporte para respuestas `204 No Content`

### 6.3 Sistema de permisos por rol

Al iniciar sesión, el token JWT se decodifica para extraer el rol del usuario. Este rol se guarda en `localStorage` y controla la visibilidad de botones y acciones:

---

## 7. Módulos del Sistema

### 7.1 Login y Registro

La pantalla de autenticación presenta dos pestañas:

- **Iniciar sesión**: envía usuario y contraseña al endpoint `POST /auth/login`
- **Crear cuenta**: formulario de registro con validación en el cliente

El formulario de registro valida que todos los campos estén completos, que las contraseñas coincidan y tengan al menos 6 caracteres antes de enviar la petición.

### 7.2 Dashboard

Tras autenticarse, el usuario accede al panel principal que incluye:

- Barra lateral (sidebar) con navegación entre módulos
- Barra superior con título de la sección activa y fecha actual
- Tarjetas de estadísticas: usuarios, bodegas, productos y movimientos
- Avatar y nombre del usuario activo con botón de cierre de sesión

### 7.3 Tablas y Búsqueda

Cada módulo presenta una tabla con los registros del sistema. Las tablas incluyen:

- Columnas relevantes por entidad con formato visual mejorado
- Indicadores de color para stock de productos
- Badges de color para tipos de movimiento y roles
- Campo de búsqueda por ID con resultado inmediato
- Botones de acción por fila según permisos del usuario

### 7.4 Modales de Formulario

Los formularios de creación y edición se presentan en modales con:

- Selects poblados dinámicamente con datos reales de la API
- Validación en el cliente antes de enviar
- Cierre al hacer clic fuera del modal
- Limpieza de campos al abrir un nuevo registro

### 7.5 Registro de Movimientos

El formulario de movimientos incluye una sección especial para agregar productos dinámicamente:

- Botón **"+ Agregar producto"** añade una fila con select de producto y campo de cantidad
- El select de productos muestra nombre y stock disponible
- Al guardar, se envían todos los detalles en el mismo request al backend
- El backend valida el stock y la capacidad de la bodega destino

---

## 8. Configuración e Instalación

### 8.1 Requisitos previos

- Backend LogiTrack ejecutándose en `http://localhost:8080`
- Base de datos MySQL con la estructura del proyecto
- Navegador web moderno (Chrome, Firefox, Edge)

### 8.2 Pasos de instalación

1. Descargar los archivos del frontend (`Index.html`, `app.js`, `css.css`)
2. Verificar que el backend esté corriendo en el puerto `8080`
3. Abrir `Index.html` directamente en el navegador
4. Iniciar sesión con las credenciales configuradas en la base de datos

### 8.3 Configuración CORS en el backend

Para que el frontend funcione al abrirse como archivo local, el backend debe permitir el origen `null`:

```java
// SecurityConfig.java
config.setAllowedOrigins(List.of(
  "http://127.0.0.1:5500",
  "http://localhost:5500",

));
```

### 8.4 Cambiar URL del backend

Si el backend corre en un puerto o host diferente, modificar la constante `API` en `app.js`:

```javascript
// app.js - línea 1
const API = "http://localhost:8080"; // cambiar según el entorno
```

---

## 9. Funcionalidades por Rol

| Acción               | ADMIN | EMPLEADO |
| -------------------- | ----- | -------- |
| Ver usuarios         | ✅    | ✅       |
| Crear usuario        | ✅    | ❌       |
| Editar usuario       | ✅    | ❌       |
| Eliminar usuario     | ✅    | ❌       |
| Gestionar bodegas    | ✅    | ✅       |
| Gestionar productos  | ✅    | ✅       |
| Crear movimientos    | ✅    | ✅       |
| Eliminar movimientos | ✅    | ✅       |
| Ver auditoría        | ✅    | ✅       |

> El rol se extrae directamente del token JWT al iniciar sesión. No requiere configuración adicional en el servidor.

---

## 10. Endpoints Consumidos

### 10.1 Autenticación

| Método | Endpoint         | Descripción                             |
| ------ | ---------------- | --------------------------------------- |
| `POST` | `/auth/login`    | Inicia sesión y devuelve el token JWT   |
| `POST` | `/auth/registro` | Registra un nuevo usuario en el sistema |

### 10.2 Usuarios

| Método   | Endpoint            | Descripción              |
| -------- | ------------------- | ------------------------ |
| `GET`    | `/api/usuario`      | Lista todos los usuarios |
| `GET`    | `/api/usuario/{id}` | Busca un usuario por ID  |
| `POST`   | `/api/usuario`      | Crea un nuevo usuario    |
| `PUT`    | `/api/usuario/{id}` | Actualiza un usuario     |
| `DELETE` | `/api/usuario/{id}` | Elimina un usuario       |

### 10.3 Bodegas

| Método   | Endpoint           | Descripción             |
| -------- | ------------------ | ----------------------- |
| `GET`    | `/api/bodega`      | Lista todas las bodegas |
| `GET`    | `/api/bodega/{id}` | Busca una bodega por ID |
| `POST`   | `/api/bodega`      | Crea una nueva bodega   |
| `PUT`    | `/api/bodega/{id}` | Actualiza una bodega    |
| `DELETE` | `/api/bodega/{id}` | Elimina una bodega      |

### 10.4 Productos

| Método   | Endpoint                   | Descripción                    |
| -------- | -------------------------- | ------------------------------ |
| `GET`    | `/api/producto`            | Lista todos los productos      |
| `GET`    | `/api/producto/{id}`       | Busca un producto por ID       |
| `GET`    | `/api/producto/stock_bajo` | Productos con stock menor a 10 |
| `POST`   | `/api/producto`            | Crea un nuevo producto         |
| `PUT`    | `/api/producto/{id}`       | Actualiza un producto          |
| `DELETE` | `/api/producto/{id}`       | Elimina un producto            |

### 10.5 Movimientos

| Método   | Endpoint               | Descripción                         |
| -------- | ---------------------- | ----------------------------------- |
| `GET`    | `/api/movimiento`      | Lista todos los movimientos         |
| `GET`    | `/api/movimiento/{id}` | Busca un movimiento por ID          |
| `POST`   | `/api/movimiento`      | Crea un movimiento con detalles     |
| `DELETE` | `/api/movimiento/{id}` | Elimina movimiento y restaura stock |

### 10.6 Detalle de Movimientos

| Método   | Endpoint                       | Descripción              |
| -------- | ------------------------------ | ------------------------ |
| `GET`    | `/api/movimiento_detalle`      | Lista todos los detalles |
| `GET`    | `/api/movimiento_detalle/{id}` | Busca un detalle por ID  |
| `DELETE` | `/api/movimiento_detalle/{id}` | Elimina un detalle       |

### 10.7 Auditoría

| Método | Endpoint              | Descripción                            |
| ------ | --------------------- | -------------------------------------- |
| `GET`  | `/api/auditoria`      | Lista todos los registros de auditoría |
| `GET`  | `/api/auditoria/{id}` | Busca un registro de auditoría por ID  |

---

## 11. Conclusión

El frontend de LogiTrack representa una solución práctica y eficiente para la gestión operativa de bodegas e inventarios. Al estar desarrollado con tecnologías web estándar sin dependencias externas, es fácil de mantener, desplegar y extender.

La arquitectura basada en un archivo JavaScript centralizado (`app.js`) con funciones bien definidas por módulo permite escalar el sistema fácilmente. La integración directa con la API REST del backend garantiza que los datos siempre estén actualizados y que las operaciones se reflejen en tiempo real.

El sistema de permisos basado en roles JWT garantiza que cada usuario solo pueda realizar las operaciones que le corresponden, sin necesidad de configuración adicional en el servidor. El registro automático de auditorías desde el backend complementa la trazabilidad del sistema sin carga adicional para el usuario.

---

_© 2026 LogiTrack — Sistema de Gestión de Logística_
