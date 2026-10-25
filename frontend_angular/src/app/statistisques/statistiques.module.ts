import { NgModule } from '@angular/core';
import { FragmentsModule } from '../fragments/fragments.module';
import { Statistisques } from './statistisques';

/** Statistiques de production. */
@NgModule({
  declarations: [Statistisques],
  imports: [FragmentsModule],
})
export class StatistiquesModule {}
