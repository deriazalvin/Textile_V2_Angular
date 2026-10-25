import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Pagination } from './pagination/pagination';

/**
 * Éléments partagés par les modules métier : directives communes,
 * routage et composants réutilisables (pagination).
 */
@NgModule({
  declarations: [Pagination],
  imports: [CommonModule, RouterModule],
  exports: [CommonModule, RouterModule, Pagination],
})
export class FragmentsModule {}
