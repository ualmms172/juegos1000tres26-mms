import { NgIf } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { GenericButton } from '../../../../shared/components/generic-button/generic-button';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-register',
  imports: [NgIf, ReactiveFormsModule, RouterLink, GenericButton],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  error: string | null = null;
  submitted = false;
  form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.form = this.fb.group({
      nombre: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = null;
    this.form.markAllAsTouched();

    if (this.form.invalid) {
      this.error = 'Revisa los campos marcados.';
      return;
    }

    const { email } = this.form.value;

    forkJoin({
      emailExists: this.auth.existeEmail(email!)
    }).subscribe({
      next: ({ emailExists }) => {
        if (emailExists) {
          this.error = 'El email ya esta registrado.';
          this.cdr.markForCheck();
          return;
        }

        this.auth.register(this.form.value as Usuario).subscribe({
          next: () => {
            this.error = null;
            this.router.navigate(['/login']);
          },
          error: (err: HttpErrorResponse) => {
            if (err.status === 409) {
              this.error = 'El email ya existe.';
            } else if (err.status === 0) {
              this.error = 'No se pudo conectar con el servidor.';
            } else {
              this.error = 'Error en el registro.';
            }
            this.cdr.markForCheck();
          }
        });
      },
      error: () => {
        this.error = 'No se pudo validar el email.';
        this.cdr.markForCheck();
      }
    });
  }

  getFieldError(controlName: string): string | null {
    const control = this.form.get(controlName);
    if (!control) return null;

    if (!control.touched && !this.submitted) return null;
    if (!control.errors) return null;

    if (control.errors['required']) return 'Este campo es obligatorio.';
    if (control.errors['email']) return 'Introduce un email válido.';

    return null;
  }
}
