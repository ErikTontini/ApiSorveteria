import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { Produto } from '../../../models/produto';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2'

@Component({
  selector: 'app-produtos-details',
  imports: [MdbFormsModule, FormsModule],
  templateUrl: './produtos-details.component.html',
  styleUrl: './produtos-details.component.scss'
})
export class ProdutosDetailsComponent {

  produto = new Produto(0, '', '', 0, 0, true);

  router = inject(ActivatedRoute);
  router2 = inject(Router);

  constructor() {
    let id = this.router.snapshot.params['id'];
    if (id > 0) {
      this.findById(id);
    }
  }

  findById(id: number){
    let produtoRetornado: Produto = new Produto(id, 'Açaí 500ml', 'ACAI', 18.9, 30, true);
    this.produto = produtoRetornado;
  }

  salvar(){
    if(this.produto.id > 0){
      Swal.fire({
        title: 'Editado com sucesso!',
        icon: 'success',
        confirmButtonText: 'Ok'
      });
      this.router2.navigate(['/admin/produtos'],{ state: {produtoEditado: this.produto}})
    } else {
      Swal.fire({
        title: 'Salvo com sucesso!',
        icon: 'success',
        confirmButtonText: 'Ok'
      });
      this.router2.navigate(['/admin/produtos'],{ state: {produtoNovo: this.produto}})
    }

  }
}
