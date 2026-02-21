# YapNet

Hey there! 👋

Welcome to **YapNet**, my take on a modern, full-stack social media platform inspired by Reddit and Twitter. This project is a playground for learning, experimenting, and building something fun with Java Spring Boot and React. If you’re reading this, you’re probably curious about how it works or want to run it yourself—so here’s everything you need to know!

---

##  What is YapNet?
YapNet is a social web app where users can:
- Register, log in, and manage their profiles
- Create posts (with titles, like Reddit!)
- Like/unlike posts and comment on them
- Follow/unfollow other users
- See a timeline/feed, popular posts, and user leaderboards
- View stats and more!

It’s designed to be clean, responsive, and easy to extend. The backend is a REST API in Java (Spring Boot), and the frontend is a modern React app (with Vite).

---

## 🛠️ Tech Stack
- **Backend:** Java 17, Spring Boot, JPA/Hibernate, H2 (dev), Maven
- **Frontend:** React (Vite), modern hooks, CSS modules
- **Testing:** JUnit 5, Mockito, React Testing Library
- **Other:** JWT Auth, RESTful APIs, Docker-ready

---

## 🏗️ Project Structure
```
ics-wtp-yapnet/
  yapnet-backend/   # Java Spring Boot backend
  frontend/         # React frontend (Vite)
```

---

## ⚡️ Getting Started

### 1. Clone the repo
```
git clone https://github.com/YOUR_USERNAME/ics-wtp-yapnet.git
cd ics-wtp-yapnet
```

### 2. Backend Setup (Spring Boot)
- Open `yapnet-backend` in IntelliJ (or your favorite IDE)
- Make sure you’re using Java 17
- The backend uses an in-memory H2 database by default (no setup needed)
- To run from terminal:
  ```
  cd yapnet-backend
  mvn spring-boot:run
  ```
- Or just run the main class in your IDE: `YapnetApplication.java`
- API docs available at `/swagger-ui.html` when running

#### Environment Variables
- Set `jwt.secret` in `application.properties` (already set for dev)
- Change DB config if you want to use Postgres/MySQL

### 3. Frontend Setup (React)
- Open a new terminal:
  ```
  cd frontend
  npm install
  npm run dev
  ```
- The app will open at [http://localhost:3000](http://localhost:3000) (or 3001 if 3000 is busy)
- The Vite dev server proxies API requests to the backend

---

## 🧪 Running Tests

### Backend (JUnit)
```
cd yapnet-backend
mvn test -Djacoco.skip=true
```
- 400+ tests for models, services, controllers, and DTOs
- Coverage is high and tests are fast!

### Frontend (React)
```
cd frontend
npm test
```
- Uses React Testing Library

---

## 🐞 Troubleshooting
- **Port 8080 already in use?**
  - Stop any other Java/Spring apps, or change the port in `application.properties`
- **Frontend can’t reach backend?**
  - Make sure both are running, and check the Vite proxy config
- **Maven plugin errors?**
  - Run `mvn clean install` to reset dependencies
- **JWT errors?**
  - Check your `jwt.secret` and make sure it’s at least 32 chars

---

## 🙏 Credits & Thanks
- Built by me (Atharv Aggarwal) as a learning project and portfolio piece
- Thanks to the open source community for all the libraries and inspiration
- If you have feedback or want to contribute, feel free to open an issue or PR!

---

## 📬 Contact
- Email: aga46435@hs-regensburg.de
- GitLab: aga46435

---

Thanks for checking out YapNet! If you run into any issues or have ideas, let me know. Happy coding! 🚀 