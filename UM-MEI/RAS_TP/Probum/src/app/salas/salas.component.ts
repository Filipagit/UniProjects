import { Component } from '@angular/core';
import { SalasService } from './salas.service';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 

@Component({
  selector: 'app-salas',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './salas.component.html',
  styleUrl: './salas.component.css',
  providers: [SalasService],
})
export class SalasComponent {

  salas: any[] = [];
  novaSala: any = {
    sala: '',
    edificio: '',
    piso: '',
    capacidade: 0
  };

  constructor(private salasService: SalasService) { }

  ngOnInit(): void {
    this.getSalas();
  }

  getSalas(): void {
    this.salasService.getSalas().subscribe(
      (data) => {
        this.salas = data.salas;
      },
      (error) => {
        console.error('Erro ao buscar salas:', error);
      }
    );
  }

  criarSala(): void {
    this.salasService.criarSala(this.novaSala).subscribe(
      (response) => {
        console.log('Sala criada com sucesso:', response);
        // Atualiza a lista de salas após criar uma nova sala
        this.getSalas();
        // Limpa os campos do formulário
        this.novaSala = {
          sala: '',
          edificio: '',
          piso: '',
          capacidade: 0
        };
      },
      (error) => {
        console.error('Erro ao criar sala:', error);
      }
    );
  }

  eliminarSala(id: number): void {
    this.salasService.eliminarSala(id).subscribe(
      (response) => {
        console.log('Sala deletada com sucesso:', response);
        // Atualiza a lista de salas após deletar uma sala
        this.getSalas();
      },
      (error) => {
        console.error('Erro ao deletar sala:', error);
      }
    );
  }
}
