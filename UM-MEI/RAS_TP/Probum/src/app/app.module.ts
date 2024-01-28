import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { TimepickerModule } from 'ngx-bootstrap/timepicker';
import { HttpClientModule } from '@angular/common/http';


import { AppComponent } from './app.component';
import { ProvaComponent } from './criarprova/criarprova.component'; 
import { HomepageComponent } from './homepage/homepage.component';
import {ChangePasswordDialogComponent} from './change-password-dialog/change-password-dialog.component';
import { routes } from './app.routes';
import { CommonModule } from '@angular/common';


@NgModule({
  declarations: [
    AppComponent,
    ProvaComponent,
    HomepageComponent,
    ChangePasswordDialogComponent,

    // ... outros componentes
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes), // Adicione suas rotas aqui
    FormsModule,
    BsDatepickerModule.forRoot(),
    TimepickerModule.forRoot(),
    HttpClientModule,
    CommonModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
