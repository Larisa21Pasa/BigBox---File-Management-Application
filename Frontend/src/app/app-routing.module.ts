import {NgModule} from '@angular/core';
import {HomeComponent} from './components/home/home.component';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {AllFilesComponent} from "./components/all-files/all-files.component";
import {DeletedFilesComponent} from "./components/deleted-files/deleted-files.component";
import {SignUpComponent} from "./components/sign-up/sign-up.component";
import {MyAccountComponent} from "./components/my-account/my-account.component";
import {WelcomeComponent} from "./components/welcome/welcome.component";
import {AuthGuard} from './guardians/auth_guardians/auth.guard';
import {NewReichtComponent} from "./components/new-reicht/new-reicht.component";


const routes: Routes = [
  {
    path: 'welcome',
    component: WelcomeComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'sign-up',
    component: SignUpComponent,
  },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'my-account',
    component: MyAccountComponent
    ,
    canActivate: [AuthGuard]
  },
  {
    path: 'all-files',
    component: AllFilesComponent,
    canActivate: [AuthGuard],
    data: {openIn: "-1"}
  },
  {
    path: 'deleted-files',
    component: DeletedFilesComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'new-reicht',
    component: NewReichtComponent,
  },
  {
    path: 'errors',
    loadChildren: () => import('./errors/errors.module').then((m) => m.ErrorsModule),
  },
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {
    path: '**',
    redirectTo: '/errors/not-found',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
