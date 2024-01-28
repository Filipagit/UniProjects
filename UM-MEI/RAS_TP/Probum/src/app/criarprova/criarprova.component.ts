import { Component } from '@angular/core';
import { ProvasService } from './criarprova.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-prova',
  templateUrl: './criarprova.component.html',
  providers: [ProvasService],
})
export class ProvaComponent {
  nomeProva: string = ''; // Variável para armazenar o nome da prova
  dataHoraProva: string = ''; // Variável para armazenar a data e hora da prova
  exibirCriarQuestao: boolean = false;
  exibirOpcoes: boolean = false;
  tipoQuestaoSelecionada: string = '';

  

  onNomeChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.nomeProva = inputElement.value; // Armazena o valor do input de nome da prova
  }

  onDateTimeChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.dataHoraProva = inputElement.value; // Armazena o valor do input de data e hora da prova
  }

  criarProva(): void {
   this.exibirCriarQuestao = true;
    console.log('Nome da prova:', this.nomeProva);
    console.log('Data e Hora da prova:', this.dataHoraProva);
  }
  exibirOpcoesQuestao(): void {
    this.exibirOpcoes = true; // Exibe as opções de tipo de questão
  }

  onTipoQuestaoChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.tipoQuestaoSelecionada = selectElement.value; // Armazena o tipo de questão selecionada
  }
  guardarProva(): void {
    // Aqui você pode adicionar a lógica para salvar os dados inseridos
    console.log('Nome da prova:', this.nomeProva);
    console.log('Data e Hora da prova:', this.dataHoraProva);
    console.log('Tipo de questão selecionada:', this.tipoQuestaoSelecionada);

  }
  criarQuestao(): void {
    // Adicione aqui a lógica para criar a questão
    console.log('Criar questão:', this.tipoQuestaoSelecionada);
  }
}
