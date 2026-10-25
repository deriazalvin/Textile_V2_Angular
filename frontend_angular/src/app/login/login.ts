import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

/**
 * Page de connexion (HTTP Basic).
 * En cas de succès, redirige vers l'URL demandée initialement (`?retour=`).
 */
@Component({
  standalone: false,
  selector: 'app-login',
  styleUrl: './login.scss',
  templateUrl: './login.html',
})
export class Login implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly formulaire = this.fb.nonNullable.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });

  readonly chargement = signal(false);
  readonly erreur = signal<string | null>(null);
  readonly deconnecte = signal(
    this.route.snapshot.queryParamMap.has('deconnexion'),
  );

  ngOnInit(): void {
    if (this.auth.connecte()) {
      void this.router.navigateByUrl('/dashboard');
    }
  }

  seConnecter(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    this.chargement.set(true);
    this.erreur.set(null);

    this.auth.connecter(this.formulaire.getRawValue()).subscribe({
      next: () => {
        const retour = this.route.snapshot.queryParamMap.get('retour');
        const destination = retour && retour.startsWith('/') ? retour : '/dashboard';
        void this.router.navigateByUrl(destination);
      },
      error: (erreur: HttpErrorResponse) => {
        this.chargement.set(false);
        this.erreur.set(
          erreur.status === 401
            ? 'Identifiant ou mot de passe incorrect.'
            : 'Connexion impossible. Vérifiez que le serveur est démarré.',
        );
      },
    });
  }

  champInvalide(nom: 'username' | 'password'): boolean {
    const champ = this.formulaire.controls[nom];
    return champ.invalid && (champ.touched || champ.dirty);
  }
}
