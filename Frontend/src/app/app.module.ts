import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {HomeComponent} from './components/home/home.component';
import {RouterModule} from '@angular/router';
import {MyAccountComponent} from './components/my-account/my-account.component';
import {AllFilesComponent} from './components/all-files/all-files.component';
import {DeletedFilesComponent} from './components/deleted-files/deleted-files.component';
import {SharedFilesComponent} from './components/shared-files/shared-files.component';
import {ContextMenuComponent} from './shared/context-menu/context-menu.component';
import {FsEntryComponent} from './shared/fs-entry/fs-entry.component';
import {ErrorsModule} from "./errors/errors.module";
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {CookieService} from 'ngx-cookie-service';
import {TokenInterceptorService} from './services/auth_service/token-interceptor.service';
import {TopbarComponent} from "./shared/topbar/topbar.component";
import {SidebarComponent} from "./shared/sidebar/sidebar.component";
import {ContactComponent} from './shared/contact/contact.component';
import {WelcomeComponent} from "./components/welcome/welcome.component";
import {LoginComponent} from "./components/login/login.component";
import {SignUpComponent} from "./components/sign-up/sign-up.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatListModule} from "@angular/material/list";
import {MatDividerModule} from "@angular/material/divider";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatInputModule} from "@angular/material/input";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {PaginatorComponent} from "./shared/paginator/paginator.component";
import {FileDialogComponent} from "./shared/file-dialog/file-dialog.component";
import {MatTreeModule} from "@angular/material/tree";
import {MatDialogModule} from "@angular/material/dialog";
import {NewReichtComponent} from "./components/new-reicht/new-reicht.component";
import { RenameDialogComponent } from './shared/rename-dialog/rename-dialog.component';
import {QuestionDialog} from "./shared/question-dialog/question-dialog.component";
import { WelcomeTopbarComponent } from './shared/welcome-topbar/welcome-topbar.component';

@NgModule({
  declarations: [AppComponent,
    PaginatorComponent,
    HomeComponent,
    MyAccountComponent,
    AllFilesComponent,
    DeletedFilesComponent,
    ContextMenuComponent,
    FsEntryComponent,
    TopbarComponent,
    SidebarComponent,
    ContactComponent,
    WelcomeComponent,
    LoginComponent,
    SignUpComponent,
    FileDialogComponent,
    NewReichtComponent,
    RenameDialogComponent,
    QuestionDialog,
    WelcomeTopbarComponent

  ],
  imports: [BrowserModule,
    AppRoutingModule,
    MatSnackBarModule,
    BrowserAnimationsModule,
    RouterModule,
    ErrorsModule,
    MatDividerModule,
    MatIconModule,
    MatListModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatInputModule,
    MatSidenavModule, MatToolbarModule, MatTreeModule, MatDialogModule, FormsModule
  ],

  providers: [CookieService,{provide:HTTP_INTERCEPTORS,useClass:TokenInterceptorService,multi:true}],
  bootstrap: [AppComponent],
})
export class AppModule {
}
