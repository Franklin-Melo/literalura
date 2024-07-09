package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.*;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO = "https://gutendex.com/books/?search=";
    private final String ENDERECOATORESVIVOS = "https://gutendex.com/books/?author_year_start=";
    private List<DadosLivro> dadoslivro = new ArrayList<>();

    private List<GutendexApi> dadosResults = new ArrayList<>();

    private List<DadosAutor> dadosAutor = new ArrayList<>();
    private List<Livro> livros = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();

    private AutorRepository autorRepositorio;
    private LivroRepository repositorio;

    public Principal(LivroRepository repositorio, AutorRepository autorRepositorio) {
        this.repositorio = repositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void exibeMenu() {

        var opcao = -1;

        while (opcao != 0) {
            var menu =
                    """     
                            ----------
                            Escolha o número de sua opção:           
                            1 - Buscar Livro pelo Título.
                            2 - Listar Livros Registrados.
                            3 - Listar Autores Registrados.
                            4 - Listar Autores Vivos Em Um Determinado Ano.
                            5 - Listar Livros em um determinado Idioma.
                            0 - Sair
                                                
                            """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {

                case 1:
                    buscarLivroWeb();
                    break;

               case 2:
                   listarLivroRegistrado();
                   break;

                case 3:
                    listaAutoresRegistrados();
                    break;

                case 4:
                    listarAutoresVivos();
                    break;

                case 5:
                    listarLivrosEmDeterminadoIdioma();
                    break;

            }

        }
    }

    private void listarLivrosEmDeterminadoIdioma() {
        System.out.println("""
                Digite o idioma que deseja escolher:
                pt - Português
                en - Inglês
                es - Espanhol
                fr - Francês
                """);
        String idiomaEscolhido = leitura.nextLine();
        String idiomaCompleto = converterCodigoParaIdioma(idiomaEscolhido);

        livros = repositorio.findAllByIdioma(idiomaEscolhido);
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado para o idioma '" + idiomaCompleto + "'.");
        } else {
            System.out.println("Livros encontrados em '" + idiomaCompleto + "':");
            for (Livro livro : livros) {
                System.out.println("Identificação: " +livro.getIdentificacao());
                System.out.println("Título: " + livro.getTitulo());
                System.out.println("Autor: " + livro.getAutor().getNome()); // Exemplo de como acessar o nome do autor
                System.out.println("Idioma: " + livro.getIdioma());
                System.out.println("Downloads: " + livro.getNumeroDownloads());
                System.out.println();
            }
        }
    }

    private String converterCodigoParaIdioma(String codigo) {
        switch (codigo) {
            case "pt":
                return "Português";
            case "en":
                return "Inglês";
            case "es":
                return "Espanhol";
            case "fr":
                return "Francês";
            default:
                return "Desconhecido";
        }
    }

    private void listarAutoresVivos() {
        System.out.println("Digite o ano para listar os autores vivos:");
        var anoEspecifico = leitura.nextInt();
        leitura.nextLine();

        List<Autor> autoresVivos = autorRepositorio.findAutoresVivosApartirDe(anoEspecifico);


        if (autoresVivos.isEmpty()) {
            System.out.println("Nenhum autor vivo em " + anoEspecifico + ".");
        } else {
            System.out.println("Autores vivos a partir de " + anoEspecifico + ":");
            autoresVivos.forEach(autor -> {
                System.out.println("Nome: " + autor.getNome());
                System.out.println("  Data de Nascimento: " + autor.getDataNascimento());
                System.out.println("  Data de Falecimento: " + (autor.getDataFalecimento() != null ? autor.getDataFalecimento() : "Ainda vivo"));
            });
        }
    }


    private void listaAutoresRegistrados() {

            autores = autorRepositorio.findAll();

            if (autores.isEmpty()) {
                System.out.println("Nenhum autor registrado.");
            } else {
                System.out.println("Lista de Autores Registrados:");
                System.out.println();
                for (Autor autor : autores) {
                    System.out.println("Nome: " + autor.getNome());
                    System.out.println("Data de Nascimento: " + autor.getDataNascimento());
                    System.out.println("Data de Falecimento: " + (autor.getDataFalecimento() != null ? autor.getDataFalecimento() : "Ainda vivo"));
                    System.out.println();
                }
            }
        }


    private void buscarLivroWeb() {

        List<DadosLivro> dadosLivro = getDadosLivro();
        List<Livro> livros = dadosLivro.stream()
                .map(Livro::new)
                .collect(Collectors.toList());

        repositorio.saveAll(livros);

    }

    private List<DadosLivro> getDadosLivro() {
        System.out.println("Digite o Título do Livro para busca:");
        var titulo = leitura.nextLine();
        var json = consumoApi.obterDados(ENDERECO + titulo.replace(" ", "+"));
        GutendexApi results = conversor.obterDados(json, GutendexApi.class);
        List<DadosLivro> livrosFiltrados = results.results().stream()
                .filter(livro -> livro.titulo().equalsIgnoreCase(titulo))
                .collect(Collectors.toList());


        System.out.println(" ##### Livros Encontrados com o Título '" + titulo + "' ##### ");

        for (DadosLivro livro : livrosFiltrados) {
            System.out.println("ID: " + livro.identificacao());
            System.out.println("Titulo: " + livro.titulo());

            for (DadosAutor autores: livro.autores()){
                System.out.println("Autor: " + autores.nome());
                System.out.println("Data de Nascimento: " + autores.dataNascimento());
                System.out.println("Data de Falecimento: " + autores.dataFalecimento());
            }
            System.out.println("Downloads: " + livro.numeroDownloads());
            System.out.println("Idioma: " + livro.idioma().get(0));
            System.out.println();

        }

        return livrosFiltrados;

    }

    private void listarLivroRegistrado() {
        livros = repositorio.findAll();

        System.out.println(" ##### Listando Livros Buscados #####");
        System.out.println();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro foi buscado ainda.");
        } else {

            for (Livro livro : livros) {
                System.out.println("ID: " + livro.getIdentificacao());
                System.out.println("Titulo: " + livro.getTitulo());

                Autor autor = livro.getAutor(); // Obter o único autor do livro

                if (autor != null) {
                    System.out.println("Autor: " + autor.getNome());
                }

                System.out.println("Downloads: " + livro.getNumeroDownloads());
                System.out.println("Idioma: " + livro.getIdioma());
                System.out.println();
            }
        }
    }



}
