import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FragmentsModule } from '../fragments/fragments.module';
import { Formulaire } from './formulaire/formulaire';
import { Liste } from './liste/liste';

/** Catalogue produits : liste et formulaire. */
@NgModule({
  declarations: [Liste, Formulaire],
  imports: [FragmentsModule, ReactiveFormsModule],
})
export class ProduitsModule {}
