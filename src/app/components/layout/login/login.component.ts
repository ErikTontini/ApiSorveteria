import { Component, inject } from '@angular/core';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { FormsModule } from "@angular/forms";
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [MdbFormsModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  usuario!: string;
  senha!: string;

  router = inject(Router);

  logar() {
    if (this.usuario === 'erik' && this.senha === 'gay') {
      this.router.navigate(['/admin/produtos'])
    } else {
      alert('Usuario ou Senha incorretos')
    }

  }
}
