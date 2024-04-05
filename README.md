# Kalon

Administration des évènements

## Technologies et architecture 

Le projet est réalisé avec SpringBoot 3 en Kotlin avec une [architecture
hexagonale](https://fr.wikipedia.org/wiki/Architecture_hexagonale)

Les données liées aux événements sont collectées dans une base de données
PostgreSQL et suivent ce schéma : 

![Schéma de base de données avec : 1 entité "event" qui contient des champs de
nom, année, des dates de début et de fin pour l'évènement, le cfp et les
inscriptions, et un lien vers le site; 1 entité "member" contenant des champs
pour le nom et prénom et pour un lien vers la photo de profil; 1 entité "team" qui contient des champs de nom et de description; 1 entité
"contact" avec des champs pour l'id du membre correspondant, le nom de la
plateforme de contact et un lien de contact. Les entités "member", "event" et "team" sont reliées ensemble par une entité "participation", qui représente le fait qu'un membre a participé à un événement dans une équipe, et l'entité "member" est reliée en
OneToMany à l'entité "contact"](./docs/database/kalon_db_v4.png "Schéma de la base de données") 

## Endpoints 

Une liste des endpoints disponibles sous la forme d'un
[Swagger](https://swagger.io/) dans le fichier `docs/api/kalon-openapi_v2.yaml` de ce répertoire

