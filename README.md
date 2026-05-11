# SpringEcom

SpringEcom is a backend-focused e-commerce project built using Spring Boot and PostgreSQL.  
The project mainly focuses on backend system design, REST API development, database modeling, and AI-powered product search features.

It includes product management, order handling, image upload support, and semantic search using PgVector and Spring AI.

---

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- Hibernate
- PostgreSQL
- PgVector
- Spring AI
- OpenAI GPT-4o
- Maven
- Docker

---

## Features

### Product Management
- Add, update, delete, and fetch products
- Multipart image upload support
- Product search functionality
- DTO-based API responses

### Order Management
- Place orders
- Manage order items
- Automatic stock updates

### AI Features
- AI-generated product descriptions
- AI-generated product images
- Semantic product search using vector embeddings
- RAG-based product assistant chatbot

---

## Project Structure

The project follows a layered architecture:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
