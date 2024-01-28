import { Component } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import {MatDividerModule} from '@angular/material/divider';
import { AuthService } from '../homepage/homepage.service';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';
import { RegisterService } from './register.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [MatSelectModule, MatFormFieldModule,FormsModule, MatInputModule, MatButtonModule, MatIconModule, MatCardModule, FlexLayoutModule, MatDividerModule, HttpClientModule,],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  providers: [RegisterService],
})
export class RegisterComponent {
  hide = true
  selected = 'option2';
  role: string = ''; // Inicialização com valor padrão vazio

  userData: any = {
    username: '',
    password: '',
    email: ''
  };

  constructor(private registerService: RegisterService, private router: Router) {}

  ngOnInit(): void {
    this.getUserInfo();
  }

  onSubmit(): void {
    if (this.selected === 'option1') {
      this.registerTeacher(); // Chama a função registerTeacher se a opção selecionada for 'option1'
    } else if (this.selected === 'option2') {
      this.registerUser(); // Chama a função registerUser se a opção selecionada for 'option2'
    }
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

  registar(): void {
    // Redireciona para a rota '/perfil'
    this.router.navigate(['/register']);
  }

  getUserInfo(): void {
    this.registerService.getUserInfo().subscribe(
      (response) => {
        this.role = response.role;
        console.log(this.role);
      },
      (error) => {
        console.error('Erro ao obter informações do aluno', error);
      }
    );
  }

  registerUser(): void {
    this.registerService.registerUser(this.userData).subscribe(
      (response) => {
        console.log('Aluno registrado com sucesso:', response);
        // Lidar com a resposta do servidor após o registro bem-sucedido
      },
      (error) => {
        console.error('Erro ao registrar aluno:', error);
        // Lidar com possíveis erros durante o registro
      }
    );
  }

  registerTeacher(): void {
    this.registerService.registerTeacher(this.userData).subscribe(
      (response) => {
        console.log('Professor registrado com sucesso:', response);
        // Lidar com a resposta do servidor após o registro bem-sucedido
      },
      (error) => {
        console.error('Erro ao registrar professor:', error);
        // Lidar com possíveis erros durante o registro
      }
    );
  }
}
