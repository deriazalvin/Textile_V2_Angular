import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi,
} from '@angular/common/http';
import {
  NgModule,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AdminModule } from './admin/admin.module';
import { AppRoutingModule } from './app-routing.module';
import { App } from './app';
import { AuthModule } from './auth.module';
import { ClientsModule } from './clients/clients.module';
import { CommandesModule } from './commandes/commandes.module';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { AuthService } from './core/services/auth.service';
import { DashboardModule } from './dashboard/dashboard.module';
import { Erreur } from './erreur/erreur';
import { LayoutModule } from './layout/layout.module';
import { ProduitsModule } from './produits/produits.module';
import { StatistiquesModule } from './statistisques/statistiques.module';

/**
 * Module racine de l'application.
 * Déclare le composant racine et importe les modules métier.
 */
@NgModule({
  declarations: [App, Erreur],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LayoutModule,
    AuthModule,
    DashboardModule,
    ClientsModule,
    ProduitsModule,
    CommandesModule,
    StatistiquesModule,
    AdminModule,
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    // Restaure la session (sessionStorage) avant la première activation de route.
    provideAppInitializer(() => inject(AuthService).restaurer()),
  ],
  bootstrap: [App],
})
export class AppModule {}
