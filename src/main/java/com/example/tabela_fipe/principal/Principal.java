package com.example.tabela_fipe.principal;

import com.example.tabela_fipe.model.Dados;
import com.example.tabela_fipe.model.Modelos;
import com.example.tabela_fipe.model.Veiculo;
import com.example.tabela_fipe.service.ConsumoApi;
import com.example.tabela_fipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal {

  private final Scanner scanner = new Scanner(System.in);
  private final ConsumoApi consumidor = new ConsumoApi();
  private final ConverteDados conversor = new ConverteDados();

  public void exibeMenu() {
    var menu = """
        *** OPÇÕES ***
        Carro
        Moto
        Caminhão
        
        Digite uma das opções para consultar:
        """;

    System.out.println(menu);

    String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    String endereco;
    var opcao = scanner.nextLine();

    if (opcao.toLowerCase().contains("car")) {
      endereco = URL_BASE + "carros/marcas";
    } else if (opcao.toLowerCase().contains("mot")) {
      endereco = URL_BASE + "motos/marcas";
    } else {
      endereco = URL_BASE + "caminhoes/marcas";
    }

    var json = consumidor.obterDados(endereco);
    System.out.println(json);

    var marcas = conversor.obterLista(json, Dados.class);

    marcas.stream()
        .sorted(Comparator.comparing(Dados::codigo))
        .forEach(System.out::println);

    System.out.println("Informe o código da marca para consulta: ");
    var codigoMarca = scanner.nextLine();

    endereco = endereco + "/" + codigoMarca + "/modelos";
    json = consumidor.obterDados(endereco);

    var modeloLista = conversor.obterDados(json, Modelos.class);

    System.out.println("\nModelos dessa marca: ");
    modeloLista.modelos().stream()
        .sorted(Comparator.comparing(Dados::codigo))
        .forEach(System.out::println);

    System.out.println("\nDigite um trecho do nome do carro a ser buscado");
    var nomeVeiculo = scanner.nextLine();

    List<Dados> modelosFiltrados = modeloLista.modelos().stream()
        .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
        .toList();

    System.out.println("\n Modelos filtrados: ");
    modelosFiltrados.forEach(System.out::println);

    System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação: ");
    var codigoModelo = scanner.nextLine();

    endereco = endereco + "/" + codigoModelo + "/anos";
    json = consumidor.obterDados(endereco);

    List<Dados> anos = conversor.obterLista(json, Dados.class);
    List<Veiculo> veiculos = new ArrayList<>();

    for (Dados ano : anos) {
      var enderecoAnos = endereco + "/" + ano.codigo();
      json = consumidor.obterDados(enderecoAnos);
      Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
      veiculos.add(veiculo);
    }

    System.out.println("Todos os veículos filtrados com avaliação por ano: ");
    veiculos.forEach(System.out::println);
  }
}