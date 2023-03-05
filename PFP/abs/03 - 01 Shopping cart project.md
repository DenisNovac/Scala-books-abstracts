# Shopping Cart project

Business requirements -> System design

## Business requirements

Backend service of online store.

Sell guitars and some more things in the future.

Guest:
  - register with unique username and password;
  - login with valid creds;
  - see all guitar catalog and search by brand.

Registered user:
  - add/remove from shopping cart;
  - modify quanity of product in shopping card;
  - checkout:
    - send user id and cart to external payment system;
    - persist order details;
    - handle of duplicate payments;
  - list existing orders as well as retrieving specific one by id;
  - log out.

Admin:
  - add brands;
  - add categories;
  - add products to the catalog;
  - modify the prices of products.

Frontend:
  - consume data via HTTP API.

## Domain

Item:
  - uuid;
  - model;
  - brand;
  - category;
  - description;
  - price.

Brand:
  - name;

Category:
  - name;

Cart:
  - uuid;
  - items (Map of ids and quantities);

Order:
  - uuid;
  - paymentId;
  - items;
  - total (amount in USD).

Card:
  - name;
  - number;
  - expiration;
  - cvv.

Guest user: no model in domain.

User:
  - uuid;
  - username;
  - password.

Admin:
  - uuid;
  - username.

## HTTP endpoints

App should be versioned to allow smooth evolution as the requirements will change in the future.

### Open Routes

#### Auth 

POST /users
  201 - user created
  400 - invalid data
  409 - username is taken

```json
{
  "username": "Denis",
  "password": "test"
}
```

POST /auth/login
  200 - success
  403 - invalid username or creds

POST /auth/logout

#### Content routes

GET /brands
GET /categories
GET /items
GET /items?brand=gibson (search by brand)

### Secured routes

GET /cart
POST /cart
PUT /cart
DELETE /cart/{itemId}
POST /checkout
GET /orders
GET /orders/{orderId}

### Admin routes

POST /brands
POST /categories
POST /items
PUT /items