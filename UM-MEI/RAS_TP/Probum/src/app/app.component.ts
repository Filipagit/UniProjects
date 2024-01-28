import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import { AppService } from './app.service';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HttpClientModule, RouterOutlet,MatToolbarModule,MatIconModule,MatMenuModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  providers: [AppService],
})
export class AppComponent {
  title = 'Probum';

  hide = true
  role: string = ''; // Inicialização com valor padrão vazio

  constructor(private appService: AppService, private router: Router) {}

  ngOnInit(): void {
    this.getUserInfo();
  }

  logout(): void {
    // Limpar token do localStorage
    localStorage.removeItem('token');
    // Definir a role como vazio
    this.role = '';
    // Aqui você define a rota da página principal (por exemplo, '/')
    this.router.navigate(['/']);
  }

  redirecionaPerfil(): void {
    // Redireciona para a rota '/perfil'
    this.router.navigate(['/perfil']);
  }

  home(): void {
    // Redireciona para a rota '/perfil'
    this.router.navigate(['/home']);
  }

  registar(): void {
    // Redireciona para a rota '/perfil'
    this.router.navigate(['/register']);
  }

  classificacoes(): void {
    // Redireciona para a rota '/perfil'
    this.router.navigate(['/classificacoes']);
  }

  salas(): void {
    // Redireciona para a rota '/perfil'
    this.router.navigate(['/salas']);
  }

  criarProva(): void {
    // Redireciona para a rota '/perfil'
    this.router.navigate(['/criarprova']);
  }


  getUserInfo(): void {
    this.appService.getUserInfo().subscribe(
      (response) => {
        this.role = response.role;
        console.log(this.role);
      },
      (error) => {
        console.error('Erro ao obter informações do usuário', error);
      }
    );
  }
}
