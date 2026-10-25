import { NgModule } from '@angular/core';
import { FragmentsModule } from '../fragments/fragments.module';
import { Dashboard } from './dashboard';

/** Tableau de bord. */
@NgModule({
  declarations: [Dashboard],
  imports: [FragmentsModule],
})
export class DashboardModule {}
