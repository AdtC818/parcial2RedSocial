🌐 RED SOCIAL UPTC - GUÍA COMPLETA DE INSTALACIÓN
📋 DESCRIPCIÓN DEL PROYECTO
Red Social desarrollada con Spring Boot que utiliza 3 bases de datos diferentes:

MariaDB: Gestión de usuarios y eventos
MongoDB: Almacenamiento de hobbies de usuarios
Neo4j: Manejo de relaciones de amistad (grafos)

🛠️ REQUISITOS PREVIOS

Windows (guía optimizada para Windows)
Java 21 instalado y configurado
Git instalado
Conexión a internet para descargar dependencias


📦 PARTE 1: INSTALACIÓN DE XAMPP Y MARIADB
1.1 Descargar e Instalar XAMPP

Ve a https://www.apachefriends.org/
Descarga XAMPP para Windows
Ejecuta como Administrador
Instala en la ruta por defecto: C:\xampp
Abre XAMPP Control Panel
Inicia Apache y MySQL (botones Start)

1.2 Crear Base de Datos y Usuario

Abre navegador y ve a http://localhost/phpmyadmin
Clic en "Cuentas de usuario" (pestaña superior)
Clic en "Agregar cuenta de Usuario"
Llenar formulario:

Nombre de usuario: red_social_parcial_2
Host: localhost
Contraseña: red_social_123
✅ Marcar: "Crear base de datos con el mismo nombre y otorgar todos los privilegios"


Clic "Continuar"

1.3 Crear Tabla de Usuarios

  CREATE TABLE usuario (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      email VARCHAR(100) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL,
      nombre VARCHAR(100) NOT NULL,
      foto_url VARCHAR(500) DEFAULT 'https://via.placeholder.com/150x150?text=Usuario'
  );
  
  INSERT INTO usuario (email, password, nombre, foto_url) VALUES 
  ('pedro@email.com', 'pedro123', 'Pedro', 'https://via.placeholder.com/150x150?text=Pedro'),
  ('nicolas@email.com', 'nico456', 'Nicolas', 'https://via.placeholder.com/150x150?text=Nicolas'),
  ('sergio@email.com', 'sergio789', 'Sergio', 'https://via.placeholder.com/150x150?text=Sergio'),
  ('santiago@email.com', 'santi2024', 'Santiago', 'https://via.placeholder.com/150x150?text=Santiago');

🍃 PARTE 2: INSTALACIÓN DE MONGODB
2.1 Descargar MongoDB Community Server

Ve a https://www.mongodb.com/try/download/community
Selecciona:

Version: Última disponible (7.0+)
Platform: Windows
Package: MSI


Descarga e instala con configuración por defecto
⚠️ IMPORTANTE: Durante instalación marca "Install MongoDB as a Service"

2.2 Verificar Instalación de MongoDB

Abre Command Prompt como administrador
Ejecuta: mongod --version
Debe mostrar la versión instalada

2.3 Descargar MongoDB Compass (Interfaz Gráfica)

Ve a https://www.mongodb.com/try/download/compass
Descarga MongoDB Compass
Instala con configuración por defecto

2.4 Configurar MongoDB

Abre MongoDB Compass
Conecta a: mongodb://localhost:27017
Crea base de datos:

Database name: red_social_hobbies
Collection name: usuario_hobbies




🔗 PARTE 3: INSTALACIÓN DE NEO4J (BASE DE DATOS DE GRAFOS)
3.1 Descargar Neo4j Desktop

Ve a https://neo4j.com/download/
Registra una cuenta gratuita
Descarga Neo4j Desktop para Windows
Instala con configuración por defecto

3.2 Configurar Neo4j

Abre Neo4j Desktop
Clic en "New Project"
Dale nombre: Red Social UPTC
Clic en "Add Database" > "Local DBMS"
Configurar:

Name: red-social-db
Password: 12345678
Version: Usar la más reciente (5.x)


Clic "Create"

3.3 Iniciar Base de Datos Neo4j

En el proyecto creado, clic en "Start" en la base de datos
Espera a que aparezca "Active"
Clic en "Open" para abrir Neo4j Browser
Conecta con:

Username: neo4j
Password: 12345678



