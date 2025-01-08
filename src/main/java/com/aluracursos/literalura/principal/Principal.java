package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Datos;
import com.aluracursos.literalura.model.DatosAutor;
import com.aluracursos.literalura.model.DatosLibros;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repositorio.AutorRepositorio;
import com.aluracursos.literalura.repositorio.LibroRepositorio;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    @Autowired
    private ConsumoAPI consumoAPI;
    @Autowired
    private ConvierteDatos conversor;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private LibroRepositorio libroRepositorio;

    private final Scanner teclado;
    private boolean ejecutar;

    public Principal() {
        this.teclado = new Scanner(System.in);
        this.ejecutar = true;
    }

    private void mostrarOpciones() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("1- Buscar libro por título");
        System.out.println("2- Listar libros registrados");
        System.out.println("3- Listar autores registrados");
        System.out.println("4- Listar autores vivos en un determinado año");
        System.out.println("5- Listar libros por idioma");
        System.out.println("0- Salir");
        System.out.println("----------------------------------------------------");
        System.out.print("Seleccione una opción: \n");
    }

    private int mostarOpcionElegida() {
        int opcion = teclado.nextInt();
        teclado.nextLine();
        return opcion;
    }

    public void muestraElMenu() {
        while (ejecutar) {
            try {
                mostrarOpciones();
                int opcion = mostarOpcionElegida();
                procesarOpcion(opcion);
            } catch (Exception e) {
                System.out.println("Error al procesar: " + e.getMessage());
                teclado.nextLine();
            }
        }
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                buscarPorTitulo();
                break;
            case 2:
                librosRegistrados();
                break;
            case 3:
                autoresRegistrados();
                break;
            case 4:
                listarAutoresVivos();
                break;
            case 5:
                listarPorIdioma();
                break;
            case 0:
                salir();
                break;
            default:
                System.out.println("Opción no válida. Favor intente nuevamente.");
        }
    }

    private void buscarPorTitulo() {
        try {

            System.out.println("\n Ingrese el nombre del libro que desea buscar");
            String titulo = teclado.nextLine().trim();

            Optional<Libro> libro = libroRepositorio.findByTituloContainingIgnoreCase(titulo); //Buscamos en la BD
            if (libro.isPresent()) {
                System.out.println("Libro en la base de datos");
                System.out.println(libro.get());
                return;
            }

            System.out.println("Buscando en la API..."); //Buscamos en la API
            String url = URL_BASE + "?search=" + URLEncoder.encode(titulo, StandardCharsets.UTF_8);
            String json = consumoAPI.obtenerDatos(url);
            if (json == null || json.isEmpty()) {
                System.out.println("Sin respuesta de la API, internar nuevamente");
                return;
            }

            if (titulo.isEmpty()) {  //Si usuario no escribe nada
                System.out.println("Debe ingresar un titulo");
                return;
            }

            Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);
            if (datosBusqueda.resultados() == null || datosBusqueda.resultados().isEmpty()) {
                System.out.println("No se encuentra: " + titulo);
                return;

            }
            procesarResultados(datosBusqueda.resultados(), titulo);
        } catch (Exception e) {
            System.out.println("Error en la búsqueda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarResultados(List<DatosLibros> resultados, String titulo) {
        boolean encontrado = false;
        for (DatosLibros datosLibro : resultados) {
            if (datosLibro.titulo().toUpperCase().contains(titulo.toUpperCase())) {
                guardarLibroYAutor(datosLibro);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.out.println("No se encontró el libro: " + titulo);
        }
    }

    private void guardarLibroYAutor(DatosLibros datosLibros) {
        try {
            if (datosLibros.autor() == null || datosLibros.autor().isEmpty()) {
                System.out.println("El libro no tiene autor registrado.");
                return;
            }

            // Guardar o recuperar el autor
            DatosAutor datosAutor = datosLibros.autor().get(0);
            Autor autor = autorRepositorio.findByNombre(datosAutor.nombre())
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor(datosAutor);
                        System.out.println("Guardando nuevo autor: " + datosAutor.nombre());
                        return autorRepositorio.save(nuevoAutor);
                    });

            // Verificar si el libro ya existe
            if (libroRepositorio.findByTituloContainingIgnoreCase(datosLibros.titulo()).isPresent()) {
                System.out.println("El libro ya existe en la base de datos.");
                return;
            }

            // Guardar el nuevo libro
            Libro libro = new Libro(datosLibros, autor);
            Libro guardado = libroRepositorio.save(libro);
            System.out.println("\nLibro guardado con éxito:");
            System.out.println(guardado);

        } catch (Exception e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    private void librosRegistrados() {
        System.out.println("\n------LIBROS REGISTRADOS------");
        List<Libro> libros = libroRepositorio.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros en la base de datos.");
            return;
        }
        libros.forEach(System.out::println);
    }

    private void autoresRegistrados() {
        System.out.println("\n------AUTORES REGISTRADOS------");
        List<Autor> autores = autorRepositorio.findAutoresAndLibros();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }
        for (Autor autor : autores) {
            String fechaNacimiento = autor.getFechaDeNacimiento() != null ? autor.getFechaDeNacimiento() : "No disponible";
            String fechaFallecimiento = autor.getFechaDeFallecimiento() != null ? autor.getFechaDeFallecimiento() : "No disponible";

            System.out.println("Autor: " + autor.getNombre());
            System.out.println("Fecha de nacimiento: " + fechaNacimiento);
            System.out.println("Fecha de fallecimiento: " + fechaFallecimiento);

            System.out.println("Libros:");
            autor.getLibros().forEach(libro -> System.out.printf("- %s (Idioma: %s, Descargas: %s)%n",
                    libro.getTitulo(), libro.getIdioma(), libro.getNumeroDescargas()));
            System.out.println("--------------------------------------------------");
        }
    }

    private void listarAutoresVivos() {
        try {

            System.out.print("Ingrese el año para consultar: \n");
            int busqueda = Integer.parseInt(teclado.nextLine().trim());

            if (busqueda < 0 || busqueda > 2025) {
                System.out.println("Por favor, ingrese un año válido.\n");
                return;
            }

            System.out.println("Autores vivos en " + busqueda + ":");
            List<Autor> autores = autorRepositorio.findAll();
            boolean encontrados = false;

            for (Autor autor : autores) {
                if (buscarVivo(autor, busqueda)) {
                    System.out.println("- " + autor.getNombre() +
                            " (Nacimiento: " + autor.getFechaDeNacimiento() +" y " + "Fallecimiento: " + autor.getFechaDeFallecimiento() + ")" );
                    encontrados = true;
                }
            }
            if (!encontrados) {
                System.out.println("No se encontraron autores para el año especificado.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un año válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean buscarVivo(Autor autor, int busqueda) {
        String fechaNacimiento = autor.getFechaDeNacimiento();
        if (fechaNacimiento == null || fechaNacimiento.isEmpty()) {
            return false;
        }
        try {
            int nacimiento = Integer.parseInt(fechaNacimiento);
            return nacimiento <= busqueda;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void listarPorIdioma() {

        System.out.println("Idiomas disponibles:\n" +
                "ES (Español)\n" +
                "EN (Inglés)\n" +
                "FR (Francés)\n" +
                "PT (Portugués)");
        System.out.print("Ingrese el código del idioma: \n");

        String idioma = teclado.nextLine().trim().toUpperCase();
        if (!idiomaValido(idioma)) {
            System.out.println("Idioma no válido:\n" +
                    "Use: ES, EN, FR o PT");
            return;
        }

        List<Libro> libros = libroRepositorio.findAll();
        List<Libro> librosFiltrados = libros.stream()
                .filter(l -> l.getIdioma().equalsIgnoreCase(idioma))
                .toList();

        if (librosFiltrados.isEmpty()) {
            System.out.println("No se encontraron libros");
            return;
        }

        System.out.println("\nLibros encongrados: ");
        librosFiltrados.forEach(l -> System.out.printf("- %s (Autor: %s)%n",
                        l.getTitulo(),
                        l.getAutor().getNombre())
        );
    }

    private boolean idiomaValido(String idioma) {
        return idioma.matches("^(ES|EN|FR|PT)$");
    }

    private void salir() {
        System.out.println("\nCerrando el sistema...");
        ejecutar = false;
        System.exit(0);
    }
}


