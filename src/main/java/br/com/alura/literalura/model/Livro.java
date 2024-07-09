package br.com.alura.literalura.model;

import jakarta.persistence.*;
@Entity
@Table(name="Livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   @Column(unique = true)
    private String titulo;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Autor autor;

    private String idioma;

   private Integer numeroDownloads;

    private Integer identificacao;

    public Livro(){}

    public Livro (DadosLivro dadosLivro){
        this.titulo = dadosLivro.titulo();
        this.autor = new Autor(dadosLivro.autores().get(0));
        this.idioma = dadosLivro.idioma().isEmpty() ? null : dadosLivro.idioma().get(0); // Pega o primeiro idioma, se existir
        this.identificacao = dadosLivro.identificacao();
        this.numeroDownloads = dadosLivro.numeroDownloads();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getNumeroDownloads() {
        return numeroDownloads;
    }

    public void setNumeroDownloads(Integer numeroDownloads) {
        this.numeroDownloads = numeroDownloads;
    }

    public Integer getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(Integer identificacao) {
        this.identificacao = identificacao;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor=" + autor +
                ", idioma=" + idioma +
                ", numeroDownloads=" + numeroDownloads +
                ", identificacao=" + identificacao +
                '}';
    }
}


