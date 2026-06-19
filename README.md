# TypeTrack ⌨️📈

TypeTrack is a full-featured Java desktop application designed to help users measure, analyze, and improve their typing performance. Built with Java Swing and MySQL, it provides real-time typing analytics, performance tracking, gamification features, secure authentication, and a comprehensive administration system.

---

## 🚀 Features

### ⌨️ Typing Engine

* Real-time calculation of WPM, Accuracy, and Mistakes
* Automatic test timer with start/stop detection
* Dynamic keystroke validation
* Detailed post-test performance analysis

### 🔐 User Authentication

* Secure user registration and login
* SHA-256 password hashing
* Strong password policy enforcement
* Security question–based password recovery

### 📊 Analytics & Insights

* Personal performance dashboard
* WPM progression charts
* Historical performance tracking
* Personal best and average statistics

### 🏆 Gamification

* Achievement and badge system
* Performance milestones and rewards
* Global leaderboard rankings

### 📁 History & Reports

* Complete test history management
* Sorting and filtering capabilities
* Export reports in CSV, TXT, and PDF formats

### 👨‍💼 Admin Dashboard

* Role-Based Access Control (RBAC)
* User management and monitoring
* Password reset controls
* Paragraph management system
* System-wide statistics and analytics

### 🎨 User Interface

* Modern Java Swing UI
* Dark Mode and Light Mode support
* Interactive charts and visualizations
* Responsive dashboard experience

---

## 🛠️ Tech Stack

| Technology | Purpose                        |
| ---------- | ------------------------------ |
| Java Swing | Desktop GUI                    |
| MySQL      | Database Management            |
| JDBC       | Database Connectivity          |
| Graphics2D | Custom Charts & Visualizations |
| SHA-256    | Password Security              |

---

## ☁️ Cloud Deployment

TypeTrack supports both local and cloud-hosted MySQL databases, including:

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

## 📦 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/TypeTrack.git
cd TypeTrack
```

### 2. Configure Database

Create:

```properties
data/database.properties
```

Example:

```properties
db.url=jdbc:mysql://localhost:3306/your_database_name
db.user=YOUR_USERNAME_HERE
db.password=YOUR_DATABASE_PASSWORD_HERE
```

### 3. Compile the Project

```bash
javac -d bin -cp "lib/*" @sources.txt
```

### 4. Run the Application

```bash
java -cp "bin;lib/*" com.typetrack.Main
```

---

## 👑 Default Administrator Account

A default administrator account is automatically created during first startup.

| Username | Password  |
| -------- | --------- |
| admin    | Admin@123 |

> ⚠️ Change the default administrator password before production deployment.

---

## 📸 Screenshots

### Login Page
### Dashboard
### Typing Test
### WPM Trend Graph
### Leaderboard
### Admin Dashboard
### History & Reports

---

## ⭐ Project Highlights

* Secure Authentication System
* Cloud Database Integration
* Interactive Performance Dashboard
* Achievement & Leaderboard System
* Exportable Reports (CSV, TXT, PDF)
* Administrative Management Portal
* Modern Desktop User Experience

---

## 📄 License

This project is intended for educational, learning, and portfolio purposes.
