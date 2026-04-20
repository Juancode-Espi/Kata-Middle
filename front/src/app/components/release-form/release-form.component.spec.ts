import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReleaseFormComponent } from './release-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ReleaseService } from '../../services/release.service';
import { of, throwError } from 'rxjs';

describe('ReleaseFormComponent', () => {
  let component: ReleaseFormComponent;
  let fixture: ComponentFixture<ReleaseFormComponent>;
  let mockReleaseService: any;

  beforeEach(async () => {
    mockReleaseService = {
      analyzeStack: jest.fn(),
      create: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ReleaseFormComponent, ReactiveFormsModule],
      providers: [
        { provide: ReleaseService, useValue: mockReleaseService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReleaseFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    expect(component.form.get('tipo')?.value).toBe('rs');
    expect(component.form.valid).toBeFalsy();
  });

  it('should call analyzeStack and patch stack value', () => {
    component.form.patchValue({ prId: 'repo-test' });
    mockReleaseService.analyzeStack.mockReturnValue(of({ stack: 'Angular' }));
    
    component.analyzeWithDeepWiki();
    
    expect(mockReleaseService.analyzeStack).toHaveBeenCalledWith('repo-test');
    expect(component.form.get('stack')?.value).toBe('Angular');
    expect(component.analyzing).toBeFalsy();
  });

  it('should handle error when analyzeStack fails', () => {
    component.form.patchValue({ prId: 'repo-test' });
    mockReleaseService.analyzeStack.mockReturnValue(throwError(() => new Error('Error')));
    
    component.analyzeWithDeepWiki();
    
    expect(component.error).toBe('DeepWiki no pudo analizar este repositorio.');
    expect(component.analyzing).toBeFalsy();
  });

  it('should submit form successfully', () => {
    component.form.patchValue({
      equipo: 'Equipo A',
      tipo: 'rs',
      descripcion: 'Nueva version'
    });
    
    const expectedFormValue = { ...component.form.value };
    const mockResponse = { id: 1, ...expectedFormValue };
    mockReleaseService.create.mockReturnValue(of(mockResponse));
    
    component.submit();
    
    expect(mockReleaseService.create).toHaveBeenCalledWith(expectedFormValue);
    expect(component.result).toEqual(mockResponse);
    expect(component.submitted).toBeTruthy();
    expect(component.submitting).toBeFalsy();
  });

  it('should handle submit error', () => {
    component.form.patchValue({
      equipo: 'Equipo A',
      tipo: 'rs',
      descripcion: 'Nueva version'
    });
    
    mockReleaseService.create.mockReturnValue(throwError(() => ({ error: { message: 'Error de servidor' } })));
    
    component.submit();
    
    expect(component.error).toBe('Error de servidor');
    expect(component.submitting).toBeFalsy();
  });
});
