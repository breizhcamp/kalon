# Histoire
En tant que module frontend, je dois pouvoir récupérer ma configuration pour mon tenant afin de me configurer.

Pour le module 'orga', la configuration est composé de :
- des informations du serveur keycloak :
  - son URL
  - son royaume
  - son client id
- URL de Kalon

Lors de l'appel à la resource REST, l'en-tête 'X-Tenant-Host' contient le nom d'hôte 
dont on souhaite récupérer la configuration et permet de récuépérer la configuration pour le tenant et le module.

# Règles d'acceptation
Lors de l'appel à la resource REST, la configuration pour le module et le tenant est retourné.
