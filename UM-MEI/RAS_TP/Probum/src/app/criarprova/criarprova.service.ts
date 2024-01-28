import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';

@Injectable()

  
export class ProvasService {
  private apiUrl = 'http://localhost:5001/Probum/api'; 
  constructor(private http: HttpClient) {
	}



  getUnidadesCurriculares(): Observable<string[]> {
    return this.http.get<any[]>(`${this.apiUrl}/provas`).pipe(
      map((provas: any[]) => provas.map(prova => prova.uc))
    );
  }
  
}
