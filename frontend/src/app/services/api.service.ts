import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import type { Beneficio, TransferenciaRequest } from '../core/models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  private get base(): string {
    return `${environment.apiUrl}/api/v1/beneficios`;
  }

  listar(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.base);
  }

  listarTodos(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.base}/all`);
  }

  buscar(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.base}/${id}`);
  }

  criar(b: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.base, b);
  }

  atualizar(id: number, b: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.base}/${id}`, b);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  transferir(req: TransferenciaRequest): Observable<void> {
    return this.http.post<void>(`${this.base}/transfer`, req);
  }
}
