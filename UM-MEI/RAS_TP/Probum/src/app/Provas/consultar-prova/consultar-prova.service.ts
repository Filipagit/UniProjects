import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class ConsultarProvaService {

  private apiUrl = 'http://127.0.0.1:5001/Probum/api';

  constructor(private http: HttpClient) {}

  getProva(userId: number, provaId: number, versaoId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/detalhesProvaCorrigida/${userId}/${provaId}/${versaoId}`).pipe(
      map(provas => provas.map(prova => ({
        resultado: prova.resultado,
        data_versao: prova.data_versao,
        prova_id: prova.prova_id,
        versao_id: prova.versao_id,
        id: prova.prova_id,
        nome: prova.nome_prova,
        uc: prova.uc,
        questoes: prova.questoes.map((questao: { id: any; pergunta: any; tag: any; opcoes: any[]; resposta: any[]; }) => ({
          id: questao.id,
          pergunta: questao.pergunta,
          tag: questao.tag,
          opcoes: questao.opcoes.map(opcao => ({
            id: opcao.id,
            correta: opcao.correta,
            texto: opcao.texto,
          })),
          resposta: questao.resposta.map(resposta => ({
            id: resposta.id,
            opcao_id: resposta.opcao_id,
            resposta_texto: resposta.resposta_texto,
          })),
        })),
      })))
    );
  }
}
