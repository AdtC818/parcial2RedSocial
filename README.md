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

