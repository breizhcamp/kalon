# Résumé
Initialisation d'un projet Kotlin Spring Boot (dernière version) avec les dépendances de base comprenant :
- Spring WebMVC
- Spring Data JPA (avec driver PostgreSQL)
- Spring Security (avec OAuth2 Resource Server)
- Spring Boot Actuator
- Spring Boot DevTools
- Spring Configuration Processor

Utilisation d'une base de données PostgreSQL avec un docker-compose.yml pour le développement local.

# Implémentation
Créer le projet avec Spring Initializr (https://start.spring.io/) en sélectionnant les dépendances mentionnées ci-dessus.
Créer le fichier `compose.yaml` pour démarrer PostgreSQL sur le port 4011.

Créer la structure de base du projet avec les packages nécessaires respectant une architecture hexagonale :
- `org.breizhcamp.kalon`
  - `application`
  - `config`
  - `domain`
  - `infrastructure`

Dans le package `config/annotations`, créer des annotations personnalisées `@Adapter`, `@UseCase` et `@Tx`
pour marquer les différentes couches de l'architecture hexagonale.

Créer un fichier `application-dev.yaml` pour démarrer le serveur sur le port 4010 et configurer la connexion à la base de données PostgreSQL.
Y ajouter le log des requêtes SQL.

Créer un fichier README.md avec des instructions pour démarrer l'application et la base de données en local.

# Attendus
- L'application doit démarrer correctement avec une connexion à la base de données PostgreSQL.
- Le README doit contenir des instructions claires pour démarrer l'application et la base de données en local.
