import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class UserService {

    private baseUrl = 'http://127.0.0.1:5000'; // URL da sua API Flask
    private headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  
    constructor(private http: HttpClient) { 
        const token = localStorage.getItem('token');
        if (token) {
            this.setAuthorizationToken(token);
        }
    }
  
    // Método para definir o token de autorização
    setAuthorizationToken(token: string): void {
      this.headers = this.headers.set('Authorization', `Bearer ${token}`);
    }
  
    getUserInfo(): Observable<any> {
      return this.http.get<any>(`${this.baseUrl}/get_user_info`, { headers: this.headers });
    }
  }