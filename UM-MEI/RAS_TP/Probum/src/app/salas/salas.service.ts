import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SalasService {
  private baseUrl = 'http://127.0.0.1:5002/Probum/api'; // URL da sua API Flask

  constructor(private http: HttpClient) { }

  getSalas(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/salas`);
  }

  criarSala(salaData: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/sala`, salaData);
  }

  eliminarSala(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/salas/${id}`);
  }
}
