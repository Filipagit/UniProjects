import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { HttpClientModule } from '@angular/common/http';
import { HomepageComponent } from './homepage.component';
import { Router } from '@angular/router';
import { tap} from 'rxjs/operators';

@Injectable()
export class AuthService {

  private baseUrl = 'http://127.0.0.1:5000'; // Replace with your Flask API URL
	constructor(private http: HttpClient, private router: Router) {
		this.headers = new HttpHeaders({ 'Content-Type': 'application/json', 'Accept': "*/*" });
	};

	headers: HttpHeaders;

  login(username: string, password: string) {
    const body = { username, password };
    return this.http.post<any>(`${this.baseUrl}/login`, body, { headers: this.headers }).pipe(
      catchError((error) => {
        console.error('Error occurred during login:', error);
        return throwError(error);
      }),
      tap((response: any) => {
        console.log('Login bem-sucedido', response);
        localStorage.setItem('token', response.token);
        this.router.navigate(['/home']).then(() => {
          window.location.reload(); // Refresh após a navegação
        });
      })
    );
  }

  // Other methods like register, changePassword, etc. can be implemented similarly
}
