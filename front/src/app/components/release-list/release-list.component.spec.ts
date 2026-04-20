import { ComponentFixture, TestBed, fakeAsync, tick, discardPeriodicTasks } from '@angular/core/testing';
import { ReleaseListComponent } from './release-list.component';
import { ReleaseService } from '../../services/release.service';
import { of, throwError } from 'rxjs';

describe('ReleaseListComponent', () => {
  let component: ReleaseListComponent;
  let fixture: ComponentFixture<ReleaseListComponent>;
  let mockReleaseService: any;

  beforeEach(async () => {
    mockReleaseService = {
      findAll: jest.fn().mockReturnValue(of([]))
    };

    await TestBed.configureTestingModule({
      imports: [ReleaseListComponent],
      providers: [
        { provide: ReleaseService, useValue: mockReleaseService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReleaseListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load releases on init', () => {
    const mockReleases = [{ equipo: 'Eq', tipo: 'rs', descripcion: 'test' }];
    mockReleaseService.findAll.mockReturnValue(of(mockReleases));
    
    fixture.detectChanges(); // calls ngOnInit
    
    expect(component.releases).toEqual(mockReleases);
    expect(component.loading).toBeFalsy();
  });

  it('should handle error when loading releases', () => {
    mockReleaseService.findAll.mockReturnValue(throwError(() => new Error('Error')));
    
    fixture.detectChanges();
    
    expect(component.error).toBe('No se pudo cargar el listado.');
    expect(component.loading).toBeFalsy();
  });


  it('should return correct badge class', () => {
    fixture.detectChanges();
    expect(component.badgeClass('APROBADO_AUTO')).toBe('badge-approved');
    expect(component.badgeClass('PENDIENTE')).toBe('badge-pending');
    expect(component.badgeClass('OTRO')).toBe('badge-unknown');
  });

  it('should count approved and pending releases', () => {
    fixture.detectChanges();
    component.releases = [
      { estado: 'APROBADO_AUTO', descripcion: '' } as any,
      { estado: 'APROBADO_AUTO', descripcion: '' } as any,
      { estado: 'PENDIENTE', descripcion: '' } as any
    ];
    
    expect(component.countAprobados()).toBe(2);
    expect(component.countPendientes()).toBe(1);
  });
});
