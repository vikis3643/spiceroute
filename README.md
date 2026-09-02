# 🌶️ SpiceRoute — Restaurant Ordering System

> A full-stack restaurant ordering platform built with React, Spring Boot, MySQL, JWT Authentication, Google Sign-In, and Gmail API.

---

## 🚀 Live Project

**Frontend:** https://spiceroute-frontend.onrender.com

**Backend:** https://spiceroute-k529.onrender.com

---

## 📌 About

**SpiceRoute** is a full-stack restaurant ordering system designed to provide a smooth online food-ordering experience.

The project demonstrates how a React frontend communicates with a Spring Boot REST API, how authentication is handled, how orders are stored in MySQL, and how the application is deployed to the cloud.

### 🎯 Main Goals

- Build a real-world full-stack application
- Implement REST APIs using Spring Boot
- Connect React with a Java backend
- Manage restaurant orders and order status
- Implement JWT-based authentication
- Support Google Sign-In
- Implement password reset through Gmail API
- Deploy the application using Render

---

## ✨ Key Features

| Feature | Description |
|---|---|
| 👤 User Authentication | Secure login and registration |
| 🔐 JWT Security | Token-based authentication |
| 🔵 Google Sign-In | Login using Google OAuth |
| 🔑 Password Reset | Reset password through email |
| 🍽️ Restaurant Ordering | Browse and place food orders |
| 🛒 Order Management | Create and manage customer orders |
| 📦 Order Status | Track order/delivery status |
| 💾 MySQL Database | Persistent application data |
| 📧 Gmail Integration | Password reset email delivery |
| ☁️ Cloud Deployment | Frontend and backend deployed on Render |

---

## 🛠️ Technology Stack

### Frontend

- React
- Vite
- JavaScript / JSX
- HTML
- CSS

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- REST API
- JWT

### Database

- MySQL

### Authentication & Services

- JWT Authentication
- Google Sign-In
- Google OAuth
- Gmail API

### Development & Deployment

- Git
- GitHub
- Render

---

## 🏗️ Architecture

```text
                    ┌─────────────────────┐
                    │        User         │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   React Frontend    │
                    │      + Vite         │
                    └──────────┬──────────┘
                               │
                         REST API / HTTP
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Spring Boot API   │
                    │       Backend       │
                    └──────┬───────┬──────┘
                           │       │
                           ▼       ▼
                 ┌─────────────┐  ┌─────────────────┐
                 │    MySQL    │  │ Google & Gmail  │
                 │   Database  │  │      APIs       │
                 └─────────────┘  └─────────────────┘
```

---

## 🔐 Authentication

SpiceRoute uses multiple authentication mechanisms.

### JWT Authentication

The application uses **JSON Web Tokens** for authenticated API requests.

```text
User Login
    ↓
Backend validates credentials
    ↓
JWT Token generated
    ↓
Frontend stores authentication state
    ↓
Authenticated requests include token
```

### Google Sign-In

Users can authenticate using their Google account.

```text
Google Sign-In
      ↓
Frontend receives Google credential
      ↓
POST /google
      ↓
Spring Boot verifies credential
      ↓
User authenticated
```

### Password Reset

The application provides password recovery through Gmail API integration.

```text
Forgot Password
      ↓
Reset request
      ↓
Reset email generated
      ↓
Gmail API
      ↓
User receives reset link
      ↓
Password updated
```

> Password reset links are configured with a **15-minute expiration period**.

---

## 🍽️ Order Management

The core functionality of SpiceRoute is restaurant ordering.

### Order Flow

```text
Browse Menu
     ↓
Select Food
     ↓
Add Items
     ↓
Place Order
     ↓
Save Order
     ↓
Save Order Items
     ↓
Track Order Status
```

The backend maintains order information and related order items in the MySQL database.

---

## 🔌 Backend API

The backend is implemented as a REST API using Spring Boot.

### Example Authentication Endpoint

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/google` | Verify Google Sign-In credential |

Additional backend APIs handle authentication, users, orders, order items, and other application functionality.

---

## 🗄️ Database

SpiceRoute uses **MySQL** as its relational database.

The backend communicates with MySQL through:

```text
Spring Boot
     ↓
Spring Data JPA
     ↓
Hibernate
     ↓
MySQL
```

### Database Responsibilities

- Store user information
- Store authentication-related data
- Store restaurant/order information
- Store order items
- Maintain relationships between application entities

---

## 📁 Project Structure

```text
spiceRoute/
│
├── restaurant-backend/
│   └── restaurant-backend/
│       ├── src/
│       ├── .mvn/
│       ├── Dockerfile
│       ├── pom.xml
│       ├── mvnw
│       └── mvnw.cmd
│
├── restaurant-frontend/
│   ├── public/
│   ├── src/
│   ├── package.json
│   ├── package-lock.json
│   ├── vite.config.js
│   └── index.html
│
├── render.yaml
├── .gitignore
└── README.md
```

---

## ⚙️ Environment Variables

Sensitive credentials should **never** be committed to GitHub.

The application uses environment variables for:

- Database configuration
- JWT secret
- Google Client ID
- Gmail OAuth configuration
- Frontend base URL

Example:

```text
DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

