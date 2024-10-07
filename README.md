# Application REST Sécurisée - Quest

## Objectif

L'objectif de ce quest est de vous apprendre à mettre en place une application backend sécurisée avec **Spring** et une interface frontend en **React**, en suivant plusieurs étapes progressives. Le but final est de créer un **service REST sécurisé** avec des tokens JWT pour l'authentification.

## Étapes du Quest

### 1. Mise en place du projet

- Initialiser le projet **Spring Boot** et configurer les dépendances nécessaires (Spring Web, Spring Data JPA, etc.).
- Mettre en place le frontend en **React** pour communiquer avec le backend.

### 2. Connexion à la base de données

- Configurer une base de données (par exemple, MySQL ou PostgreSQL) pour l'application.
- Utiliser un ORM comme **Hibernate** pour gérer les entités et les opérations CRUD (Create, Read, Update, Delete).

### 3. Sécurisation de l'application avec JWT

- Implémenter la sécurisation de l'application avec des **tokens JWT (JSON Web Tokens)**.
- Cette étape est cruciale et doit être réalisée avec attention pour garantir la sécurité de l'API REST.
- Les utilisateurs pourront se connecter et obtenir un token JWT pour accéder aux services protégés.

### 4. Création des contrôleurs REST

- Développer les **contrôleurs REST** pour permettre l'interaction entre le frontend et le backend.
- Implémenter les endpoints pour les opérations CRUD sur les données.

### 5. Intégration avec l'interface web (React)

- Faire en sorte que le frontend en **React** communique avec le backend Spring via des appels HTTP (Axios, Fetch API).
- Gérer l'authentification sur le frontend en envoyant et stockant les tokens JWT.

### 6. Projet final

- Le quest se termine avec un **projet libre** où vous choisissez le sujet.
- Ce projet reprendra les éléments développés pendant les étapes précédentes, avec des attentes précises :
    - Sécurisation via JWT
    - Connexion à une base de données
    - API REST fonctionnelle
    - Interaction fluide entre le frontend (React) et le backend (Spring)

## Technologies Utilisées

### Backend (Spring Boot)
- **Spring Boot** : Framework Java pour créer des applications web robustes.
- **Spring Security** : Utilisé pour sécuriser l'application avec JWT.
- **Hibernate/JPA** : Pour la gestion des données en base via ORM.
- **JWT** : Pour l'authentification et l'autorisation.

### Frontend (React)
- **React** : Framework JavaScript pour le développement d'une interface utilisateur dynamique.
- **Axios** ou **Fetch API** : Pour les appels HTTP au backend.
- **React Router** : Pour la gestion des routes dans l'application frontend.

## Prérequis

- Java 11 ou supérieur
- Node.js et npm (pour le frontend en React)
- MySQL, PostgreSQL ou une autre base de données relationnelle
- Postman ou cURL (pour tester les endpoints API)