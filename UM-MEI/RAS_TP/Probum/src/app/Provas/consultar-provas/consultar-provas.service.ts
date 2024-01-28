import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProvasService {
  private apiUrl = 'http://127.0.0.1:5001/Probum/api'; 
  private baseUrl = 'http://127.0.0.1:5000'; // URL da sua API Flask
  
  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
        if (token) {
            this.setAuthorizationToken(token);
        }
   }

  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  
    // Método para definir o token de autorização
    setAuthorizationToken(token: string): void {
      this.headers = this.headers.set('Authorization', `Bearer ${token}`);
    }
  
    getUserInfo(): Observable<any> {
      return this.http.get<any>(`${this.baseUrl}/get_user_info`, { headers: this.headers });
    }

  getProvas(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/provasAtuais/${userId}`).pipe(
        map(provas => provas.map(prova => ({
            resultado: prova.resultado,
            data_versao: prova.data_versao,
            prova_id: prova.prova_id,
            versao_id: prova.versao_id,
            id: prova.prova_id,
            nome: prova.nome_prova,
            uc: prova.uc
          })))
        );
  }
}