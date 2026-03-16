<h1 align="center">LogiTrack API</h1>
<h3 align="center">Sistema REST para Gestión y Auditoría de Bodegas</h3>

<p align="center">
  API desarrollada con Spring Boot para la administración de inventarios, 
  control de operaciones en bodegas y registro de auditorías dentro del sistema LogiTrack.
</p>

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.11-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8+-4479A1?logo=mysql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger%20OpenAPI-3-85EA2D?logo=swagger&logoColor=black)

</div>

## Tabla de Contenido

1. [Introducción](#1-introducción)  
2. [Caso de Estudio](#2-caso-de-estudio)  
3. [Descripción del proyecto](#3-descripción-del-proyecto)  
4. [Requerimientos funcionales del sistema](#4-requerimientos-funcionales-del-sistema)  
5. [Estructura del proyecto](#5-estructura-del-proyecto)  
6. [Arquitectura general del backend](#6-arquitectura-general-del-backend)  
7. [Modelo de datos del sistema](#7-modelo-de-datos-del-sistema)  
8. [Configuración e instalación](#8-configuración-e-instalación)  
9. [Documentación de endpoints](#9-documentación-de-endpoints)    
10. [Swagger y pruebas de la API](#10-swagger-y-pruebas-de-la-api)   
11. [Conclusión](#11-conclusión)

---

## 1. Introducción

**LogiTrack** es un sistema backend desarrollado con **Spring Boot**, orientado a la administración y control de bodegas dentro de una organización. El sistema permite gestionar productos, registrar movimientos de inventario y mantener un historial de auditoría que facilita el seguimiento de las operaciones realizadas en el sistema.

El objetivo principal del proyecto es **centralizar la información operativa** que anteriormente se gestionaba de forma manual. De esta manera se mejora la organización de los datos, se garantiza la trazabilidad de las operaciones y se facilita la consulta de la información a través de una **API REST**.

Para lograr una arquitectura clara y mantenible, el proyecto sigue una **estructura por capas**, compuesta por:

- Controladores  
- Servicios  
- Repositorios  
- Entidades  
- DTOs  
- Mappers  

En las siguientes secciones se describe el funcionamiento general del sistema **LogiTrack**, incluyendo la estructura del backend, las entidades principales, la configuración necesaria para su ejecución local y la documentación de los endpoints implementados para la gestión de:

- Usuarios  
- Bodegas  
- Productos  
- Movimientos de inventario  
- Registros de auditoría

## 2. Caso de Estudio

La empresa **LogiTrack S.A.** administra varias bodegas ubicadas en diferentes ciudades, donde se almacenan productos y se registran los movimientos de inventario. Antes de implementar este sistema, el control se realizaba mediante hojas de cálculo, lo que generaba problemas como duplicidad de información, baja trazabilidad y dificultad para auditar los cambios realizados por los usuarios.

Para solucionar esta situación, se propone el desarrollo de una **API REST centralizada** que permita gestionar de forma estructurada las bodegas, los productos y los movimientos de inventario, además de registrar auditorías sobre las modificaciones realizadas dentro del sistema.

Desde el punto de vista funcional, el backend debe permitir **operaciones CRUD, consultas con filtros y documentación clara de los endpoints**. Además, el sistema se plantea como una base que posteriormente puede ampliarse con mecanismos de seguridad, control de accesos y generación de reportes más avanzados.

**Problema:** control manual de Movimientos, poca trazabilidad y falta de auditoría centralizada.

**Solución:** desarrollo de un backend con **Spring Boot**, acceso mediante **endpoints REST**, persistencia en **MySQL** y documentación con **Swagger/OpenAPI**.

**Alcance actual del sistema:**
- Gestión de usuarios
- Administración de bodegas  
- Registro de productos  
- Movimientos de inventario  
- Consultas de auditoría

---

## 3. Descripción del proyecto

Este proyecto consiste en el desarrollo de una **API REST para la gestión de bodegas e Detalle de Movimientos** dentro del sistema **LogiTrack**. La aplicación está desarrollada en **Java 25 con Spring Boot**, utilizando tecnologías como **Spring Web, Spring Data JPA, Bean Validation, MySQL y Springdoc OpenAPI** para la documentación de la API.

La solución sigue una **arquitectura por capas**, donde cada componente tiene una responsabilidad específica. Las **entidades** representan la estructura de datos almacenada en la base de datos, los **DTOs** controlan la entrada y salida de información, los **mappers** se encargan de transformar entidades a DTOs y viceversa, los **repositorios** gestionan el acceso a datos y los **servicios** contienen la lógica principal del sistema.

De manera general, el sistema permite **registrar usuarios con roles**, **gestionar bodegas y sus encargados**, **asociar productos a cada bodega**, **registrar movimientos con sus detalle de movimientos** y **consultar los registros de auditoría** almacenados en la base de datos.
---
## 4. Requerimientos funcionales del sistema

1. **Gestión de Usuarios**  
   - Registro de nuevos usuarios con roles (admin, empleado).  
   - Consulta de usuarios registrados.  
   - Actualización y eliminación de usuarios.

2. **Administración de Bodegas**  
   - Creación de nuevas bodegas con información detallada.
    - Consulta de bodegas existentes.
    - Actualización y eliminación de bodegas.
3. **Gestión de Productos**
    - Registro de productos asociados a cada bodega.  
    - Consulta de productos por bodega.  
    - Actualización y eliminación de productos.
    - Stock menor a 10 de productos.
4. **Registro de Movimientos de Inventario** 
    - Creación de movimientos con detalle de productos y cantidades.  
    - Movimientos por bodega y fecha.  
    - Actualización y eliminación de movimientos.
5. **Registro de Detalle de Movimientos**
    - Registro de detalles asociados a cada movimiento.  
    - Consulta de detalles por movimiento.  
    - Actualización y eliminación de detalles.
6. **Auditoría de Operaciones**
    - Registro de auditorías para cada operación realizada.  
    - Consulta de auditorías por usuario, fecha y tipo de operación.  
    - Actualización y eliminación de registros de auditoría.

---

## 5. Estructura del proyecto



<h3 align=center>5.1 Evidencias desde Swagger UI</h3>

En esta subsección se ubicarán las capturas relacionadas con la documentación interactiva de la API en Swagger UI, donde se puede observar la estructura de los endpoints disponibles, los métodos HTTP implementados y las respuestas generadas por el sistema al ejecutar las peticiones.