import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { BeneficioListComponent } from './beneficio-list.component';
import { ApiService } from '../../services/api.service';
import { DialogService } from '../../shared/services/dialog.service';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import type { Beneficio } from '../../core/models';

describe('BeneficioListComponent', () => {
  let component: BeneficioListComponent;
  let fixture: ComponentFixture<BeneficioListComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let dialogService: jasmine.SpyObj<DialogService>;

  const mockBeneficios: Beneficio[] = [
    { id: 1, nome: 'A', valor: 100, ativo: true },
    { id: 2, nome: 'B', valor: 200, ativo: true },
  ];

  beforeEach(async () => {
    apiService = jasmine.createSpyObj('ApiService', ['listarTodos', 'excluir']);
    apiService.listarTodos.and.returnValue(of(mockBeneficios));
    dialogService = jasmine.createSpyObj('DialogService', ['confirm']);
    dialogService.confirm.and.returnValue(of(true));

    await TestBed.configureTestingModule({
      imports: [
        BeneficioListComponent,
        HttpClientTestingModule,
        RouterTestingModule,
        NoopAnimationsModule,
      ],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: DialogService, useValue: dialogService },
        MessageService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BeneficioListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load beneficios on init', () => {
    expect(apiService.listarTodos).toHaveBeenCalled();
    expect(component.dataSource.data.length).toBe(2);
  });

  it('should call dialog and excluir when user confirms', () => {
    apiService.excluir.and.returnValue(of(undefined));
    component.excluir(mockBeneficios[0]);
    expect(dialogService.confirm).toHaveBeenCalled();
    expect(apiService.excluir).toHaveBeenCalledWith(1);
  });
});
