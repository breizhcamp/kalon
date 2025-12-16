# Histoire
En tant qu'admin, je dois pouvoir créer un événement.

Un événement doit avoir un identifiant unique (par exemple breizhcamp-2025), un nom (par exemple BreizhCamp 2025), 
une date de début, une date de fin, un site web (optionnel) et une adresse (optionnel).
Tous les champs sont modifiables sauf l'identifiant.

# Règles d'acceptation
- L'admin peut créer un événement en fournissant les informations requises.
- Le système doit valider que l'identifiant de l'événement est unique.
- Le système doit valider que la date de fin est postérieure à la date de début.
- Une fois l'événement créé, il doit être stocké dans la base de données.
