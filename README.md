# TypeTrack ⌨️📊

A feature-rich Java desktop application for measuring, analyzing, and improving typing performance. TypeTrack combines real-time typing analytics, user authentication, performance tracking, gamification, and administrative controls in a modern Swing-based interface backed by MySQL.

---

## 🚀 Features

### Typing Engine

* Real-time WPM, Accuracy, and Mistake tracking
* Automatic test timer
* Dynamic keystroke validation
* Detailed post-test analytics

### User Management

* Secure user registration and login
* SHA-256 password hashing
* Strong password enforcement
* Security question–based password recovery

### Analytics & Insights

* Personal dashboard with performance statistics
* WPM progression charts
* Historical performance tracking
* Average and best score calculations

### Gamification

* Achievement & badge system
* Personal milestones and rewards
* Global leaderboard rankings

### History & Reporting

* Complete test history
* Filtering and sorting options
* Export reports in CSV, TXT, and PDF formats

### Admin Dashboard

* Role-Based Access Control (RBAC)
* User management
* Password reset controls
* Paragraph management
* System-wide analytics

### UI & Experience

* Modern Java Swing interface
* Dark / Light Theme support
* Interactive charts and visualizations
* Responsive dashboard layout

---

## 🛠️ Tech Stack

* Java (Swing)
* MySQL
* JDBC
* Graphics2D
* SHA-256 Encryption

---

## ☁️ Cloud Ready

TypeTrack supports deployment with cloud-hosted MySQL databases such as:

* Railway
* Aiven
* Render
* AWS RDS
* Google Cloud SQL

Database configuration is managed through:

```properties
data/database.properties
```

---

## 📦 Installation

### Clone Repository

```bash
git clone https://github.com/<your-username>/TypeTrack.git
cd TypeTrack
```

### Configure Database

Create:

```properties
data/database.properties
```

Example:

```properties
db.url=YOUR_DATABASE_URL
db.user=YOUR_DATABASE_USER
db.password=YOUR_DATABASE_PASSWORD
```

### Compile

```bash
javac -d bin -cp "lib/*" @sources.txt
```

### Run

```bash
java -cp "bin;lib/*" com.typetrack.Main
```

---

## 👑 Default Administrator Account

The application automatically provisions an administrator account during first startup.

| Username | Password  |
| -------- | --------- |
| admin    | Admin@123 |

> Change the default administrator password before production deployment.

---

## 📊 Project Highlights

* Secure Authentication System
* Cloud Database Integration
* Interactive Performance Dashboard
* Achievement & Leaderboard System
* Exportable Reports (CSV / TXT / PDF)
* Admin Management Portal
* Modern Desktop UI

📸 Screenshots

Added screenshots of:

* Login Screen
* Dashboard
* Typing Test Interface
* WPM Trend Graph
* Leaderboard
* Admin Panel
* History & Reports


## 📄 License

This project is intended for educational and portfolio purposes.
