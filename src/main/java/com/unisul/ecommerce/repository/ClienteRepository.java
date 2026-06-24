package com.unisul.ecommerce.repository;

import com.unisul.ecommerce.model.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

    private List<Cliente> bancoDeClientes = new ArrayList<>();

    public ClienteRepository() {
        bancoDeClientes.add(new Cliente("Cliente Demonstração", "88000-000"));
    }

    public List<Cliente> buscarTodos() {
        return bancoDeClientes;
    }
}