import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReleaseService } from '../../services/release.service';
import { ReleaseRequest } from '../../models/release.model';

@Component({
  selector: 'app-release-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './release-form.component.html',
  styleUrls: ['./release-form.component.scss']
})
export class ReleaseFormComponent implements OnInit {
  form!: FormGroup;
  submitting = false;
  submitted = false;
  result: ReleaseRequest | null = null;
  error: string | null = null;
  analyzing = false;

  tipoOptions = [
    { value: 'rs', label: 'RS — Release de Software' },
    { value: 'fx', label: 'FX — Fix / Hotfix' },
    { value: 'cv', label: 'CV — Config / Variable' }
  ];

  constructor(private fb: FormBuilder, private releaseService: ReleaseService) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      equipo: ['', [Validators.required, Validators.minLength(2)]],
      tipo: ['rs', Validators.required],
      descripcion: ['', Validators.required],
      prId: [''],
      cobertura: [null, [Validators.min(0), Validators.max(100)]],
      stack: [''],
      notificationEmail: ['', Validators.email]
    });
  }

  get f() { return this.form.controls; }

  analyzeWithDeepWiki(): void {
    const prId = this.form.get('prId')?.value;
    if (!prId) {
      this.error = 'Ingresa un PR o repositorio para analizar.';
      return;
    }

    this.analyzing = true;
    this.releaseService.analyzeStack(prId).subscribe({
      next: (res) => {
        this.form.patchValue({ stack: res.stack });
        this.analyzing = false;
      },
      error: () => {
        this.error = 'DeepWiki no pudo analizar este repositorio.';
        this.analyzing = false;
      }
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.submitting = true;
    this.error = null;
    this.result = null;

    this.releaseService.create(this.form.value).subscribe({
      next: (res) => {
        this.result = res;
        this.submitted = true;
        this.submitting = false;
        this.form.reset({ tipo: 'rs' });
      },
      error: (err) => {
        this.error = err?.error?.message || 'Error al procesar la solicitud.';
        this.submitting = false;
      }
    });
  }

  reset(): void {
    this.submitted = false;
    this.result = null;
    this.error = null;
  }
}