3.4 Verificar Conexión Neo4j
En Neo4j Browser, ejecuta:

  RETURN "Hello, Neo4j!" as greeting

Debe mostrar el saludo correctamente.

🚀 PARTE 4: CLONAR Y EJECUTAR EL PROYECTO
4.1 Clonar Repositorio

  git clone [URL_DEL_REPOSITORIO]
  cd [NOMBRE_DEL_PROYECTO]

4.2 Verificar Configuración application.properties
Asegúrate que src/main/resources/application.properties contenga exactamente:

  spring.application.name=red-social-parcial-2
  
  # Configuración MariaDB (usuarios y eventos)
  spring.datasource.url=jdbc:mariadb://localhost:3306/red_social_parcial_2
  spring.datasource.username=red_social_parcial_2
  spring.datasource.password=red_social_123
  spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
  
  # Configuración MongoDB (hobbies)
  spring.data.mongodb.uri=mongodb://localhost:27017/red_social_hobbies
  
  # Configuración Neo4j (relaciones de amigos)
  spring.neo4j.uri=neo4j://localhost:7687
  spring.neo4j.authentication.username=neo4j
  spring.neo4j.authentication.password=12345678
  
  # JPA/Hibernate
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  
  # Configuración específica para repositorios
  spring.data.jpa.repositories.enabled=true
  spring.data.mongodb.repositories.enabled=true
  spring.data.neo4j.repositories.enabled=true
  
  # Configuración específica Neo4j para Spring Boot 3.x
  spring.neo4j.transaction-type=auto
  spring.neo4j.pool.log-leaked-sessions=false

4.3 Verificar que las 3 Bases de Datos estén Activas
Antes de ejecutar, confirma que estén funcionando:
✅ MariaDB:

XAMPP Control Panel → MySQL debe mostrar "Running"

✅ MongoDB:

MongoDB Compass debe conectar a mongodb://localhost:27017

✅ Neo4j:

Neo4j Desktop → tu base debe mostrar "Active"

4.4 Ejecutar Aplicación
  ./mvnw spring-boot:run

Si es la primera vez, la descarga de dependencias puede tomar 5-10 minutos.
4.5 Verificar Inicialización Correcta
En los logs, debes ver estos mensajes:

  Creando hobbies de prueba...
  Intentando conectar a Neo4j...
  Neo4j conectado exitosamente
  Creando usuarios en Neo4j...
  Creando relaciones de amistad...
  Datos de Neo4j creados exitosamente
  Creando eventos de prueba...
  Eventos de prueba creados exitosamente: 6 eventos
  === APLICACIÓN INICIADA CORRECTAMENTE ===

🌐 PARTE 5: ACCEDER A LA APLICACIÓN
5.1 Abrir la Aplicación

Navegar a: http://localhost:8080
Aparecerá la pantalla de login

5.2 Usuarios de Prueba
  pedro@email.com / pedro123
  nicolas@email.com / nico456  
  sergio@email.com / sergio789
  santiago@email.com / santi2024

5.3 Funcionalidades Disponibles
📊 Dashboard Principal (después del login)

Pestaña Amigos: Visualización de red de amigos usando Neo4j
Pestaña Hobbies: Filtrado de amigos por hobbies comunes
Pestaña Eventos: Catálogo de eventos con filtros

👥 Sección Amigos

Grafo interactivo de relaciones de amistad
Los nodos se pueden arrastrar
Información de cada amigo al hacer hover

🎯 Sección Hobbies

Filtrado de amigos por intereses comunes
Visualización de hobbies por usuario
Grafo interactivo con información de hobbies

🎉 Sección Eventos

Catálogo visual de eventos disponibles
Filtros por:

Búsqueda: título, lugar, descripción
Categoría: Música, Deportes, Tecnología, etc.
Precio: Gratis vs De pago


Cards informativas con imagen, fecha, lugar, precio


✅ VERIFICACIÓN COMPLETA DEL FUNCIONAMIENTO
Checklist de Verificación:
🗄️ MariaDB

✅ Login funciona correctamente
✅ Tabla usuario contiene 4 usuarios de prueba
✅ Tabla evento se crea automáticamente con 6 eventos

