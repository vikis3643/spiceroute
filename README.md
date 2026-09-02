🌶️ SpiceRoute — Restaurant Ordering System

A full-stack Restaurant Ordering & Authentication System built using Java, Spring Boot, React, MySQL, JWT, Google OAuth, and Gmail API.

SpiceRoute provides a complete web-based restaurant experience where users can authenticate, browse the restaurant interface, place orders, and use secure account recovery functionality.

🚀 Live Demo
🌐 Frontend

https://spiceroute-frontend.onrender.com

⚙️ Backend API

https://spiceroute-k529.onrender.com

📌 Project Overview

SpiceRoute is a full-stack restaurant ordering web application designed to demonstrate real-world software development using a separate frontend and backend architecture.

The project follows this basic architecture:

React Frontend
      ↓
REST API
      ↓
Spring Boot Backend
      ↓
JPA / Hibernate
      ↓
MySQL Database

Authentication and email functionality are integrated with Google services:

Google Sign-In
      ↓
Google Credential
      ↓
Spring Boot Backend
      ↓
Authentication

and:

Password Reset Request
      ↓
Spring Boot
      ↓
Gmail API + Google OAuth
      ↓
Reset Email
✨ Features
👤 User Authentication
User authentication using JWT
Google Sign-In
Secure authentication flow
Configurable JWT expiration
Logout/session handling
🔵 Google Sign-In

Users can sign in using their Google account.

The flow uses:

Google Sign-In
Google OAuth
Google credential / ID token
Backend-side credential verification

The frontend sends the Google credential to the backend's /google endpoint for verification.

🔐 Password Reset

SpiceRoute includes an email-based password recovery system.

Flow
User selects Forgot Password
            ↓
Backend processes request
            ↓
Reset token/link generated
            ↓
Gmail API sends email
            ↓
User opens reset link
            ↓
User creates new password

The password reset link is configured to expire after 15 minutes.

🍽️ Restaurant Ordering

The application supports restaurant order processing.

The backend stores order information in MySQL using JPA/Hibernate.

The order flow includes:

Customer
   ↓
Select Food
   ↓
Place Order
   ↓
Spring Boot API
   ↓
JPA / Hibernate
   ↓
MySQL
   ↓
Order Created

The project has been validated with successful database insert operations for:

orders
order_items
🚚 Order Delivery / Status

The project also includes order delivery/status functionality so that order-related information can be managed through the application.

🛠️ Technology Stack
Layer	Technology
Frontend	React
Frontend Build Tool	Vite
Programming Language	JavaScript / JSX
Backend Language	Java
Backend Framework	Spring Boot
API Architecture	REST API
ORM	JPA / Hibernate
Database	MySQL
Authentication	JWT
Social Login	Google OAuth / Google Sign-In
Email	Gmail API
OAuth	Google OAuth 2.0
Version Control	Git + GitHub
Backend Deployment	Render Web Service
Frontend Deployment	Render Static Site
💡 Why These Technologies?
☕ Java

Java is used for backend development because it:

Supports object-oriented programming
Is widely used in enterprise applications
Has a strong ecosystem
Works well with Spring Boot
Is suitable for scalable backend applications
🌱 Spring Boot

Spring Boot is used to build the backend because it simplifies:

REST API development
Dependency injection
Database integration
Application configuration
Authentication/security integration
⚛️ React

React is used for the frontend because it provides:

Component-based development
Reusable UI components
Interactive user interfaces
Efficient frontend development
⚡ Vite

Vite is used as the frontend development and build tool because it provides:

Fast development server
Fast build process
Modern React development workflow
🐬 MySQL

MySQL is used as the database because restaurant applications need persistent relational data such as:

Users
Orders
Order items
Authentication-related data
🔗 JPA / Hibernate

JPA/Hibernate is used as the ORM layer.

It allows Java objects to be mapped to relational database tables and reduces the need for repetitive SQL/JDBC code.

🔑 JWT

JWT is used for application authentication.

It allows the backend to issue a signed token that can be used for authenticated requests.

The JWT expiration is configurable through the backend configuration.

🔵 Google OAuth

Google OAuth / Google Sign-In provides a convenient authentication method for users who prefer signing in with their Google account.

📧 Gmail API

Gmail API is used to send password-reset emails.

Google OAuth credentials are used to authorize the backend to communicate with Gmail.

