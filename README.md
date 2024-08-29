# Mobile Phone Sales Web Service

## Users

This service has three types of users:
1. Guest. This type of user can browse the product catalog but cannot create a shopping cart or place an order;
2. Authorized User. This type of user can browse the product catalog, create a shopping cart, and place an order;
3. Administrator. This type of user manages the product catalog (adding, deleting, modifying products) and processes customer orders.

## Website Features

There is a sorting feature for phones by price, from cheapest to most expensive, and vice versa. In addition, there is a search function for mobile phones by name, as well as search filtering by various criteria: brand, processor, screen, amount of memory and more.

## Authorized User Capabilities

An authorized user can add products to a cart and then place an order from the cart page. Additionally, in their personal account, the user can view brief information about themselves, their current orders, and order history. They can also access a page where they can leave comments and ratings (from 1 to 5) for phones. This feature is available only after receiving an order to ensure that reviews and ratings are as objective as possible.

## Registration and Authorization

Registration is done as follows: the user enters their full name, age, phone number, email, and password. Authorization is performed using this email and password. On the authorization page, users can log in as an authorized user or as an administrator.

## Web Application Configuration Before Launch

Before launching, you need to specify values for the following fields in the application.properties file:
1. email host for sending messages (spring.mail.host);
2. email address (spring.mail.username);
3. email password (spring.mail.password);
4. email port (spring.mail.port);
5. secret key from the Stripe account (STRIPE_SECRET_KEY);
6. public key from the Stripe account (STRIPE_PUBLIC_KEY).
