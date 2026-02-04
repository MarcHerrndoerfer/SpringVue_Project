# Spring + Vue Hospital Scheduling

Full-stack demo for hospital staff scheduling with a Spring Boot backend and a Vue 3 frontend.

## Features
- Registration & login
- Staff-only dashboard
- Ward overview & duty schedule sample
- Session-based authentication

## Tech Stack
- Java 22, Spring Boot, Spring Security, JPA
- MySQL 8 (Docker)
- Vue 3, Vite, TypeScript, Vue Router

## Project Structure
- SpringProject/demo — backend
- vue-frontend — frontend

## Setup
1) Start database (Docker Compose)
2) Run Spring Boot app
3) Run Vue dev server

## Environment
- Configure DB credentials via SpringProject/demo/.env (see SpringProject/demo/.env.example)


## Auth & Roles
- Staff roles: DOCTOR, NURSE, ADMIN
- Staff dashboard: /staff

## Roadmap
- Real shift data from backend
- CRUD for wards and schedules
- Role-based UI and permissions

