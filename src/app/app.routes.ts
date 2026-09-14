import { Routes } from '@angular/router';
import { LoginComponent } from './components/layout/login/login.component';
import { PrincipalComponent } from './components/layout/principal/principal.component';
import { ProdutosListComponent } from './components/produtos/produtos-list/produtos-list.component';
import { ProdutosDetailsComponent } from './components/produtos/produtos-details/produtos-details.component';

export const routes: Routes = [
  {path:'', redirectTo:'login', pathMatch:'full'},
  {path:'login', component: LoginComponent},
  {
    path:'admin',
    component: PrincipalComponent,
    children: [
      {path: 'produtos', component: ProdutosListComponent},
      {path: 'produtos/new', component: ProdutosDetailsComponent},
      {path: 'produtos/edit/:id', component: ProdutosDetailsComponent},
    ]
  }

];
