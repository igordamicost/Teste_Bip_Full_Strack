import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';
import { environment } from '../../environments/environment';
import type { Beneficio, TransferenciaRequest } from '../core/models';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/api/v1/beneficios`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService],
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('listarTodos should GET /all', () => {
    const mock: Beneficio[] = [{ id: 1, nome: 'A', valor: 10, ativo: true }];
    service.listarTodos().subscribe((data) => expect(data).toEqual(mock));
    const req = httpMock.expectOne(`${baseUrl}/all`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('buscar should GET /:id', () => {
    const mock: Beneficio = { id: 1, nome: 'A', valor: 10, ativo: true };
    service.buscar(1).subscribe((data) => expect(data).toEqual(mock));
    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('criar should POST', () => {
    const body: Beneficio = { nome: 'X', valor: 5, ativo: true };
    service.criar(body).subscribe((data) => expect(data.nome).toBe('X'));
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush({ ...body, id: 1 });
  });

  it('transferir should POST /transfer', () => {
    const body: TransferenciaRequest = { fromId: 1, toId: 2, amount: 100 };
    service.transferir(body).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/transfer`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush(null);
  });
});