🏗️ System Architecture
                         ┌──────────────────────┐
                         │       USER           │
                         │      Browser         │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   React + Vite       │
                         │      Frontend        │
                         └──────────┬───────────┘
                                    │
                              REST / HTTP
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   Spring Boot        │
                         │      Backend         │
                         └───────┬───────┬──────┘
                                 │       │
                         JPA     │       │ Google APIs
                                 │       │
                                 ▼       ▼
                       ┌────────────┐  ┌──────────────┐
                       │   MySQL    │  │    Google    │
                       │  Database  │  │ OAuth/Gmail  │
                       └────────────┘  └──────────────┘
📂 Project Structure
spiceRoute/
│
├── restaurant-backend/
│   └── restaurant-backend/
│       ├── src/
│       │   ├── main/
│       │   └── test/
│       ├── pom.xml
│       ├── Dockerfile
│       └── mvnw.cmd
│
├── restaurant-frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
├── render.yaml
└── README.md
🔐 Authentication Architecture

SpiceRoute uses multiple authentication-related technologies.

JWT Authentication
Login
  ↓
Backend
  ↓
Credentials validated
  ↓
JWT generated
  ↓
Frontend
  ↓
Authenticated requests
Google Authentication
Google Sign-In
      ↓
Google Credential
      ↓
Frontend
      ↓
POST /google
      ↓
Backend verification
      ↓
Application authentication
📧 Password Reset Architecture
Forgot Password
      ↓
Backend
      ↓
Reset Token / Link
      ↓
Gmail API
      ↓
User Email
      ↓
Reset Link
      ↓
New Password

Password reset expiration:

15 minutes
🔌 API Architecture

The frontend and backend communicate using HTTP-based REST APIs.

React
  │
  │ HTTP Request
  ▼
Spring Boot REST API
  │
  ├── Authentication
  ├── Google Login
  ├── Password Reset
  ├── Restaurant / Order Operations
  │
  ▼
MySQL
Confirmed Google Login Endpoint
POST /google

Purpose:

Receives the Google credential from the frontend and performs backend-side verification/authentication.

Other API endpoints should be documented directly from the current controller source code to ensure the endpoint list remains accurate.

🗄️ Database

SpiceRoute uses MySQL as its relational database.

The backend communicates with MySQL through:

Spring Boot
     ↓
JPA
     ↓
Hibernate
     ↓
MySQL

The order system stores order-related information including:

orders
order_items
⚙️ Environment Variables

Sensitive configuration is stored using environment variables instead of committing secrets to GitHub.

Frontend
VITE_API_BASE_URL=
VITE_GOOGLE_CLIENT_ID=
Purpose
Variable	Purpose
VITE_API_BASE_URL	Backend API URL
VITE_GOOGLE_CLIENT_ID	Google Sign-In client ID
Backend — Database
DB_URL=
DB_USERNAME=
DB_PASSWORD=
Backend — JWT
JWT_SECRET=
Backend — Google / Gmail
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
GOOGLE_REFRESH_TOKEN=

These values are used for Google/Gmail OAuth functionality.

Backend — Frontend URL
FRONTEND_BASE_URL=

This is used for generating deployed password-reset links.

💻 Local Setup
1. Clone Repository
git clone https://github.com/vikis3643/spiceroute.git

Move into the project:

cd spiceroute
⚙️ Backend Setup

Move into the backend directory:

cd restaurant-backend/restaurant-backend

Make sure Java and MySQL are installed.

Then configure the required environment variables.

For local development, the backend uses:

Port: 8080

Start the backend using Maven Wrapper.

Windows
mvnw.cmd spring-boot:run

Backend will be available at:

http://localhost:8080
⚛️ Frontend Setup

Open a new terminal.

Move to:

cd restaurant-frontend

Install dependencies:

npm install

Start the development server:

npm run dev

The frontend will normally be available through the Vite development URL shown in the terminal.

🔄 Running Full Application Locally

Start the backend:

cd restaurant-backend/restaurant-backend
mvnw.cmd spring-boot:run

Then start the frontend in another terminal:

cd restaurant-frontend
npm install
npm run dev

Application architecture:

Browser
   ↓
React Frontend
   ↓
localhost:8080
   ↓
Spring Boot
   ↓
MySQL
☁️ Deployment

SpiceRoute is deployed using Render.

Frontend
React + Vite
      ↓
Render Static Site

Live frontend:

https://spiceroute-frontend.onrender.com
Backend
Java + Spring Boot
      ↓
Render Web Service

Live backend:

https://spiceroute-k529.onrender.com
Source Control
Local Project
      ↓
