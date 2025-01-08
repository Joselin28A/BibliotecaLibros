# Biblioteca de Libros

## Descripción

Este proyecto es una aplicación para gestionar una biblioteca de libros, desarrollada en **Java** utilizando **Spring Boot** como framework, y **PostgreSQL** como base de datos. La aplicación se conecta a la API pública de [Gutendex](https://gutendex.com/) para obtener información sobre libros y autores, ofreciendo una variedad de funcionalidades para interactuar con los datos.

---

## Funcionalidades

La aplicación presenta las siguientes opciones para el usuario:

1. **Buscar libro por título**: Permite buscar un libro registrado desde la API o en la biblioteca por su título.
2. **Listar libros registrados**: Muestra una lista de todos los libros registrados en la biblioteca.
3. **Listar autores registrados**: Muestra una lista de los autores registrados en la biblioteca.
4. **Listar autores vivos en un determinado año**: Permite listar autores que estaban vivos en un año especificado por el usuario.
5. **Listar libros por idioma**: Muestra los libros registrados filtrados por idioma.
6. **Salir**: Finaliza la ejecución de la aplicación.

---

## Requisitos del Sistema

- **Java**: 17 o superior.
- **Spring Boot**: 3.4.1
- **PostgreSQL**: 15 o superior.
- Conexión a Internet (para consumir la API de Gutendex).

---

## Instalación

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/tu-repositorio.git
   cd tu-repositorio
   ```

2. **Configura la base de datos**
   - Crea una base de datos PostgreSQL.
   - Configura las credenciales en el archivo `application.properties` o `application.yml` en el directorio `src/main/resources`.
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost/literalura
     spring.datasource.username=tu_usuario
     spring.datasource.password=tu_contraseña
     spring.datasource.driver-class-name=org.postgresql.Driver
     hibernate.dialect=org.hibernate.dialect.HSQLDialect
     spring.jpa.hibernate.ddl-auto=update
     spring.main.allow-circular-references=true    
    spring.jpa.show-sql=true
    spring.jpa.format-sql=true
     
     ```

3. **Ejecuta la aplicación**
La aplicación se ejecuta desde la clase LiteraluraApplication

---

## Uso

Al iniciar la aplicación, se mostrará un menú con las opciones disponibles:

```text
--------------------------------------------------
1- Buscar libro por título
2- Listar libros registrados
3- Listar autores registrados
4- Listar autores vivos en un determinado año
5- Listar libros por idioma
0- Salir
----------------------------------------------------
Seleccione una opción:
```

### Ejemplo de Uso:
- **Buscar libro por título**: Ingresa el número `1` y luego escribe el título del libro que deseas buscar.
- **Listar libros registrados**: Ingresa el número `2` para ver todos los libros registrados en la biblioteca.
- **Salir**: Ingresa el número `0` para cerrar la aplicación.

---

## Autor

Desarrollado por [Jose Torres](https://github.com/Joselin28A)

. 🚀
