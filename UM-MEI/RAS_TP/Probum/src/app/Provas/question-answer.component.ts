// question-answer.component.ts
import { Component, Input } from '@angular/core';
import { Questao } from '../Modules/questao.module';
@Component({
  selector: 'app-question-answer',
  template: `
    <div>
      <p>{{ questaoAtual.enunciado }}</p>
      <div *ngIf="questaoAtual.tipo === 'escolhaMultipla'">
        <ul>
          <li *ngFor="let opcao of questaoAtual.opcoes">{{ opcao }}</li>
        </ul>
      </div>
      <div *ngIf="questaoAtual.tipo === 'respostaAberta'">
        <input type="text" placeholder="Sua resposta" />
      </div>
    </div>
  `,
})
export class QuestionAnswerComponent {
  @Input() questaoAtual: Questao | undefined;
}
