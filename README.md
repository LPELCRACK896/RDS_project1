# Cliente de Mensajería Instantánea XMPP
Este proyecto implementa un cliente de mensajería instantánea que soporta el protocolo XMPP con una interfaz de consola (CLI).

## Funcionalidades
### Administración de cuentas:

1. Registrar una nueva cuenta en el servidor.
2. Iniciar sesión con una cuenta.
3. Cerrar sesión con una cuenta.
4. Eliminar la cuenta del servidor.

### Comunicación:

1. Mostrar todos los usuarios/contactos y su estado.
2. Agregar un usuario a los contactos.
3. Mostrar detalles de contacto de un usuario.
4. Comunicación 1 a 1 con cualquier usuario/contacto.
5. Participar en conversaciones grupales.
6. Definir mensaje de presencia.
7. Enviar/recibir notificaciones.
8. Enviar/recibir archivos (Nota: Solo se maneja la transferencia de metadatos, no la visualización de imágenes).
## Requisitos
- Java 11 o superior.
- Maven para gestionar las dependencias.
## Instrucciones de Instalación
1. Clonar el repositorio:
```
git clone https://github.com/LPELCRACK896/RDS_project1.git
```
2. Navegar al directorio del proyecto
```
cd src
```
3. Instalar las dependencias con Maven:
```
mvn install
```
## Uso
Para iniciar el cliente de mensajería, ejecute:

```
java -jar target/nombre-del-archivo-jar.jar
```
Siga las instrucciones en la consola para acceder a las diversas funcionalidades del cliente.

## Contribución
Si deseas contribuir al proyecto, por favor, realiza un "fork" del repositorio, crea una nueva rama, realiza tus cambios y envía un "pull request".

