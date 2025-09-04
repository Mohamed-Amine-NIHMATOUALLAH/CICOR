# CICOR - Articles and Cartons Management System

## 📋 Table of Contents
- [Description](#description)
- [✨ Features](#features)
- [⚙️ Prerequisites](#prerequisites)
- [🛠️ Technologies Used](#technologies-used)
- [🚀 Quick Installation](#quick-installation)
- [📖 Detailed Installation Guide](#detailed-installation-guide)
- [🏗️ Project Structure](#project-structure)
- [🎯 Starting the Application](#starting-the-application)
- [🐛 Troubleshooting](#troubleshooting)
- [📞 Contact](#contact)
- [📜 License](#license)

## 🏭 Description

CICOR is a comprehensive JavaFX application designed to modernize **Cicor Berrechid** (formerly Éolane) article and carton management. This industrial solution automates production processes with full traceability.

**Main capabilities:**
- ✅ Scan and validate article MAC addresses in real time
- ✅ Manage product categories with hardware/software versions
- ✅ Create and track production cartons with automatic numbering
- ✅ Print professional labels with barcodes
- ✅ Generate detailed Excel reports for analysis
- ✅ Easily configure network printers

## ✨ Features

### 🔐 Security & Access
- **Two-level authentication**: Administrator and User with different permissions
- **Secure interface** with real-time data validation

### 📦 Cart Management
- Automatic numbering (YYYYMMDDNNNN format)
- Automatic calculation of manufacturing date
- Real-time capacity and filling tracking
- Color-coded status (green/red) for cartons

### 📱 Intelligent Scanning
- Instant MAC address validation (format, uniqueness, capacity)
- Audio-visual feedback for each operation
- Duplicate prevention and automatic capacity control

### 🖨️ Professional Printing
- Support for network CAB printers
- Generates ZPL/EPL codes for labels
- Easy configuration via GUI

### 📊 Advanced Reporting
- Excel export with professional formatting
- Complete metadata and detailed article list
- Compatible with industrial standards

## ⚙️ Prerequisites

**Operating System:** Windows 10/11, macOS 10.15+, or Ubuntu 18.04+  

**Required Software:**
- ☕ **Java 17+** ([Download](https://adoptium.net/))
- 🐬 **MySQL 8.0+** ([XAMPP recommended](https://www.apachefriends.org/))
- 📦 **Maven 3.6+** ([Download](https://maven.apache.org/))
- 🔧 **Git** ([Download](https://git-scm.com/))

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|------------|---------|--------|
| **Java** | 17+ | Main language |
| **JavaFX** | 17.0.2 | Modern UI |
| **MySQL** | 8.0+ | Database |
| **Maven** | 3.6+ | Build management |
| **HikariCP** | 5.0.1 | Database connection pool |
| **Apache POI** | 5.2.3 | Excel export |
| **Commons Net** | 3.8.0 | Printer communication |

## 🚀 Quick Installation (5 minutes)

### 1. Clone the project
```bash
git clone https://github.com/Mohamed-Amine-NIHMATOUALLAH/CICOR.git
cd CICOR
````

### 2. Set up the database

* Start XAMPP and launch MySQL
* Import `cicor_db.sql` via phpMyAdmin

### 3. Run the application

```bash
mvn clean compile
mvn javafx:run
```

### 4. Login

* **Admin**: `admin` / `admin`
* **User**: `user` / `user`

## 📖 Detailed Installation Guide

### Step 1: Install prerequisites

#### Install Java 17

1. Download from [adoptium.net](https://adoptium.net/)
2. Verify installation:

```bash
java --version
# Should show "openjdk 17" or similar
```

#### Install XAMPP

1. Download from [apachefriends.org](https://www.apachefriends.org/)
2. Install and start the control panel
3. Run **MySQL** and **Apache**

#### Install Maven

1. Download from [maven.apache.org](https://maven.apache.org/)
2. Add to PATH and verify:

```bash
mvn --version
```

### Step 2: Configure the database

1. Open phpMyAdmin: [http://localhost/phpmyadmin](http://localhost/phpmyadmin)
2. Create a database named `cicor_db` with collation `utf8_general_ci`
3. Import `cicor_db.sql` file from the project

### Step 3: Configure the application

Database connection settings are in `DatabaseManager.java`:

```java
static final String DB_URL = "jdbc:mysql://localhost:3306/cicor_db";
static final String USER = "root";
private static final String PASS = ""; // Add password if any
```

### Step 4: Compile and run

```bash
# Compile
mvn clean compile

# Build JAR (optional)
mvn clean package

# Run
mvn javafx:run

# Or via JAR
java -jar target/CICOR-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 🏗️ Project Structure

```
CICOR/
├── src/main/java/com/example/cicor/
│   ├── Controllers/       # UI controllers
│   ├── database/          # Data access (DAO)
│   ├── models/            # Data models
│   └── services/          # Business logic
├── src/main/resources/com/example/cicor/
│   ├── images/            # Visual resources
│   ├── sounds/            # Alert sounds
│   ├── styles/            # CSS styles
│   └── views/             # FXML files
├── cicor_db.sql           # Database schema
├── pom.xml                # Maven config
└── README.md              # Documentation
```

## 🎯 Starting the Application

1. Start XAMPP and ensure MySQL is running
2. Open terminal in project folder
3. Run:

```bash
mvn clean compile
mvn javafx:run
```

4. Login:

* **Admin**: `admin` / `admin` (category management)
* **User**: `user` / `user` (daily operations)

**Typical workflow:**

1. Admin creates product categories
2. User creates cartons and scans articles
3. Generate labels and reports
4. Export data for analysis

## 🐛 Troubleshooting

* **MySQL connection failed**: Check XAMPP MySQL status, verify `DatabaseManager.java` settings, test connection with:

```bash
mysql -u root -p -h localhost
```

* **JavaFX not found**: Ensure Java 17+ installed with JavaFX modules
* **Maven dependencies**:

```bash
mvn clean install
mvn dependency:resolve
```

* Application not starting: Restart XAMPP, re-import database, or clone project again

## 📞 Contact

**Developer:** Mohamed Amine Nihmatouallah
**Email:** [mohamed.amine.nihmatouallah@gmail.com](mailto:mohamed.amine.nihmatouallah@gmail.com)
**LinkedIn:** [Mohamed Amine NIHMATOUALLAH](https://www.linkedin.com/in/mohamed-amine-nihmatouallah/)

## 📜 License

© 2025 Mohamed Amine Nihmatouallah. All rights reserved.

**Allowed:**

* Personal testing
* Academic purposes
* Non-commercial demonstrations

**Not allowed without permission:**

* Commercial use
* Modification and redistribution
* Integration into other projects

See [LICENSE](./LICENSE) for full details.