JWT_SECRET=your_secret

GOOGLE_CLIENT_ID=your_google_client_id

FRONTEND_URL=your_frontend_url
```

> Never place real passwords, API keys, OAuth secrets, or JWT secrets inside `README.md`.

---

## 💻 Run Locally

### 1. Clone Repository

```bash
git clone <your-github-repository-url>
cd spiceRoute
```

### 2. Start Backend

```bash
cd restaurant-backend/restaurant-backend
mvnw.cmd spring-boot:run
```

The Spring Boot backend runs on:

```text
http://localhost:8080
```

### 3. Start Frontend

Open another terminal:

```bash
cd restaurant-frontend
npm install
npm run dev
```

Vite will provide the local frontend URL in the terminal.

---

## 🌐 Deployment

SpiceRoute is deployed using **Render**.

### Deployment Architecture

```text
GitHub Repository
       │
       ├──────────────────┐
       │                  │
       ▼                  ▼
React Frontend      Spring Boot Backend
       │                  │
       ▼                  ▼
Render Static Site   Render Web Service
                          │
                          ▼
                       MySQL
```

### Current Deployment

**Frontend**

https://spiceroute-frontend.onrender.com

**Backend**

https://spiceroute-k529.onrender.com

Environment variables are configured separately on the deployment platform.

---

## 🔒 Security Practices

- JWT-based authentication
- Environment-based configuration
- No hardcoded production credentials
- OAuth-based Google authentication
- Expiring password reset links
- Sensitive configuration excluded from Git
- Separate frontend and backend deployment configuration

---

## 🧪 Testing & Verification

The project has been tested across its major application flows.

### Verified Areas

- Backend startup
- MySQL database connection
- User authentication
- JWT authentication
- Google Sign-In
- Password reset
- Gmail email delivery
- Order creation
- Order item storage
- Frontend-backend communication
- Production deployment

---

## 📊 Current Project Scope

### Implemented

```text
Authentication       ✅
JWT Security         ✅
Google Sign-In       ✅
Password Reset       ✅
Restaurant Ordering  ✅
Order Management     ✅
Order Status         ✅
MySQL Integration    ✅
React Frontend       ✅
Spring Boot Backend  ✅
Cloud Deployment     ✅
```

---

## 🔮 Future Scope

- Online payment gateway
- Restaurant/admin dashboard
- Food search and filtering
- Reviews and ratings
- Improved order history
- Real-time order tracking
- Notifications
- Delivery partner module
- Restaurant analytics
- AI-powered food recommendations
- AI chatbot for customer support

---

## 🤖 Future AI Integration

A future version of SpiceRoute can introduce:

### AI Food Recommendation

Recommend dishes based on previous orders, user preferences, popular items, and time of day.

### AI Customer Assistant

A chatbot could help users find food, understand menu items, track orders, and answer restaurant-related questions.

### Smart Analytics

AI could help restaurants understand popular dishes, customer behavior, peak ordering periods, and sales trends.

---

## 🎓 Learning Outcomes

This project demonstrates practical knowledge of:

- Java development
- Spring Boot
- REST API development
- Spring Data JPA
- Hibernate
- MySQL
- React
- JavaScript
- Authentication
- JWT
- OAuth
- Gmail API
- Git & GitHub
- Environment variables
- Cloud deployment
- Frontend-backend integration

---

## 💼 Why This Project Matters

SpiceRoute demonstrates the integration of multiple technologies into one complete application:

```text
Frontend
   +
Backend
   +
Database
   +
Authentication
   +
External APIs
   +
Cloud Deployment
```

This makes the project suitable for:

- Academic project submission
- College viva
- Java Full Stack portfolio
- GitHub portfolio
- Placement discussions
- Backend development practice

---

## 📌 Project Highlights

| Category | Technology |
|---|---|
| Architecture | Full Stack |
| Backend | Java + Spring Boot |
| Frontend | React + Vite |
| Database | MySQL |
| Authentication | JWT + Google Sign-In |
| Email Service | Gmail API |
| Deployment | Render |
| Source Control | Git + GitHub |

---

## 👨‍💻 Author

**Vikash Kumar Jha**

BCA — Full Stack Development

### Interests

- Java Backend Development
- Spring Boot
- Full Stack Development
- AI-powered Applications

---

## ⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.

---

## 📄 License

This project is created for **educational, portfolio, and learning purposes**.
