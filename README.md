# 🥬 FreshBazaar

## 🚀 Overview
**FreshBazaar** digitizes large fruit & vegetable markets.  
Instead of walking from stall to stall, customers can **see live product rates** posted by traders and contact them directly.


> **Real-time marketplace for fresh produce rates**  
> Web + Mobile platform that lets **traders list daily prices** of vegetables & fruits  
> and enables **customers to view, compare, and connect** with traders instantly.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-green.svg)
![React](https://img.shields.io/badge/React-18-blue.svg)
![Build](https://github.com/ajax-07/freshbazaar/actions/workflows/ci.yml/badge.svg)

### ✨ Key Features (MVP)
- **Trader Portal** – Create profiles, upload products, update prices & availability.
- **Customer App** – Browse all market prices, search/filter products, view trader details.
- **Secure Auth** – Role-based authentication using Spring Security & JWT.
- **Scalable Microservices** – Independent services for auth, products, traders, and more.
- **Responsive Frontend** – React web app (mobile-friendly) with future React Native support.

## Getting Started -------------


## Repository Structure

- `apps/`: Mobile and web client codebases.
- `api-gateway/`: API gateway logic and configuration.
- `microservices/`: All backend microservices, each in its own folder.
- `ai-models/`: Machine learning models and related code.
- `databases/`: Database schemas, migrations, and Elasticsearch configs.
- `docs/`: Design docs, architecture diagrams, and API specs.
- `shared-config/`: Environment variable templates, logging configs, and secret templates.
- `deploy/`: Dockerfiles, docker-compose, Kubernetes manifests, and deployment scripts.
- `.github/workflows/`: GitHub Actions workflows for CI/CD.

------------------------------

## 🏗️ Architecture

Microservices with Spring Boot, React frontend, and Docker-based deployment.

> Full OpenAPI specs live in [`/docs/api-specs`](docs/api-specs).

---------------------

## 🗂 Tech Stack
| Layer            | Technology                                   |
|------------------|----------------------------------------------|
| Backend          | Java 21, Spring Boot, Spring Cloud           |
| Frontend         | React 18, TypeScript, Redux Toolkit          |
| Database         | PostgreSQL (primary), Redis (caching), MongoDB (images)       |
| Messaging        | Kafka (future event streaming)               |
| Deployment       | Docker, Docker Compose, Kubernetes-ready     |
| Authentication   | Spring Security, JWT                         |
| AI (Phase 3)     | Python, TensorFlow/PyTorch, FastAPI (model serving) |

-------------


--------------

## 🛠️ Local Development

```bash
# Clone repository
git clone [text](https://github.com/ajax-07/FreshBazaar.git)
cd FreshBazaar

# Start backend services
docker-compose up --build

# Start frontend (from /frontend/web)
npm install
npm start

