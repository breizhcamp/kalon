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

## Installation du poste en local

### pre-commit

On utilise le framework pre-commit afin d'analyser et de corriger la syntaxe des fichiers.

L'exécution se fait au moment du commit git, ou en exécutant la commande ```pre-commit run --all-files```.

Pour installer pre-commit, plusieurs méthodes :

* En utilisant pip : ```pip install pre-commit```
* En utilisant mise : ```mise use -g pre-commit```

On installera ensuite les hooks à partir du fichier de configuration **.pre-commit-config.yaml** :

```pre-commit install```

Hooks mise en place :

* **trailing-whitespace** : supprimer les espaces en fin de ligne
* **end-of-file-fixer** : pour que les fichiers se terminent par une nouvelle ligne
* **check-yaml** : vérifier la syntaxe des fichiers yaml
* **check-json** : vérifier la syntaxe des fichiers json
* **check-added-large-files** : pour ne pas commit les gros fichiers, comme les binaires
* **check-merge-conflict** : vérifier qu'il n'y a pas de conflits de fusion git
* **gitleaks** : pour identifier les secrets dans les fichiers à commit

### run.sh

On utilisera l'utilitaire **run.sh** pour gérer les containers Docker, générer une image Docker du projet **kalon**
et analyser le code.

Usage et commandes :

```
Usage run.sh [OPTIONS] COMMAND
Commands:
start               Initialize and creating containers
stop                Stopping containers
down                Stopping/removing containers, networks and volumes
build               Building docker image
lint                Lint the Dockerfile
gitleaks            Detecting secrets like passwords, API keys, and tokens in files
Options:
-p, --prod          Starting kalon container
-v, --verbose       Make the command more talkative
-h, --help          Display help
```

### Gérer les containers

Pour démarrer, stopper ou supprimer les containers, on utilisera les commandes suivantes :

* ```./run.sh start```
* ```./run.sh stop```
* ```./run.sh down```

L'option **-p** ou **--prod** permet également de démarrer le container kalon, dont l'image est construite à partir des
sources en local.

Exemple :

```
./run.sh start -p
Creating and starting Docker containers
...
[+] Running 3/3
✔ kalon                    Built                                                                                                                                                                                                                        0.0s
✔ Container kalon-kalon-1  Started                                                                                                                                                                                                                      0.1s
✔ Container kalon-db       Started
```

### Analyser la syntaxe du code

Pour analyser le fichier Dockerfile, on utilisera la commande ```./run.sh lint```.

### Identifier les secrets

Pour vérifier que l'on ne commit pas des mots de passe, clés d'API ou token dans le dépôt git, on utilisera
l'utilisera gitleaks pour analyser les fichiers.

Exemple :

```
./run.sh gitleaks
Execute a scan with gitleaks and pre-commit to find secrets
Detect hardcoded secrets.................................................Passed
- hook id: gitleaks
- duration: 0.37s

INF 0 commits scanned.
INF scanned ~0 bytes (0) in 134ms
INF no leaks found
```
