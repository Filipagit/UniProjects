import { Component } from '@angular/core';
import { ProvasService } from "../consultar-provas/consultar-provas.service";
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-consultar-provas',
  standalone: true,
  imports: [HttpClientModule],
  templateUrl: './consultar-provas.component.html',
  styleUrl: './consultar-provas.component.css',
  providers: [ProvasService],
})
export class ConsultarProvasComponent {
  provas: any[] = [];
  userId: number = 0;

  // falta trazer de trás o userid

  constructor(private provasService: ProvasService) { }

  ngOnInit(): void {
    this.getUserInfo();
  }

  getUserInfo(): void {
    this.provasService.getUserInfo().subscribe(
      (response) => {
        this.userId = response.id;
        console.log(this.userId);
        console.log(this.userId);
        console.log(this.userId);
        this.getProvas();
      },
      (error) => {
        console.error('Erro ao obter informações do usuário', error);
      }
    );
  }

  getProvas(): void {
    console.log(this.userId);
    this.provasService.getProvas(this.userId).subscribe(
      (provas) => {
        this.provas = provas;
        console.log(this.provas); // Exibe os dados na consola para verificação
      },
      (error) => {
        console.error('Erro ao obter provas:', error);
      }
    );
  }
}
