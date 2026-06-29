# Medilabosolution Documentation

## documentation d'installation

- **Java 21** doit être installé sur la machine.  
  Vérifiez avec la commande : java -version
  
- **Maven** est nécessaire pour compiler et gérer les dépendances 

- **Docker** est nécessaire pour executer les microservices en conteneurs. 

- **Les dépendances externes** suivantes doivent être accessibles :    
- MongoDB pour la base de données NoSQL.  
- MySQL pour la base de données SQL (base patients).  

- **Les fichiers de configurations** 
- Chaque microservice dispose de son application.properties si besoin pour adapter selon son environnement si nécessaire  
Il est situé ici src/main/resources/application.properties
	
- **Installation et build**  
  - Cloner le repository git : git clone {URL du projet}
  - Compiler avec Maven (avec les tests unitaires) : mvn clean install
  - Construire et démarrer les images Docker : docker-compose up --build -d
  - Se connecter à l'application : http://localhost:8080
  - Arrêter les images Docker : docker-compose down

## Schémas de base de données MYSQL pour la base Patient
```SQL
CREATE TABLE `patient` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `date_naissance` date NOT NULL,
  `genre` char(1) NOT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `telephone` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `patient_chk_1` CHECK ((`genre` in (_utf8mb4'M',_utf8mb4'F'))),
  CONSTRAINT `patient_chk_2` CHECK (regexp_like(`telephone`,_utf8mb4'^[0-9]{10}$'))
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```
## Documentation fonctionnelle de l'application
- **Rappel du besoin**  
Application destinées au personnel soignant, permettant de gérer des informations administratives et médicales, et notamment orienter le médecin dans l'analyse du risque de contracter un diabète de type 2.  
Cette application doit pouvoir :  
	- Créer de nouveaux patients et pouvoir modifier les informations les concernants  
	- Créer des notes sous forme de compte rendu à la suite de la visite d'un patient  
	- Calculer un "risque diabète" en fonction de l'age, du sexe et des informations contenues dans les comptes rendus du médecin
- **Objectif de l'application**  
	- Dans sa V1, je dois pouvoir sur l'application :  
	- créer de nouveaux patients et les modifier.  
	- ajouter des notes.  
	- calculer le risque des patients.  
	- L'application doit être sécurisée. Elle doit également être réalisée en microservices et conteneurisée (via Docker). C'est une application Synchrone.  
	- Pour la V2, on pourrait envisager de pouvoir supprimer des patients, supprimer des notes et calculer plus finement le risque diabète. On pourrait également envisager des accès différents en fonction des profils mis en place (via la sécurité)  
- **Descrition des fonctionnalités**  
	- **rechercher un patient :**  
	- Description : rechercher un patient dans la base de données.  
	- Entrées : Nom, prénom.  
	- Sorties : La fiche administrative du patient trouvé.  
	- Règles métier : Si le patient n'est pas retrouvé par son nom et prénom, l'application propose de le créer.  
	- **créer un patient :**  
	- Description : enregistrer les informations administratives d’un nouveau patient dans la base de données.  
	- Entrées : Nom, prénom, date de naissance, sexe, adresse, téléphone.  
	- Sorties : Création d’un nouvel enregistrement patient avec un identifiant unique.  
	- Règles métier : L'adresse et le téléphone peuvent être non-reneignés. Les autres champs sont obligatoires. Si le patient existe déjà en base de données, il est impossible de le re-créer.   
	- **modifier un patient :**  
	- Description : modifier les informations administratives d’un patient déjà connu de la base de données.  
	- Entrées : Nom, prénom, date de naissance, sexe, adresse, téléphone.  
	- Sorties : modification des informations.  
	- Règles métier : Lors d'une modification, la date de naissance doit obligatoirement être redemandée et ressaisie.  
	- **créer une note :**  
	- Description : enregistrer le compte rendu médical de rendez-vous pour pouvoir le relire plus tard  
	- Entrées : le compte rendu.  
	- Sorties : retour sur la page globale des notes.  
	- Règles métier : Le médecin peut créer autant de notes qu'il ne souhaite mais ne peut ni les modifier ni les supprimer une fois enregistrées. 
	- **consulter les notes :**  
	- Description : consulter les notes précédentes pour un patient.    
	- Entrées : juste aller sur la page globale.  
	- Sorties : idem.  
	- Règles métier : le médecin peut lire les notes mais n'a pas la possibilité de les modifier ou suprimmer. 
	- **calculer le risque diabète :**  
	- Description : consulter le risque diabète pour un patient.    
	- Entrées : cliquer sur le bouton risque diabète sur la page des informations administratives.  
	- Sorties : le risque diabète classée en 4 catégories Apparition précoce / danger / limité / aucun risque.  
	- Règles métier : le médecin peut seulement consulter le risque. 
- **Acteur / utilisateur**  
Actuellement un seul profil est configuré. Cependant avec l'utilisation de Keycloak la possibilité de mettre en place différents profils d'utilisateur avec des accès différents peut être mis en place relativement simplement.  
- **Contraintes et exigences**  
L’application doit garantir un niveau élevé de sécurité, notamment dans la gestion des données administratives et médicales.  
L’authentification et l’autorisation sont assurées via des tokens OAuth 2.0, implémentés avec Keycloak.  
L'application est une application SpringBoot. 
L’architecture repose sur une communication synchrone entre les microservices.  
La base de données relationnelle utilisée est conçue selon la troisième forme normale (3NF) pour assurer une organisation optimale des données, éviter les redondances, et faciliter la maintenance.  

**Architecture fonctionnelle**   
L’application est conçue suivant une architecture microservices, où chaque service gère un domaine fonctionnel spécifique :

- **Module Patient :** gère les données administratives et médicales des patients, stockées dans une base SQL normalisée en 3NF.  
- **Module Notes :**  permet la création et la gestion des notes médicales sous forme de compte-rendus, stockées dans une base NoSQL (MongoDB) pour conserver leur format libre.  
- **Module Risk :**  calcule le risque de diabète en interrogeant les autres microservices de manière synchrone.  
- **Module Common :**  contient les DTO (Data Transfer Objects), constantes et énumérations partagés par tous les microservices backend, favorisant la cohérence et la réutilisation du code.  
- **Module Security :**  Gère la sécurité, les connexions sécurisées avec Keycloak et Oauth2, assure la validation et la gestion des tokens via JWT, et contrôle les rôles et permissions.  
- **Module Gateway :**  centralise le routage des requêtes, applique la sécurité (authentification et autorisation via le composant Security), et simplifie l’accès depuis le frontend.  
- **Module FrontEnd :**  affiche les informations et permet les interactions utilisateur (via FeignClient).  

Chaque microservice, ainsi que la passerelle API et le composant Security, sont conteneurisés avec Docker.
``` 
+----------------+       +-------------------+       +------------------+  
|                |       |                   |       |                  |  
| Module Frontend+-------+   Module Gateway  +-------+  Module Security |  
|  (Application) |       | (Routage & Auth)  |       | (Auth & Keycloak)|  
+----------------+       +---------+---------+       +------------------+  
                                     |  
            ---------------------------------------------------------
            |          |             |               |              |
+----------------+  +------------------+  +--------------+  +------------------+
|                |  |                  |  |              |  |                  |
| Module         |  | Module           |  | Module       |  | Module           |
| Patient        |  | Notes            |  | Risk         |  | Common           |
| (BDD SQL 3NF)  |  | (BDD NoSQL)      |  | (Pas de BDD) |  | (DTO, constantes)|
+----------------+  +------------------+  +--------------+  +------------------+
```