import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { Beneficio } from '../../core/models';
import { ApiService } from '../../services/api.service';
import { PageToolbarComponent } from '../../shared/components/page-toolbar/page-toolbar.component';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    PageToolbarComponent,
  ],
  templateUrl: './beneficio-form.component.html',
  styleUrl: './beneficio-form.component.scss',
})
export class BeneficioFormComponent implements OnInit {
  id: number | null = null;
  model: Beneficio = { nome: '', descricao: '', valor: 0, ativo: true };

  constructor(
    private api: ApiService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id = +idParam;
      this.api.buscar(this.id).subscribe((b) => (this.model = { ...b }));
    }
  }

  get title(): string {
    return this.id ? 'Editar benefício' : 'Novo benefício';
  }

  onSubmit(): void {
    if (this.id != null) {
      this.api.atualizar(this.id, this.model).subscribe({
        next: () => this.router.navigate(['/']),
        error: (e) => alert(e.error?.error || 'Erro ao atualizar'),
      });
    } else {
      this.api.criar(this.model).subscribe({
        next: () => this.router.navigate(['/']),
        error: (e) => alert(e.error?.error || 'Erro ao criar'),
      });
    }
  }
}
