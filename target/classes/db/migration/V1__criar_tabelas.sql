CREATE TABLE produtos (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(255),
                        categoria VARCHAR(255),
                        preco DOUBLE PRECISION,
                        estoque INTEGER,
                        disponivel BOOLEAN
);


CREATE TABLE usuarios (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL,
                        login VARCHAR(255) NOT NULL UNIQUE,
                        senha VARCHAR(255) NOT NULL,
                        cargo VARCHAR(255) NOT NULL,
                        ativo BOOLEAN NOT NULL
);


CREATE TABLE vendas (
                      id BIGSERIAL PRIMARY KEY,
                      valor_total DOUBLE PRECISION,
                      data_hora TIMESTAMP,
                      status VARCHAR(255)
);


CREATE TABLE itens_venda (
                           id BIGSERIAL PRIMARY KEY,

                           venda_id BIGINT NOT NULL,
                           produto_id BIGINT NOT NULL,

                           quantidade INTEGER,
                           valor_unitario DOUBLE PRECISION,
                           subtotal DOUBLE PRECISION,

                           CONSTRAINT fk_itens_venda_venda
                             FOREIGN KEY (venda_id)
                               REFERENCES vendas(id),

                           CONSTRAINT fk_itens_venda_produto
                             FOREIGN KEY (produto_id)
                               REFERENCES produtos(id)
);
