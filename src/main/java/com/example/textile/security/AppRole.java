package com.example.textile.security;

/**
 * Roles applicatifs. Les droits sont differents selon le role :
 * ADMIN      -> configuration et administration
 * MANAGER    -> gestion metier (clients, produits, commandes, planning)
 * OPERATOR   -> operations quotidiennes de production et de livraison
 * USER       -> consultation uniquement
 */
public enum AppRole {
    ROLE_ADMIN,
    ROLE_MANAGER,
    ROLE_OPERATOR,
    ROLE_USER
}