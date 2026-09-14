import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";
import { Produto } from '../../../models/produto';

@Component({
  selector: 'app-produtos-list',
  imports: [RouterLink],
  templateUrl: './produtos-list.component.html',
  styleUrl: './produtos-list.component.scss'
})
export class ProdutosListComponent {

  lista: Produto[] = []

  constructor() {

    this.lista.push(new Produto(1, 'Casquinha de Baunilha', 'CASQUINHA', 8.5, 50, true));
    this.lista.push(new Produto(2, 'Açaí 500ml', 'ACAI', 18.9, 30, true));
    this.lista.push(new Produto(3, 'Milk Shake de Morango', 'MILK_SHAKE', 15.0, 20, true));


    let produtoNovo = history.state.produtoNovo;

    let produtoEditado = history.state.produtoEditado;

    if(produtoNovo) {
      produtoNovo.id = 123;
      this.lista.push(produtoNovo);
    }

    if(produtoEditado) {
      console.log(produtoEditado)
      for(let i = 0; i < this.lista.length; i++) {
        console.log(produtoEditado.id, this.lista[i].id)
        if(produtoEditado.id == this.lista[i].id) {
          console.log("achou")
          this.lista[i] = produtoEditado
          break;
        }
      }
    }

  }

  deletar(produto: Produto) {

  }
}
