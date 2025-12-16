# Kalon 📋 – Système de Configuration de Conférence

Système de configuration pour l'organisation de conférences.

Table des matières: [🚀 Démarrage Rapide](#-démarrage-rapide) • [🧱 Architecture Hexagonale](#-architecture-hexagonale)

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
- Base de données : localhost:4011 (utilisateur/mot de passe : postgres / postgres)

### Construire un JAR :

```bash
./mvnw clean package
java -jar target/kalon-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## 🧱 Architecture Hexagonale

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

