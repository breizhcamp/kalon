# Kalon 📋 – Système de Configuration de Conférence

Système de configuration pour l'organisation de conférences.

Table des matières: [🚀 Démarrage Rapide](#-démarrage-rapide) • [🧱 Architecture](#-architecture)

## 🚀 Démarrage Rapide

**Prérequis:** Java 21, Maven (wrapper fourni), Docker (pour PostgreSQL).

### Démarrer la base de données PostgreSQL :

```bash
docker-compose up -d
```

### Démarrer l'application (profil dev) :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Configuration par défaut :**
- URL : http://localhost:4010/
- Base de données : localhost:4011 (base/utilisateur/mot de passe : kalon / kalon / kalonpass)
- OAuth2 Mock Server : localhost:4012

### Faire un appel curl :

Récupérer un token d'accès via OAuth2 Mock Server
```bash
TOKEN=$(curl -s -d 'grant_type=password' \
  -d 'username=my-user' \
  -d 'password=123' \
  -d 'client_id=kalon-user' \
  localhost:4012/kalon/token | jq -r .access_token)
```

Utilisez le client id `kalon-user` pour un utilisateur standard ou `kalon-admin` pour un administrateur.

Appeler un endpoint protégé avec le token :
```bash
curl -H "Authorization: Bearer ${TOKEN}" http://localhost:4010/user
```

### Construire un JAR :

```bash
./mvnw clean package
java -jar target/kalon-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## 🧱 Architecture

Le projet suit une architecture en couches inspirée du pattern hexagonal :

```
application/       (Contrôleurs REST, DTOs, services)
config/            (Configuration typée, annotations personnalisées)
domain/            (Entités métier, cas d'usage, ports)
infrastructure/    (Adaptateurs, JPA repositories, modèles de persistance)
```

### Principes clés :

- **Séparation des couches** : Chaque couche a une responsabilité bien définie
- **Annotations personnalisées** : `@Adapter`, `@UseCase`, `@Tx` marquent clairement les composants
- **Dépendances unidirectionnelles** : Des couches externes vers les couches internes
- **Testabilité** : L'architecture permet des tests unitaires et d'intégration isolés
