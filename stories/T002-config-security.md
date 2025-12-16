# Résumé
Configurer Spring Security avec OAuth2 Resource Server pour récupérer les roles de l'utilisateur depuis
le token JWT généré par Keycloak.

# Implémentation
Définir la configuration de Spring Security dans une classe `SecurityConfig` située dans le package `config`.

Ajouter un filter `UserMDCFilter` permettant d'ajouter le login de l'utilisateur dans le MDC avec la clé `user.login`.

Le mapping des roles doit être configuré pour extraire les rôles depuis le claim `realm_access.roles` du token JWT.
Dans le token, les rôles seront `kalon_user` et `kalon_admin`, mais ils doivent être mappés respectivement 
aux rôles Spring Security `ROLE_USER` et `ROLE_ADMIN`.

Pour les tests, on utilisera le projet navikt/mock-oauth2-server avec une image Docker. Le fichier de configuration
`src/test/resources/oauth2-mock-server-config.json` permettra de définir 2 client-id `kalon-user` et `kalon-admin` avec les rôles appropriés.

# Attendus
- La configuration de Spring Security doit permettre de sécuriser les endpoints REST en fonction des rôles extraits.
- Le fichier `application.yaml` doit contenir des placeholders pour la configuration Keycloak.
- Le fichier `application-dev.yaml` doit contenir les configurations spécifiques pour le serveur OAuth2 de test.
- Un controller de test doit être créé pour vérifier l'accès aux endpoints en fonction des rôles utilisateur.
- Le README doit être mis à jour avec l'utilisation du Mock OAuth2 Server pour les tests locaux avec curl.
