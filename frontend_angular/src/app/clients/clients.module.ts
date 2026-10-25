import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FragmentsModule } from '../fragments/fragments.module';
import { Carte } from './carte/carte';
import { Formulaire } from './formulaire/formulaire';
import { Liste } from './liste/liste';

/** Référentiel clients : liste, formulaire, carte. */
@NgModule({
  declarations: [Liste, Formulaire, Carte],
  imports: [FragmentsModule, FormsModule, ReactiveFormsModule],
})
export class ClientsModule {}
