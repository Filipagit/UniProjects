import { Component, OnInit } from '@angular/core';
import { Questao } from '../Modules/questao.module';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-provas', // Seletor usado para referenciar esse componente em outros arquivos HTML
  template: `
    <h2>Lista de Questões</h2>
  `,
  //templateUrl: './provas.component.html', // Caminho para o arquivo HTML associado a este componente
})
export class ProvasComponent implements OnInit {
  questoes: Questao[] = [
    {
      id: 1,
      enunciado: 'Qual é a capital do Brasil?',
      tipo: 'escolhaMultipla',
      opcoes: ['Rio de Janeiro', 'São Paulo', 'Brasília'],
      respostaCorreta: 'Brasília',
    },
    {
      id: 2,
      enunciado: 'Quanto é 2 + 2?',
      tipo: 'respostaAberta',
      respostaCorreta: '4',
    },
    // Adicione outras questões fixas aqui
  ];

  constructor() {}

  ngOnInit(): void {}
}
