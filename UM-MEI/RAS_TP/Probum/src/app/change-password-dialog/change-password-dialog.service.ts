import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class ChangePasswordService {
  private baseUrl = 'http://127.0.0.1:5000'; // URL da sua API Flask
  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    if (token) {
      this.setAuthorizationToken(token);
    }
  }

  // Método para definir o token de autorização
  private setAuthorizationToken(token: string): void {
    this.headers = this.headers.set('Authorization', `Bearer ${token}`);
  }

  // Método para salvar a senha
  savePassword(oldPassword: string, newPassword: string): Observable<any> {
    const data = {
      old_password: oldPassword,
      new_password: newPassword
    };

    return this.http.post<any>(`${this.baseUrl}/change_password`, data, { headers: this.headers });
  }
}
