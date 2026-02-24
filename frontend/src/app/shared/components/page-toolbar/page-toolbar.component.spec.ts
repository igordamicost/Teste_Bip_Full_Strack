import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PageToolbarComponent } from './page-toolbar.component';

describe('PageToolbarComponent', () => {
  let component: PageToolbarComponent;
  let fixture: ComponentFixture<PageToolbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PageToolbarComponent, RouterTestingModule, NoopAnimationsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(PageToolbarComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show title when set', () => {
    component.title = 'My Page';
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('My Page');
  });

  it('should show New and Transfer when flags true', () => {
    component.showNew = true;
    component.showTransfer = true;
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('Novo benefício');
    expect(el.textContent).toContain('Transferir');
  });
});
