package com.uniamerica.sorveteria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sistema de caixa da sorveteria.
 * Uso interno: apenas gerente e funcionario operam o sistema, nao ha acesso de cliente.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
