import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';
import { AuthSession } from '../auth/models/auth-session.model';
import { GenericButton } from '../../shared/components/generic-button/generic-button';

@Component({
  selector: 'app-home',
  imports: [GenericButton, NgIf],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  usuario: AuthSession | null = null;

  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  ngOnInit(): void {
    this.auth.loadSession().subscribe(user => {
      this.usuario = user;
    });
    this.auth.currentUser$.subscribe(user => {
      this.usuario = user;
    });
  }

  entrarInvitado(): void {
    this.auth.guest().subscribe({
      next: () => this.router.navigate(['/sala']),
      error: () => this.router.navigate(['/login'])
    });
  }

  cerrarSesion(): void {
    this.usuario = null;
    this.auth.logout().subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.router.navigate(['/'])
    });
  }
}
