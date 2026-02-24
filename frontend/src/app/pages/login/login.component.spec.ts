import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../../environments/environment';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    router = jasmine.createSpyObj('Router', ['navigate']);
    await TestBed.configureTestingModule({
      imports: [LoginComponent, HttpClientTestingModule, NoopAnimationsModule],
      providers: [AuthService, { provide: Router, useValue: router }],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have empty username and password initially', () => {
    expect(component.username).toBe('');
    expect(component.password).toBe('');
    expect(component.error).toBe('');
  });

  it('should set error on failed login', () => {
    component.username = 'admin';
    component.password = 'wrong';
    component.onSubmit();
    const req = httpMock.expectOne(`${environment.apiUrl}/api/v1/auth/login`);
    req.flush({ error: 'Credenciais inválidas' }, { status: 401, statusText: 'Unauthorized' });
    fixture.detectChanges();
    expect(component.error).toBe('Credenciais inválidas');
  });
});
