import { NgModule } from '@angular/core';
import { FragmentsModule } from '../fragments/fragments.module';
import { Barre_navigation } from './barre_navigation/barre_navigation';

/** Coquille applicative : sidebar, topbar et zone de contenu. */
@NgModule({
  declarations: [Barre_navigation],
  imports: [FragmentsModule],
})
export class LayoutModule {}
