import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FragmentsModule } from '../fragments/fragments.module';
import { Utilisateurs } from './utilisateurs/utilisateurs';

/** Administration des comptes (ROLE_ADMIN). */
@NgModule({
  declarations: [Utilisateurs],
  imports: [FragmentsModule, ReactiveFormsModule],
})
export class AdminModule {}
