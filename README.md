GUÍA COMPLETA PARA EJECUTAR LA APLICACIÓN RED SOCIAL
REQUISITOS PREVIOS

Windows (la guía está optimizada para Windows)
Java 21 instalado
Git instalado


PARTE 1: INSTALACIÓN DE XAMPP Y MARIADB
1.1 Descargar e Instalar XAMPP

Ve a https://www.apachefriends.org/
Descarga XAMPP para Windows
Ejecuta como Administrador
Instala en la ruta por defecto (C:\xampp)
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

Selecciona base red_social_parcial_2 (menú izquierdo)
Ve a pestaña "SQL"
Ejecuta este código:

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

  PARTE 2: INSTALACIÓN DE MONGODB
2.1 Descargar MongoDB Community Server

Ve a https://www.mongodb.com/try/download/community
Selecciona:

Version: Última disponible
Platform: Windows
Package: MSI


Descarga e instala con configuración por defecto
IMPORTANTE: Durante instalación marca "Install MongoDB as a Service"

2.2 Descargar MongoDB Compass

Ve a https://www.mongodb.com/try/download/compass
Descarga MongoDB Compass
Instala con configuración por defecto

2.3 Configurar MongoDB

Abre MongoDB Compass
Conecta a: mongodb://localhost:27017
Crea base de datos:

Database name: red_social_hobbies
Collection name: usuario_hobbies

PARTE 3: CLONAR Y EJECUTAR EL PROYECTO
3.1 Clonar Repositorio

  git clone [URL_DEL_REPOSITORIO]
  cd [NOMBRE_DEL_PROYECTO]

3.2 Configurar application.properties
Verificar que src/main/resources/application.properties contenga:

  spring.application.name=red-social-parcial-2

  spring.datasource.url=jdbc:mariadb://localhost:3306/red_social_parcial_2
  spring.datasource.username=red_social_parcial_2
  spring.datasource.password=red_social_123
  spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
  
  spring.data.mongodb.uri=mongodb://localhost:27017/red_social_hobbies
  
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  
  spring.data.jpa.repositories.enabled=true
  spring.data.mongodb.repositories.enabled=true

3.3 Ejecutar Aplicación
  ./mvnw spring-boot:run

3.4 Acceder a la Aplicación

Navegar a http://localhost:8080
Usuarios de prueba:

pedro@email.com / pedro123
nicolas@email.com / nico456
sergio@email.com / sergio789
santiago@email.com / santi2024

VERIFICACIÓN DE FUNCIONAMIENTO

✅ Login funciona
✅ Dashboard muestra pestañas Amigos/Hobbies
✅ MongoDB Compass muestra documentos en usuario_hobbies
✅ PhpMyAdmin muestra usuarios en tabla usuario
