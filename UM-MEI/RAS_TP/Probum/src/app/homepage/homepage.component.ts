import { Component } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import {MatDividerModule} from '@angular/material/divider';
import { AuthService } from '../homepage/homepage.service';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [MatFormFieldModule,FormsModule, MatInputModule, MatButtonModule, MatIconModule, MatCardModule, FlexLayoutModule, MatDividerModule, HttpClientModule,],
  styleUrl: './homepage.component.css',
  templateUrl: './homepage.component.html',
  providers: [AuthService],
})
export class HomepageComponent {
  hide = true

  constructor(private authService: AuthService, private router: Router) {}

  login(credentials: { username: string, password: string }): void {
    this.authService.login(credentials.username, credentials.password).subscribe(() => {
    });
  }
}

