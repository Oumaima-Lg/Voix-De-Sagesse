# 📖 Voix De Sagesse - Backend

API REST backend pour **Voix De Sagesse**, une plateforme web moderne de partage de contenu inspirationnel permettant aux utilisateurs de créer, partager et découvrir des articles de sagesse et des histoires inspirantes.

## 📋 Table des Matières

- [À Propos](#à-propos)
- [Technologies](#technologies)
- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Lancement de l'Application](#lancement-de-lapplication)
- [Déploiement Docker](#déploiement-docker)
- [Documentation API](#documentation-api)
- [Dépôt Frontend](#dépôt-frontend)
- [Structure du Projet](#structure-du-projet)
- [Sécurité](#sécurité)
- [Contribution](#contribution)

## 🔍 À Propos

**Voix De Sagesse** est une application web full-stack de partage de contenu inspirationnel qui permet aux utilisateurs de :
- Créer et partager du contenu de sagesse et des histoires inspirantes
- Découvrir du contenu pertinent via un feed personnalisé
- Interagir avec la communauté (likes, commentaires, suivi d'utilisateurs)
- Bénéficier d'un système de modération collaborative

Le backend fournit une API REST sécurisée construite avec Spring Boot, offrant une architecture multicouches robuste et scalable.

## 🛠️ Technologies

### Core Framework
- **Java 17** - Langage de programmation
- **Spring Boot 3.4.4** - Framework principal
- **Maven** - Gestion des dépendances

### Sécurité
- **Spring Security** - Framework de sécurité
- **JWT (JSON Web Tokens) 0.11.5** - Authentication sans état
- **Password Encoding** - Chiffrement des mots de passe

### Base de Données
- **MongoDB** - Base de données NoSQL
- **Spring Data MongoDB** - Couche d'accès aux données

### Services
- **Spring Mail** - Service d'envoi d'emails
- **Spring Actuator** - Monitoring et health checks
- **Spring Validation** - Validation des données

### Développement
- **Lombok** - Réduction du code boilerplate
- **Spring DevTools** - Rechargement automatique en développement
- **Docker** - Containerisation

## ✨ Fonctionnalités

### 🔐 Authentification & Autorisation
- Inscription et connexion sécurisées avec JWT
- Gestion des rôles (User, Admin, Moderator)
- Protection des endpoints par rôle
- Refresh token management

### 👤 Gestion des Profils
- Création et modification de profil utilisateur
- Upload de photo de profil
- Suivi d'utilisateurs (followers/following)
- Statistiques utilisateur

### 📝 Gestion du Contenu
- **Deux types d'articles** :
  - 📚 **Sagesse** - Citations et enseignements
  - 📖 **Histoire** - Récits inspirants
- Système de catégorisation
- Tags pour une meilleure organisation
- Brouillons et publications

### 🔄 Interactions Sociales
- Système de likes
- Commentaires et réponses
- Partages
- Notifications en temps réel

### 🔍 Découverte de Contenu
- Feed personnalisé basé sur les intérêts
- Recherche avancée (titre, contenu, tags, auteur)
- Filtrage par catégorie et type
- Tri par popularité, date, pertinence

### 🛡️ Modération
- Système de signalement collaboratif
- Interface d'administration
- Gestion des contenus signalés
- Modération des commentaires

### 📊 Analytics
- Statistiques d'articles
- Métriques utilisateur
- Monitoring via Spring Actuator

## 🏗️ Architecture

### Architecture Multicouches

```
┌─────────────────────────────────────┐
│     Controllers (REST API)          │  ← Endpoints HTTP
├─────────────────────────────────────┤
│     Services (Business Logic)       │  ← Logique métier
├─────────────────────────────────────┤
│     Repositories (Data Access)      │  ← Accès données
├─────────────────────────────────────┤
│     MongoDB Database                │  ← Persistance
└─────────────────────────────────────┘
```

### Principes de Design
- **Separation of Concerns** - Séparation claire des responsabilités
- **Dependency Injection** - Inversion de contrôle avec Spring
- **RESTful API** - Respect des conventions REST
- **DTO Pattern** - Objets de transfert de données
- **Repository Pattern** - Abstraction de l'accès aux données
- **Exception Handling** - Gestion centralisée des erreurs

## 📦 Prérequis

- **Java 17** ou supérieur
- **Maven 3.6+**
- **MongoDB 4.4+** (local ou cloud - MongoDB Atlas)
- **Docker** (optionnel, pour le déploiement conteneurisé)

## 🚀 Installation

1. **Cloner le repository**
```bash
git clone https://github.com/OumaimaLg/Voix-De-Sagesse-Backend.git
cd Voix-De-Sagesse-Backend
```

2. **Installer les dépendances**
```bash
./mvnw clean install
```

3. **Créer le dossier uploads**
```bash
mkdir uploads
```

## ⚙️ Configuration

Créez un fichier `application.properties` dans `src/main/resources/` :

```properties
# ========================================
# Configuration Serveur
# ========================================
server.port=8080
server.servlet.context-path=/api

# ========================================
# Configuration MongoDB
# ========================================
spring.data.mongodb.uri=mongodb://localhost:27017/voixdesagesse
spring.data.mongodb.database=voixdesagesse
# Pour MongoDB Atlas (cloud)
# spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/voixdesagesse

# ========================================
# Configuration JWT
# ========================================
jwt.secret=***REMOVED***
jwt.expiration=86400000
# 24 heures en millisecondes
jwt.refresh-expiration=604800000
# 7 jours

# ========================================
# Configuration Mail
# ========================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# ========================================
# Configuration Upload de Fichiers
# ========================================
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=./uploads

# ========================================
# Configuration Spring Actuator
# ========================================
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized

# ========================================
# Configuration CORS
# ========================================
cors.allowed-origins=http://localhost:3000,http://localhost:5173
cors.allowed-methods=GET,POST,PUT,DELETE,PATCH
cors.allowed-headers=*
cors.allow-credentials=true

# ========================================
# Configuration Logging
# ========================================
logging.level.com.voixdesagesse=DEBUG
logging.level.org.springframework.security=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

## 🏃 Lancement de l'Application

### Mode Développement

```bash
./mvnw spring-boot:run
```

### Mode Production

```bash
# Compiler le projet
./mvnw clean package -DskipTests

# Exécuter le JAR
java -jar target/VoixDeSagesse-0.0.1-SNAPSHOT.jar
```

L'API sera accessible sur `http://localhost:8080/api`

## 🐳 Déploiement Docker

### 1. Build de l'image Docker

```bash
docker build -t voixdesagesse-backend:latest .
```

### 2. Lancer avec Docker Compose

Créez un fichier `docker-compose.yml` :

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:7.0
    container_name: voixdesagesse-mongodb
    restart: always
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_DATABASE: voixdesagesse
    volumes:
      - mongodb_data:/data/db

  backend:
    image: voixdesagesse-backend:latest
    container_name: voixdesagesse-backend
    restart: always
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongodb:27017/voixdesagesse
      JWT_SECRET: ${JWT_SECRET}
      SPRING_MAIL_USERNAME: ${MAIL_USERNAME}
      SPRING_MAIL_PASSWORD: ${MAIL_PASSWORD}
    depends_on:
      - mongodb
    volumes:
      - ./uploads:/app/uploads

volumes:
  mongodb_data:
```

Lancez les services :

```bash
docker-compose up -d
```

## 📚 Documentation API

### Endpoints d'Authentification

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/auth/register` | Inscription d'un nouvel utilisateur |
| POST | `/api/auth/login` | Connexion utilisateur |
| POST | `/api/auth/refresh` | Rafraîchir le token JWT |
| POST | `/api/auth/logout` | Déconnexion utilisateur |
| GET | `/api/auth/me` | Récupérer le profil connecté |

### Endpoints Articles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/articles` | Liste des articles (feed) |
| GET | `/api/articles/{id}` | Détails d'un article |
| POST | `/api/articles` | Créer un article |
| PUT | `/api/articles/{id}` | Modifier un article |
| DELETE | `/api/articles/{id}` | Supprimer un article |
| GET | `/api/articles/search` | Recherche avancée |
| GET | `/api/articles/category/{category}` | Articles par catégorie |

### Endpoints Utilisateurs

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/users/{id}` | Profil utilisateur |
| PUT | `/api/users/{id}` | Modifier le profil |
| POST | `/api/users/{id}/follow` | Suivre un utilisateur |
| DELETE | `/api/users/{id}/unfollow` | Ne plus suivre |
| GET | `/api/users/{id}/followers` | Liste des abonnés |
| GET | `/api/users/{id}/following` | Liste des abonnements |

### Endpoints Interactions

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/articles/{id}/like` | Liker un article |
| DELETE | `/api/articles/{id}/unlike` | Retirer le like |
| POST | `/api/articles/{id}/comments` | Ajouter un commentaire |
| GET | `/api/articles/{id}/comments` | Liste des commentaires |
| DELETE | `/api/comments/{id}` | Supprimer un commentaire |

### Endpoints Admin

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/admin/users` | Liste tous les utilisateurs |
| GET | `/api/admin/reports` | Signalements en attente |
| POST | `/api/admin/articles/{id}/moderate` | Modérer un article |
| DELETE | `/api/admin/users/{id}` | Supprimer un utilisateur |

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

## 🎨 Dépôt Frontend

L'application frontend React est disponible sur :

**🔗 [Voix De Sagesse Frontend](https://github.com/Oumaima-Lg/Voix-De-Sagesse-Frontend)**

## 📁 Structure du Projet

```
Voix-De-Sagesse-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/voixdesagesse/
│   │   │   ├── VoixDeSagesseApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── MongoConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── MailConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ArticleController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── CommentController.java
│   │   │   │   └── AdminController.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Article.java
│   │   │   │   ├── Comment.java
│   │   │   │   ├── Category.java
│   │   │   │   └── ArticleType.java (SAGESSE/HISTOIRE)
│   │   │   ├── dto/
│   │   │   │   ├── AuthRequest.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── ArticleDTO.java
│   │   │   │   └── UserDTO.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ArticleRepository.java
│   │   │   │   ├── CommentRepository.java
│   │   │   │   └── CategoryRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── ArticleService.java
│   │   │   │   ├── CommentService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   └── FileStorageService.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── JwtAuthenticationEntryPoint.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── UnauthorizedException.java
│   │   │   └── util/
│   │   │       ├── Constants.java
│   │   │       └── ValidationUtils.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/com/voixdesagesse/
│           ├── controller/
│           ├── service/
│           └── repository/
├── uploads/                    # Dossier pour les fichiers uploadés
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## 🛡️ Sécurité

### Mesures de Sécurité Implémentées

✅ **Authentification JWT** - Tokens sécurisés avec expiration  
✅ **Password Encoding** - Hachage BCrypt des mots de passe  
✅ **CORS Configuration** - Protection contre les requêtes cross-origin non autorisées  
✅ **CSRF Protection** - Protection contre les attaques CSRF  
✅ **Input Validation** - Validation des données entrantes avec Bean Validation  
✅ **SQL Injection Prevention** - Utilisation de Spring Data MongoDB  
✅ **XSS Protection** - Échappement des données utilisateur  
✅ **Rate Limiting** - Protection contre les attaques par force brute  
✅ **Error Handling** - Gestion centralisée sans exposition d'informations sensibles  
✅ **Role-Based Access Control** - Contrôle d'accès basé sur les rôles  

### Bonnes Pratiques

- Tokens JWT signés avec clé secrète forte
- Mots de passe jamais stockés en clair
- Validation systématique des entrées utilisateur
- Gestion des exceptions sans leak d'informations
- HTTPS recommandé en production
- Variables d'environnement pour les secrets

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. **Fork** le projet
2. Créez une branche feature (`git checkout -b feature/NouvelleFonctionnalite`)
3. Committez vos changements (`git commit -m 'Ajout d'une nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/NouvelleFonctionnalite`)
5. Ouvrez une **Pull Request**

### Conventions de Code

- Respecter les conventions Java et Spring Boot
- Utiliser Lombok pour réduire le boilerplate
- Commenter le code complexe
- Écrire des tests unitaires
- Suivre les principes SOLID

## 📝 Tests

```bash
# Exécuter tous les tests
./mvnw test

# Exécuter avec couverture
./mvnw test jacoco:report
```

## 📄 License

Ce projet est sous licence **MIT**. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👥 Auteurs

- **Oumaima Lg** - *Développeur Full-Stack* - [@OumaimaLg](https://github.com/OumaimaLg)

## 🙏 Remerciements

- Spring Boot Team
- MongoDB Team
- La communauté Open Source

## 📞 Support & Contact

- 🐛 **Issues** : [GitHub Issues](https://github.com/OumaimaLg/Voix-De-Sagesse-Backend/issues)
- 📧 **Email** : support@voixdesagesse.com
- 💬 **Discussions** : [GitHub Discussions](https://github.com/OumaimaLg/Voix-De-Sagesse-Backend/discussions)

---

<div align="center">

**Développé avec ❤️ en utilisant Spring Boot & MongoDB**

⭐ Si ce projet vous plaît, n'hésitez pas à lui donner une étoile !

</div>
