# CICOR - Système de Gestion des Articles et Cartons

## Table des matières

- [Description](#description)
- [Fonctionnalités principales](#fonctionnalités-principales)
- [Prérequis](#prérequis)
- [Technologies utilisées](#technologies-utilisées)
- [Installation et configuration](#installation-et-configuration)
- [Structure du projet](#structure-du-projet)
- [Démarrage de l'application](#démarrage-de-lapplication)
- [Gestion de la base de données](#gestion-de-la-base-de-données)
- [Problèmes courants](#problèmes-courants)
- [Contact](#contact)
- [Licence](#licence)

## Description

CICOR est une applicatsion JavaFX complète pour la gestion des articles et des cartons au sein de l'entreprise Cicor Berrechid (anciennement Éolane). Cette solution modernise les processus de production en automatisant la traçabilité des articles, la gestion des cartons, l'impression d'étiquettes et la génération de rapports.

L'application permet de :
- Scanner et valider des adresses MAC d'articles
- Gérer les catégories de produits
- Créer et suivre des cartons de production
- Imprimer des étiquettes avec code-barres
- Générer des rapports Excel détaillés
- Configurer des imprimantes réseau

## Fonctionnalités principales

- 🔐 **Authentification sécurisée** avec deux profils (Administrateur et Utilisateur)
- 📦 **Gestion des cartons** avec numérotation automatique et calcul de la date de fabrication
- 📱 **Scan d'articles** avec validation en temps réel des adresses MAC
- 🖨️ **Impression d'étiquettes** avec support des imprimantes CAB
- 📊 **Export de rapports** en format Excel avec mise en forme professionnelle
- ⚙️ **Configuration avancée** des paramètres d'impression et de connexion base de données

## Prérequis

- Java 17 ou supérieur
- MySQL 8.0 ou supérieur
- Maven 3.6 ou supérieur
- XAMPP (recommandé pour MySQL)

## Technologies utilisées

- **Java 17** : Langage de programmation principal
- **JavaFX 17** : Framework d'interface utilisateur
- **MySQL 8.0** : Base de données relationnelle
- **Maven** : Gestion des dépendances et build
- **HikariCP** : Pool de connexions à la base de données
- **Apache POI** : Génération de fichiers Excel
- **Commons Net** : Communication avec les imprimantes réseau

## Installation et configuration

### 1. Cloner le dépôt

```bash
git clone https://github.com/Mohamed-Amine-NIHMATOUALLAH/CICOR.git
cd CICOR
```

### 2. Configuration de la base de données

- Démarrer XAMPP et activer MySQL
- Importer le fichier `cicor_db.sql` dans phpMyAdmin ou MySQL Workbench

### 3. Configuration de l'application

Le projet utilise un fichier de configuration pour la base de données. Assurez-vous que les paramètres de connexion dans la classe `DatabaseManager` correspondent à votre environnement :

```java
static final String DB_URL = "jdbc:mysql://localhost:3306/cicor_db";
static final String USER = "root";
private static final String PASS = "";
```

### 4. Compilation avec Maven

```bash
mvn clean compile
```

### 5. Exécution de l'application

```bash
mvn javafx:run
```

## Structure du projet

```
CICOR/
├── src/main/java/com/example/cicor/
│   ├── Controllers/          # Contrôleurs JavaFX
│   ├── database/             # Accès aux données (DAO)
│   ├── models/               # Modèles de données
│   └── services/             # Services métier
├── src/main/resources/
│   └── com/example/cicor/
│       ├── images/           # Images de l'interface
│       ├── sounds/           # Sons d'alerte
│       ├── styles/           # Feuilles de style CSS
│       └── views/            # Fichiers FXML des interfaces
├── cicor_db.sql              # Script de création de la base
└── pom.xml                   # Configuration Maven
```

## Démarrage de l'application

1. **Lancer XAMPP** et démarrer le service MySQL
2. **Exécuter l'application** avec la commande Maven :
   ```bash
   mvn javafx:run
   ```
3. **Se connecter** avec les identifiants par défaut :
    - Administrateur : `admin` / `admin`
    - Utilisateur : `user` / `user`

## Gestion de la base de données

La base de données `cicor_db` contient les tables suivantes :
- `categories` : Catégories de produits
- `cardboard` : Cartons de production
- `article` : Articles avec adresses MAC
- (voir le script SQL pour le schéma complet)

Pour recréer la base de données :
1. Ouvrir phpMyAdmin
2. Créer une nouvelle base nommée `cicor_db`
3. Importer le fichier `cicor_db.sql`

## Problèmes courants

### Erreur de connexion à la base de données
- Vérifier que MySQL est démarré
- Confirmer les paramètres de connexion dans `DatabaseManager.java`

### Erreur de dépendances Maven
- Exécuter `mvn clean install` pour rafraîchir les dépendances

### Erreur JavaFX
- Vérifier que Java 17+ est installé
- Confirmer que le module JavaFX est correctement configuré

## Contact

Développé par Mohamed Amine Nihmatouallah.

Pour toute question ou demande d'information :
- Email : [mohamed.amine.nihmatouallah@gmail.com](mailto:mohamed.amine.nihmatouallah@gmail.com)
- LinkedIn : [Mohamed Amine NIHMATOUALLAH](https://www.linkedin.com/in/mohamed-amine-nihmatouallah/)

## Licence

Ce projet est protégé par le droit d'auteur. Toute utilisation, modification ou distribution du code est strictement interdite sans autorisation explicite de l'auteur.

Le code peut être utilisé uniquement à des fins de test personnel, d'éducation ou d'évaluation.

Pour plus de détails, voir le fichier [LICENSE](./LICENSE).  
Pour des demandes d'autorisation, contactez [mohamed.amine.nihmatouallah@gmail.com](mailto:mohamed.amine.nihmatouallah@gmail.com).