# Résumé
Mettre en place la configuration pour effectuer des tests d'intégrations.

Il faut faire en sorte que la base de données soit démarrée ainsi que le mock OAuth2 server.

# Implémentation
A l'aide de Spring Boot test, démarrer l'appli avec le profil `it`. 
Utiliser Testcontainers pour démarrer une base de données PostgreSQL et un serveur OAuth2 mocké.

# Attendus
- La base de données PostgreSQL est démarrée avant les tests.
- Le serveur OAuth2 mocké est démarré avant les tests.
- Les tests d'intégration se lancent correctement avec la configuration adéquate.
- Un test d'intégration simple est implémenté pour vérifier que la configuration fonctionne correctement.
