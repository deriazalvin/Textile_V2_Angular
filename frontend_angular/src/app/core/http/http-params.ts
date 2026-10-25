import { HttpParams } from '@angular/common/http';

export type QueryValue = string | number | boolean | null | undefined;

/**
 * Construit les `HttpParams` d'une requête en ignorant les valeurs vides.
 * Évite d'envoyer `search=` ou `active=undefined` au backend.
 */
export function toHttpParams(query: object): HttpParams {
  let params = new HttpParams();
  for (const [cle, valeur] of Object.entries(query) as [string, QueryValue][]) {
    if (valeur !== null && valeur !== undefined && valeur !== '') {
      params = params.set(cle, String(valeur));
    }
  }
  return params;
}