🍃 MongoDB

✅ MongoDB Compass muestra base red_social_hobbies
✅ Colección usuario_hobbies contiene documentos de hobbies
✅ Filtros por hobby funcionan en la interfaz

🔗 Neo4j

✅ Neo4j Browser muestra 4 nodos de usuario
✅ Relaciones AMIGO_DE conectan los usuarios
✅ Grafo de amigos se visualiza correctamente en la web

🌐 Interfaz Web

✅ Dashboard carga con 3 pestañas
✅ Gráficos D3.js se renderizan correctamente
✅ Filtros de eventos funcionan
✅ Design responsive con colores pasteles


🔧 SOLUCIÓN DE PROBLEMAS COMUNES
Problema: "Error connecting to Neo4j"
Solución:

Verificar que Neo4j Desktop esté abierto
La base de datos debe mostrar "Active"
Verificar password: 12345678

Problema: "Connection refused MongoDB"
Solución:

Verificar que MongoDB esté corriendo como servicio
En Services.msc buscar "MongoDB" y verificar que esté "Running"

Problema: "Access denied MariaDB"
Solución:

Verificar que XAMPP MySQL esté iniciado
Confirmar credenciales en phpMyAdmin:

Usuario: red_social_parcial_2
Password: red_social_123



Problema: "Table 'evento' doesn't exist"
Solución:

La tabla se crea automáticamente. Si no existe, reinicia la aplicación.


📊 DATOS DE PRUEBA INCLUIDOS
👤 Usuarios (MariaDB)

Pedro, Nicolas, Sergio, Santiago

🎯 Hobbies (MongoDB)

Pedro: futbol, programacion, musica
Nicolas: programacion, videojuegos, lectura
Sergio: futbol, musica, cocina
Santiago: programacion, futbol, arte

👫 Amistades (Neo4j)

Pedro ↔ Nicolas
Pedro ↔ Sergio
Nicolas ↔ Santiago
Sergio ↔ Santiago

🎉 Eventos (MariaDB)

Concierto de Rock Nacional - Música - $45,000
Festival de Comida Colombiana - Gastronomía - Gratis
Conferencia de Tecnología - Tecnología - $80,000
Maratón de Bogotá 2025 - Deportes - $25,000
Feria de Arte y Artesanías - Arte - Gratis
Workshop de Fotografía Digital - Educación - $120,000

🏗️ ARQUITECTURA DEL PROYECTO

📁 src/main/java/myPacket/parcial_2/
├── 📁 controller/
│   └── AuthController.java          # Controlador principal con APIs REST
├── 📁 model/
│   ├── Usuario.java                 # Entidad JPA (MariaDB)
│   ├── Evento.java                  # Entidad JPA (MariaDB) 
│   ├── UsuarioHobbies.java          # Documento MongoDB
│   └── UsuarioNeo4j.java            # Nodo Neo4j
├── 📁 repository/
│   ├── 📁 jpa/
│   │   ├── UsuarioRepository.java   # Repositorio MariaDB
│   │   └── EventoRepository.java    # Repositorio MariaDB
│   ├── 📁 mongodb/
│   │   └── UsuarioHobbiesRepository.java # Repositorio MongoDB
│   └── 📁 neo4j/
│       └── UsuarioNeo4jRepository.java   # Repositorio Neo4j
├── 📁 service/
│   ├── UsuarioService.java          # Lógica de negocio usuarios/hobbies
│   ├── EventoService.java           # Lógica de negocio eventos
│   └── Neo4jDirectService.java      # Servicio directo Neo4j
└── MonolitoSpringbootApplication.java # Clase principal

Selecciona base red_social_parcial_2 (menú izquierdo)
Ve a pestaña "SQL"
Ejecuta este código:

🎯 FUNCIONALIDADES TÉCNICAS IMPLEMENTADAS

✅ Autenticación con sesiones HTTP
✅ Multi-base de datos (JPA + MongoDB + Neo4j)
✅ APIs REST para frontend dinámico
✅ Grafos interactivos con D3.js
✅ Filtros avanzados en tiempo real
✅ Design responsive con Bootstrap 5
✅ Inicialización automática de datos de prueba
✅ Manejo de errores y validaciones
