# 🌶️ SpiceRoute — Restaurant Ordering System

<p align="center">
  <img src="https://img.shields.io/badge/RESTAURANT%20ORDERING%20SYSTEM-ff69b4?style=for-the-badge" />
  <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" />
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white" />
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white" />
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white" />
</p>

<p align="center">
  <strong>Order • Authenticate • Manage • Track</strong>
</p>

<p align="center">
  A full-stack restaurant ordering platform designed to provide a smooth online food-ordering experience with secure authentication and cloud deployment.
</p>

<p align="center">
  <a href="https://spiceroute-frontend.onrender.com">
    <img src="https://img.shields.io/badge/🚀_LIVE_DEMO-Visit_Website-00C7B7?style=for-the-badge" />
  </a>
  <a href="https://github.com/vikis3643/SpiceRoute">
    <img src="https://img.shields.io/badge/💻_SOURCE_CODE-GitHub-181717?style=for-the-badge&logo=github" />
  </a>
</p>

---

## 🖥️ Project Overview

**SpiceRoute** is a responsive full-stack web application that helps users experience a seamless online food-ordering process.

The application provides restaurant ordering, JWT authentication, order status tracking, password recovery via Gmail API, and Google Sign-in — all through a clean interface powered by React, HTML/CSS, and Spring Boot.

### 🎯 What makes it different?

Instead of being only a basic CRUD application, this project combines:

> 🔐 Secure JWT Authentication
> 🔵 Google OAuth Sign-In
> 🔑 Gmail API Password Reset
> 🍽️ Restaurant Ordering Flow
> 📦 Order Tracking & Management
> ☁️ Cloud Deployment (Render)

---

## 🌐 Live Preview

### 🚀 Try It Yourself

**Live Frontend:**
https://spiceroute-frontend.onrender.com

**Live Backend API:**
https://spiceroute-k529.onrender.com

---

## ✨ Core Features

### 👤 Secure User Authentication

Authenticate users using multiple methods:

```text
┌─────────────────────────────┐
│       LOGIN OPTIONS         │
├─────────────────────────────┤
│ 📧 Standard Email/Password  │
│ 🔵 Google OAuth Sign-In     │
│ 🔑 Gmail Password Reset     │
└─────────────────────────────┘
```

Password reset links are securely generated and sent via Gmail API, configured with a **15-minute expiration period**.

---

### 🍽️ Order Management

The core functionality tracks the entire lifecycle of an order:

| Phase | Description |
| :--- | :--- |
| **Browse** | View menus and select food items |
| **Cart** | Add items and manage quantities |
| **Order** | Place secure orders to the database |
| **Track** | Monitor real-time order status |

---

### 🗄️ Database Integration

The application maintains data persistence using **MySQL**:

* User credentials and profiles
* Restaurant and menu information
* Order history and cart items
* Complex entity relationships via Hibernate

---

# 🛡️ Security Architecture

One of the main technical aspects of this project is secure client-server communication.

Instead of basic sessions, the application uses:

```java
Jwts.builder()
```

for generating secure JSON Web Tokens.

The user credentials and API endpoints are protected using **Spring Security**.

### Security Flow

```text
        User Credentials
             │
             ▼
     Backend Validation
             │
             ▼
   Secure Token Generation
       (JWT Creation)
             │
             ▼
    Frontend State Storage
             │
             ▼
    🔐 Authenticated APIs
             │
       ┌─────┴─────┐
       ▼           ▼
     Order       Profile
   Management    Access
```

---

# 🧰 Technology Stack

<p align="center">

