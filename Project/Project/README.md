# CertifyPro — Phase: RBAC & Authentication

Professional Certification & Examination Management System.
This phase implements **only login (email/password) + role-based access control (RBAC)** for the 6 actors. No business modules yet.

## Stack
- **Frontend:** React (Vite)
- **Backend:** Spring Boot 3.3 (Java 21)
- **Database:** MySQL
- **Auth:** JWT

## The 6 Roles
| Role enum | Actor |
|---|---|
| `CANDIDATE` | Candidate |
| `CENTRE_ADMIN` | Test Centre Administrator |
| `EXAM_CONTROLLER` | Examination Controller |
| `EVALUATOR` | Evaluator / Examiner |
| `CERTIFICATION_OFFICER` | Certification Officer |
| `PROGRAMME_ADMIN` | Programme Admin |

## Demo accounts (seeded on startup)
Password for all: **`Password@123`**
- candidate@certifypro.com
- centreadmin@certifypro.com
- examcontroller@certifypro.com
- evaluator@certifypro.com
- certofficer@certifypro.com
- programmeadmin@certifypro.com

## Run the backend
1. Ensure MySQL is running. Update `backend/src/main/resources/application.properties`
   with your MySQL username/password (the `certifypro` DB is auto-created).
2. From `backend/`:
   ```
   mvnw.cmd spring-boot:run
   ```
   Backend runs on http://localhost:8080

## Run the frontend
From `frontend/`:
```
npm install
npm run dev
```
Frontend runs on http://localhost:5173

## API
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/login` | public — returns JWT |
| GET | `/api/me` | any authenticated user |
| GET | `/api/test/{role}` | role-restricted (proves RBAC) |
