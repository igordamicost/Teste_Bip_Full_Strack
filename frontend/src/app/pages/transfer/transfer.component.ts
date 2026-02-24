import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MessageService } from 'primeng/api';
import { Beneficio, TransferenciaRequest } from '../../core/models';
import { ApiService } from '../../services/api.service';
import { PageToolbarComponent } from '../../shared/components/page-toolbar/page-toolbar.component';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [
    DecimalPipe,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule,
    PageToolbarComponent,
  ],
  templateUrl: './transfer.component.html',
  styleUrl: './transfer.component.scss',
})
export class TransferComponent implements OnInit {
  beneficios: Beneficio[] = [];
  fromId: number | null = null;
  toId: number | null = null;
  amount = 0;
  error = '';

  constructor(
    private api: ApiService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.api.listarTodos().subscribe((list) => (this.beneficios = list));
  }

  onSubmit(): void {
    this.error = '';
    if (this.fromId == null || this.toId == null || this.amount <= 0) {
      this.error = 'Preencha origem, destino e valor positivo.';
      return;
    }
    if (this.fromId === this.toId) {
      this.error = 'Origem e destino devem ser diferentes.';
      return;
    }
    const req: TransferenciaRequest = { fromId: this.fromId, toId: this.toId, amount: this.amount };
    this.api.transferir(req).subscribe({
      next: () => {
        this.amount = 0;
        this.api.listarTodos().subscribe((list) => (this.beneficios = list));
        this.messageService.add({
          severity: 'success',
          summary: 'Transferência',
          detail: 'Transferência realizada.',
        });
      },
      error: (e) => (this.error = e.error?.error || 'Erro na transferência'),
    });
  }
}
