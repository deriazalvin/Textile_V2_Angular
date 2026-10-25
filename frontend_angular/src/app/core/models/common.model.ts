/** Réponse paginée renvoyée par les endpoints REST métier. */
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  sort: string;
}

/** Réponse `Page` brute de Spring Data (endpoint /api/users). */
export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/** Paramètres de pagination communs. */
export interface PageQuery {
  page?: number;
  size?: number;
  /** Format Spring : `champ,direction` (ex. `name,asc`). */
  sort?: string;
}

/** Corps d'erreur normalisé du backend (GlobalExceptionHandler). */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string> | null;
}
