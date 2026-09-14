export class Produto {

  id!: number;
  nome!: string;
  categoria!: string;
  preco!: number;
  estoque!: number;
  disponivel!: boolean;


  constructor(id: number, nome: string, categoria: string, preco: number, estoque: number, disponivel: boolean) {
    this.id = id;
    this.nome = nome;
    this.categoria = categoria;
    this.preco = preco;
    this.estoque = estoque;
    this.disponivel = disponivel;
  }
}
