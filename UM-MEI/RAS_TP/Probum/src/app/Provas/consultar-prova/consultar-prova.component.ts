import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-consultar-prova',
  standalone: true,
  imports: [HttpClientModule],
  templateUrl: './consultar-prova.component.html',
  styleUrl: './consultar-prova.component.css'
})
export class ConsultarProvaComponent {

  // falta isto
  prova: any = {}; 
}
