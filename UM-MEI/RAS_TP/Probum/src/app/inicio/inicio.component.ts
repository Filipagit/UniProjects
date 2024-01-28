import { Component } from '@angular/core';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatCardModule} from '@angular/material/card';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [MatGridListModule, MatCardModule],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.css'
})
export class InicioComponent {

}
