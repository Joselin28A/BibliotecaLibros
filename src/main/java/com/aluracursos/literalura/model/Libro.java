package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="Libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String titulo;

    @ManyToOne
    @JoinColumn(name="id_autor")
    private Autor autor;
    private String idioma;
    @Column(name="Número Descargas")
    private Double numeroDescargas;
    private List<String> temas;

    public Libro(){

    }

    public Libro(DatosLibros datosLibros, Autor autor){
        this.titulo= datosLibros.titulo();
        this.autor = autor;
        this.idioma = datosLibros.idiomas().get(0);
        this.numeroDescargas=datosLibros.numeroDeDescargas();
        this.temas= datosLibros.temas();
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Double getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Double numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public List<String> getTemas() {
        return temas;
    }

    public void setTemas(List<String> temas) {
        this.temas = temas;
    }

    @Override
    public String toString() {
        return "-----------LIBRO------------\n" +
                "Titulo: " + titulo + "\n" +
                "Autor: " + autor.getNombre() + "\n" +
                "Idioma: " + idioma + "\n" +
                "Numero de descargas: " + numeroDescargas + "\n" +
                "Temas: " + temas + "\n" +
                "---------------------------";
    }
}
