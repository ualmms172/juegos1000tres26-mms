import { NgIf } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { GenericButton } from '../../../../shared/components/generic-button/generic-button';

@Component({
  selector: 'app-login',
  imports: [NgIf, ReactiveFormsModule, RouterLink, GenericButton],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
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

    const { email, password } = this.form.value;

    this.auth.login(email!, password!).subscribe({
      next: () => {
        this.error = null;
        this.router.navigate(['/']);
      },
      error: (err) => {
        if (err.status === 401) {
          this.error = 'Email o contrasena incorrectos.';
        } else if (err.status === 0) {
          this.error = 'No se pudo conectar con el servidor.';
        } else {
          this.error = 'Error al iniciar sesion.';
        }
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