| Technology | Role |
| --- | --- |
| ⚛️ **React & Vite** | Frontend architecture & fast bundling |
| 🟧 **HTML5** | Application structure & semantic markup |
| 🟦 **CSS3** | UI styling, animations & responsive layout |
| 🟨 **JavaScript (JSX)**| Dynamic frontend application logic |
| ☕ **Java** | Core backend programming language |
| 🟩 **Spring Boot** | Backend REST API & server logic |
| 🍃 **Spring Data JPA**| Database ORM & Hibernate implementation |
| 🐬 **MySQL** | Relational database storage |
| 🔐 **JWT & OAuth** | Secure token & Social authentication |
| 📧 **Gmail API** | Transactional email delivery |
| 🚀 **Render** | Cloud hosting & deployment |

</p>

---

# ☁️ Deployment Architecture

The application is deployed separately for optimal performance:

* Frontend deployed on Render Static Site
* Backend deployed on Render Web Service
* Environment variables securely injected without hardcoding

The frontend and backend communicate seamlessly over HTTPS.

---

# 📂 Project Structure

```text
spiceRoute/
│
├── restaurant-backend/
│   └── restaurant-backend/
│       ├── src/
│       ├── .mvn/
│       ├── Dockerfile
│       ├── pom.xml
│       └── mvnw
│
├── restaurant-frontend/
│   ├── public/
│   ├── src/
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
│
├── render.yaml
└── README.md
```

---

# ⚡ Getting Started

## 1. Clone Repository

```bash
git clone https://github.com/vikis3643/SpiceRoute.git
```

## 2. Enter Project

```bash
cd spiceRoute
```

## 3. Configure Environment

Create required environment variables for DB credentials, JWT secret, Google Client ID, and Gmail API keys.

## 4. Run Backend

```bash
cd restaurant-backend/restaurant-backend
mvnw.cmd spring-boot:run
```

## 5. Run Frontend

```bash
cd restaurant-frontend
npm install
npm run dev
```

---

# 📊 Feature Overview

```text
                    🌶️ SPICEROUTE PLATFORM
                              │
             ┌────────────────┼────────────────┐
             │                │                │
             ▼                ▼                ▼
       AUTHENTICATION      ORDERING        MANAGEMENT
             │                │                │
      ┌──────┼──────┐    ┌────┴────┐    ┌─────┼─────┐
      ▼      ▼      ▼    ▼         ▼    ▼     ▼     ▼
     JWT   Google Reset Menu    Checkout API  Cloud  DB
```

---

# 🚀 Future Roadmap

The project can be extended with:

```text
☐ Online payment gateway
☐ Restaurant/admin dashboard
☐ Real-time order tracking
☐ Delivery partner module
☐ AI-powered food recommendations
☐ AI customer support chatbot
☐ Food search and filtering
☐ Restaurant analytics
```

---

# 📚 What I Learned

Building this project helped demonstrate practical knowledge of:

* Java & Spring Boot REST APIs
* React and Vite frontend architecture
* HTML5 structure and CSS3 responsive design
* JavaScript ES6+ logic and state management
* JWT Authentication flow
* Google OAuth integration
* Gmail API configuration
* MySQL database relationships (JPA/Hibernate)
* Cloud deployment on Render
* Environment variable security

---

# 👨‍💻 Developer

## Vikash Kumar Jha

**BCA Student | Aspiring Full Stack Java Developer**

### 💻 Skills & Interests

```text
Java
Spring Boot
JavaScript
React
HTML
CSS
MySQL
Web Development
```

<p align="center">
  <a href="https://github.com/vikis3643">
    <img src="https://img.shields.io/badge/GitHub-vikis3643-181717?style=for-the-badge&logo=github" />
  </a>
</p>

---

# ⭐ Support the Project

If you like this project:

### ⭐ Star the repository

### 🍴 Fork the project

### 📢 Share it with others

Your support motivates me to build more projects. ❤️

---

<p align="center">

### 🌶️ Seamless orders. Secure platform.

**Made with ❤️ by Vikash Kumar Jha**

</p>

---

<p align="center">
  <a href="https://spiceroute-frontend.onrender.com">
    🚀 <strong>Explore Live Demo</strong>
  </a>
</p>
