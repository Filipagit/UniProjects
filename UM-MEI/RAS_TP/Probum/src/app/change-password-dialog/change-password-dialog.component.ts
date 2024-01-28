import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldControl} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {HttpClientModule} from '@angular/common/http';
import { ChangePasswordService } from './change-password-dialog.service';

@Component({
  selector: 'app-change-password-dialog',
  standalone: true,
  imports: [MatFormFieldModule, FormsModule, HttpClientModule, MatDialogModule,MatInputModule,MatIconModule],
  templateUrl: './change-password-dialog.component.html',
  styleUrl: './change-password-dialog.component.css',
  providers: [ChangePasswordService],
})

export class ChangePasswordDialogComponent {

  hide= true;
  oldPassword = ''; // Declaração das variáveis oldPassword e newPassword
  newPassword = '';

  constructor(
    private changePassword: ChangePasswordService,
    public dialogRef: MatDialogRef<ChangePasswordDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {}


    savePassword(): void {
      this.changePassword.savePassword(this.oldPassword, this.newPassword).subscribe(
        (response) => {
          console.log('Senha alterada com sucesso:', response);
          // Feche o diálogo após alterar a senha com sucesso
          this.dialogRef.close();
        },
        (error) => {
          console.error('Erro ao alterar a senha:', error);
          // Lide com o erro aqui, se necessário
        }
      );
    }

  closeDialog(): void {
    // Fechar o diálogo sem salvar a senha
    this.dialogRef.close();
  }
}