Git
      ↓
GitHub
      ↓
Render Deployment
🔒 Security

Security practices used in the project include:

JWT-based authentication
Google credential verification
OAuth-based Gmail authorization
Environment variables for sensitive configuration
Time-limited password reset links
Secrets excluded from source code
⚠️ Important

Never commit the following to GitHub:

GOOGLE_CLIENT_SECRET
GOOGLE_REFRESH_TOKEN
DB_PASSWORD
MAIL_APP_PASSWORD
JWT_SECRET

If a secret is accidentally exposed, it should be rotated/revoked.

🧪 Testing & Validation

The project has been tested across important application flows.

Backend
BUILD SUCCESS

Backend compilation/build was successfully validated.

Order System

Database operations were validated through successful inserts into:

orders
order_items
Password Reset

Password reset was tested successfully:

Forgot Password
      ↓
Email Sent
      ↓
User Receives Reset Email
Google Login

Google Sign-In was tested through browser developer tools and backend request inspection.

📊 Current Project Scope

The current version focuses on:

Restaurant ordering
User authentication
JWT authentication
Google Sign-In
Password recovery
Gmail API integration
Order management
Delivery/status functionality
React frontend
Spring Boot backend
MySQL database
Cloud deployment
🔮 Future Scope

The application can be expanded into a production-grade restaurant platform.

👨‍💼 Admin Dashboard

Add an admin panel for:

Managing users
Managing food items
Managing categories
Managing orders
Managing restaurant information
Viewing sales reports
💳 Online Payments

Integrate payment gateways such as:

Razorpay
Stripe
Other suitable payment providers

Possible flow:

Cart
 ↓
Checkout
 ↓
Payment Gateway
 ↓
Payment Verification
 ↓
Order Confirmation
📍 Real-Time Order Tracking

Add real-time tracking so customers can see:

Order Placed
     ↓
Confirmed
     ↓
Preparing
     ↓
Ready
     ↓
Out for Delivery
     ↓
Delivered

WebSockets or another real-time communication mechanism could be introduced.

⭐ Reviews & Ratings

Customers could:

Rate food
Review restaurants
Rate delivery
View other customer reviews
📱 Notifications

Future versions could support:

Email notifications
SMS notifications
WhatsApp notifications
Push notifications
🤖 AI Integration

SpiceRoute can be extended with AI.

Possible features:

AI Food Recommendations
User History
     ↓
AI Recommendation Engine
     ↓
Personalized Food Suggestions
AI Chat Assistant

Users could ask:

"Mujhe spicy vegetarian food suggest karo."

The AI assistant could recommend suitable menu items.

Personalized Offers

AI could analyze:

Previous orders
Favorite categories
Order frequency
Spending patterns

and generate personalized recommendations/offers.

🚀 Long-Term Vision

The long-term goal is to transform SpiceRoute from a simple restaurant ordering project into a complete digital restaurant platform.

                   SpiceRoute
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
   Customer App   Admin Panel    Restaurant Panel
        │              │              │
        └──────────────┼──────────────┘
                       ▼
                 Spring Boot
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
      MySQL          Payments         AI
        │              │              │
        └──────────────┼──────────────┘
                       ▼
              Complete Platform
🎯 Project Learning Outcomes

This project demonstrates practical knowledge of:

Java programming
Spring Boot
REST API development
React development
MySQL
JPA/Hibernate
JWT authentication
Google OAuth
Gmail API
Environment-based configuration
Git/GitHub
Cloud deployment
Full-stack application architecture
Debugging and API testing
💼 Why This Project Is Valuable for a Developer Portfolio

SpiceRoute demonstrates more than a basic CRUD application.

It combines:

Frontend
   +
Backend
   +
Database
   +
Authentication
   +
OAuth
   +
External API
   +
Email Service
   +
Cloud Deployment

This makes it suitable as a Java Full Stack portfolio project and provides a foundation for adding advanced features such as payments, real-time tracking, analytics and AI.

👨‍💻 Author
Vikash Kumar Jha
Project

SpiceRoute — Restaurant Ordering System

Technologies
Java
Spring Boot
React
Vite
MySQL
JPA / Hibernate
JWT
Google OAuth
Gmail API
GitHub
Render
📄 License

This project is created for educational, portfolio and demonstration purposes.

⭐ If you like this project

Feel free to explore the source code and provide feedback.

SpiceRoute — Bringing Restaurant Ordering Online 🌶️
