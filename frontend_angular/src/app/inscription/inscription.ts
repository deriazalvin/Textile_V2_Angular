import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

/** Vérifie que les deux mots de passe saisis sont identiques. */
const motsDePasseIdentiques: ValidatorFn = (
  groupe: AbstractControl,
): ValidationErrors | null => {
  const motDePasse = groupe.get('password')?.value;
  const confirmation = groupe.get('confirmation')?.value;
  return motDePasse === confirmation ? null : { confirmation: true };
};

/**
 * Inscription publique : crée un compte de consultation (ROLE_USER)
 * puis connecte automatiquement l'utilisateur.
 */
@Component({
  standalone: false,
  selector: 'app-inscription',
  styleUrl: './inscription.scss',
  templateUrl: './inscription.html',
})
export class Inscription {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly formulaire = this.fb.nonNullable.group(
    {
      displayName: [''],
      username: [
        '',
        [Validators.required, Validators.minLength(3), Validators.maxLength(60)],
      ],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmation: ['', [Validators.required]],
    },
    { validators: motsDePasseIdentiques },
  );

  readonly chargement = signal(false);
  readonly erreur = signal<string | null>(null);

  sInscrire(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }

    this.chargement.set(true);
    this.erreur.set(null);

    const valeurs = this.formulaire.getRawValue();
    this.auth
      .inscrire({
        username: valeurs.username.trim(),
        password: valeurs.password,
        displayName: valeurs.displayName.trim() || undefined,
      })
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/dashboard');
        },
        error: (erreur: HttpErrorResponse) => {
          this.chargement.set(false);
          const message = (erreur.error as { message?: string } | null)?.message;
          this.erreur.set(
            message ??
              'Inscription impossible. Vérifiez que le serveur est démarré.',
          );
        },
      });
  }

  champInvalide(nom: 'displayName' | 'username' | 'password' | 'confirmation'): boolean {
    const champ = this.formulaire.controls[nom];
    if (nom === 'confirmation') {
      return (
        (champ.invalid || this.formulaire.hasError('confirmation')) &&
        (champ.touched || champ.dirty)
      );
    }
    return champ.invalid && (champ.touched || champ.dirty);
  }
}
