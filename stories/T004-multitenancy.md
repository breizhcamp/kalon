# Résumé
Mettre en place la configuration pour que l'application supporte le multitenant.

Le multitenant est appliqué à 2 niveaux :
- La base de données : chaque tenant a son propre schéma PostgreSQL.
- L'authentification : chaque tenant défini un provider OAuth2 différent.

# Implémentation
## Configuration
Implémenter les classes de configuration nécessaires pour définir les propriétés d'un tenant :
 - nom (identifiant)
 - hôte HTTP qui détermine le tenant
 - schéma de la base de données
 - configuration OAuth2 (issuer-uri)

## Base de données
- Configurer Liquibase pour qu'il applique les migrations à chaque schéma de chaque tenant.
- Définir un `TenantResolver` qui stocke le tenant courant dans un `ThreadLocal`.
- Utiliser le `TenantResolver` comme `CurrentTenantIdentifierResolver` pour Hibernate afin de sélectionner le schéma correct en fonction du tenant courant.
- Définir un `ConnectionProvider` comment `MultiTenantConnectionProvider` et qui défini le schéma PostgreSQL en fonction du tenant courant.
- Définir un `Filter` HTTP qui extrait le tenant à partir de l'hôte HTTP de la requête entrante et le stocke dans le `TenantResolver`.
