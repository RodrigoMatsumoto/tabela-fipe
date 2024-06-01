package com.example.tabela_fipe.service;

public interface IConverteDados {

  <T> T obterDados(String json, Class<T> classe);
}