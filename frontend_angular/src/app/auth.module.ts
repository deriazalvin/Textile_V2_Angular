import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FragmentsModule } from './fragments/fragments.module';
import { Inscription } from './inscription/inscription';
import { Login } from './login/login';

/** Connexion et inscription (hors coquille applicative). */
@NgModule({
  declarations: [Login, Inscription],
  imports: [FragmentsModule, ReactiveFormsModule],
})
export class AuthModule {}
