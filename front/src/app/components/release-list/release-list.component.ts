import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReleaseService } from '../../services/release.service';
import { ReleaseRequest } from '../../models/release.model';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-release-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './release-list.component.html',
  styleUrls: ['./release-list.component.scss']
})
export class ReleaseListComponent implements OnInit, OnDestroy {
  releases: ReleaseRequest[] = [];
  loading = true;
  error: string | null = null;
  private pollSub?: Subscription;

  constructor(private releaseService: ReleaseService) {}

  ngOnInit(): void {
    this.load();
    this.pollSub = interval(10000)
      .pipe(switchMap(() => this.releaseService.findAll()))
      .subscribe({ next: (data) => (this.releases = data) });
  }

  ngOnDestroy(): void {
    this.pollSub?.unsubscribe();
  }

  load(): void {
    this.loading = true;
    this.releaseService.findAll().subscribe({
      next: (data) => { this.releases = data; this.loading = false; },
      error: () => { this.error = 'No se pudo cargar el listado.'; this.loading = false; }
    });
  }

  badgeClass(estado?: string): string {
    if (estado === 'APROBADO_AUTO') return 'badge-approved';
    if (estado === 'PENDIENTE') return 'badge-pending';
    return 'badge-unknown';
  }

  tipoLabel(tipo?: string): string {
    const map: Record<string, string> = { rs: 'RS', fx: 'FX', cv: 'CV' };
    return tipo ? (map[tipo] || tipo.toUpperCase()) : '—';
  }

  formatDate(fecha?: string): string {
    if (!fecha) return '—';
    return new Date(fecha).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
  }

  countAprobados(): number {
    return this.releases.filter(r => r.estado === 'APROBADO_AUTO').length;
  }

  countPendientes(): number {
    return this.releases.filter(r => r.estado === 'PENDIENTE').length;
  }
}
