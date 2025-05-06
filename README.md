# 🐶 Pubi Pet Shop

This is a mock pet e-commerce website project built for interview purposes. It simulates a basic shopping flow from browsing products to placing an order.

The project uses Vue 3 with Quasar on the frontend, and Spring Boot (Java) for the backend.
> 本專案為面試用作品，功能涵蓋商品瀏覽、購物車、會員登入與訂單流程，模擬實際電商購物體驗。
---

## 🛠️  Built with

- **Frontend**: Vue 3 + Quasar Framework  
- **Backend**: Spring Boot (Java)  
- **Database**: MySQL  

---

## ✨ Features
- **Authentication**: Email-based magic link login + JWT
- Product listing and category browsing
- Add to cart (stored in localStorage for guests)
- 3-step checkout process:
  1. View Cart
  2. Select Payment & Delivery Method & Enter Shipping Info
  4. Order Complete
- Member Center (view/edit profile & shipping addresses)
- View order history
  
<img width="1000" alt="截圖 2025-05-06 14 29 39" src="https://github.com/user-attachments/assets/2be2a789-38a7-4cf4-86bb-7c0fbd328a8c" />

---

## 🚀 Run locally
### Frontend

Before running the frontend, create a `.env` file in the `frontend` folder and add your backend API URL:

| Variable Name      | Description                    |
|--------------------|--------------------------------|
| `VITE_API_BASE_URL`| Backend URL (for CORS)         |

Once the variable is set, run:
```
cd frontend
npm install
quasar dev
```
### Backend
Before starting the backend, set the following environment variables:

| Variable Name     | Description                    |
|-------------------|--------------------------------|
| `DB_HOST`         | MySQL host address             |
| `DB_PORT`         | MySQL port                     |
| `DB_NAME`         | Database name                  |
| `DB_USER`         | MySQL user                     |
| `DB_PASS`         | MySQL password                 |
| `JWT_SECRET`      | Secret key for JWT auth        |
| `ALLOWED_ORIGIN`  | Frontend URL (for CORS)        |
| `EMAIL_USER`       | Sender email for login links   |
| `EMAIL_PASS`       | App password for the email     |

Once variables are set, run:
```
./mvnw spring-boot:run
```
---
## ERD (Entity Relationship Diagram)
![Untitled](https://github.com/user-attachments/assets/58985b22-2cc8-4502-82fc-df0c91ddb816)

---

## 📝 Notes
This project is for learning and interview demonstration only

Payment gateway integration is not implemented

UI style is inspired by Lativ (clean and simple)

Some features (validation, exception handling) are still being improved
