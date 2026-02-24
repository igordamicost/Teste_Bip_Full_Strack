import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MessageService } from 'primeng/api';
import { Beneficio } from '../../core/models';
import { ApiService } from '../../services/api.service';
import { DialogService } from '../../shared/services/dialog.service';
import { PageToolbarComponent } from '../../shared/components/page-toolbar/page-toolbar.component';
import { routeEditar } from '../../core/enums';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [
    DecimalPipe,
    RouterLink,
    MatTableModule,
    MatButtonModule,
    MatCardModule,
    PageToolbarComponent,
  ],
  templateUrl: './beneficio-list.component.html',
  styleUrl: './beneficio-list.component.scss',
})
export class BeneficioListComponent implements OnInit {
  dataSource = new MatTableDataSource<Beneficio>([]);
  displayedColumns: string[] = ['id', 'nome', 'descricao', 'valor', 'ativo', 'acoes'];

  readonly routeEditar = routeEditar;

  constructor(
    private api: ApiService,
    private dialog: DialogService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.api.listarTodos().subscribe((list) => (this.dataSource.data = list));
  }

  excluir(b: Beneficio): void {
    if (!b.id) return;
    this.dialog
      .confirm({
        title: 'Excluir benefício',
        message: `Excluir "${b.nome}"?`,
        confirmText: 'Excluir',
      })
      .subscribe((ok) => {
        if (ok) {
          this.api.excluir(b.id!).subscribe({
            next: () => (this.dataSource.data = this.dataSource.data.filter((x) => x.id !== b.id)),
            error: (e) =>
              this.messageService.add({
                severity: 'error',
                summary: 'Erro ao excluir',
                detail: e.error?.error || 'Erro ao excluir',
              }),
          });
        }
      });
  }
}
