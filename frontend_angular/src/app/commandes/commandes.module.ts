import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FragmentsModule } from '../fragments/fragments.module';
import { CloturerEtape } from './cloturer-etape/cloturer-etape';
import { Detail } from './detail/detail';
import { Formulaire } from './formulaire/formulaire';
import { Liste } from './liste/liste';
import { Livraison } from './livraison/livraison';
import { Production } from './production/production';
import { Qualite } from './qualite/qualite';
import { Retard } from './retard/retard';

/** Commandes et workflow de production. */
@NgModule({
  declarations: [
    Liste,
    Formulaire,
    Detail,
    Production,
    Qualite,
    Livraison,
    CloturerEtape,
    Retard,
  ],
  imports: [FragmentsModule, ReactiveFormsModule],
})
export class CommandesModule {}
