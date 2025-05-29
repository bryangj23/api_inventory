# 📦 api-inventory
The api-inventory microservice is part of the system's distributed architecture and is responsible for managing products.
Available Endpoints

🔐 Product

- GET /products

Retrieves a paginated list of products with optional filtering support.
![image](https://github.com/user-attachments/assets/4adda746-ede9-41a8-a011-3e1bf8abfa29)


- GET /products/{productId}

Get product by ID.
![image](https://github.com/user-attachments/assets/f8f7083c-3c15-42b2-913f-0951e7adbbc0)


- POST /products

Creates a new product using the data provided in the request body.
![image](https://github.com/user-attachments/assets/8726f3e4-f968-41fe-b5a6-6ff9dd7e73d7)



- PUT /products/{productId}

Updates an existing product identified by its ID.
![image](https://github.com/user-attachments/assets/6f7229e6-4608-4095-8037-fa2c630f9eaa)



- DELETE /products/{productId}/users/{userId}

Deletes a product from the system (irreversible operation).
![image](https://github.com/user-attachments/assets/d56df743-3c3a-4a19-9c04-d6996f68099f)



👤 Product Movement

- GET /products/{productId}/product-movements

Retrieves a paginated list of registered product movement with filtering support.
![image](https://github.com/user-attachments/assets/50a15f7f-deef-404d-9f0f-8935f124ffa1)



- GET /products/{productId}/product-movements/{productMovementId}

Retrieves detailed information of a specific product movement by their ID.
![image](https://github.com/user-attachments/assets/3dacc61a-8be7-45ae-a7f7-b1d5bb202d6c)


- POST /products/{productId}/product-movements

Creates a new product movement with input validations.
![image](https://github.com/user-attachments/assets/c508807f-057b-4b7e-9a81-364280fa5cb7)



- PUT /products/{productId}/product-movements/{productMovementId}

Partially updates the data of an existing product movement.
![image](https://github.com/user-attachments/assets/edad792d-cef6-443d-96e0-b246979daa07)



- DELETE /products/{productId}/product-movements/{productMovementId}/users/{userId}

Deactivates a product movement (the user is not physically removed, just marked as inactive).
![Uploading image.png…]()



## 🧰 Technologies Used

- Java 17+
- Spring Boot
- Spring Web
- Spring JPA
- RSQL JPA specification
- PostgresSQL Driver
- Swagger UI (opcional)
- Maven

🌐 Internationalization (i18n)
Error messages are centralized in messages.properties files and translated according to the LocaleConfiguration configuration class.

## Project organization

The code is organized as follows:

1. `configuration` contains all the external library implementations to be configured inside spring-boot.
2. `controller` contains the communication interfaces with the client.
3. `entity` contains the persistence domains.
4. `dto` contains classes that separate in-memory objects from the database.
5. `mapper` contains mapping classes between objects and entities.
6. `repository` contains the classes or components that encapsulate the logic necessary to access the data sources.
7. `service` contains the interfaces and implementations that define the functionality provided by the service.
8. `utils` contains application utils classes.


## Environment variable

**Database environment variable**

- DB_PORT: Database port. Default value: 5432
- DB_NAME: Database name.
- DB_SCHEMA: Database schema.
- DB_USER: Database user.
- DB_PASS: Database password.

**Service environment variable**
- API_USER_HOST: User api host.


```json
{
  "DB_PORT": "5432",
  "DB_NAME": "postgres",
  "DB_SCHEMA": "api_webflux",
  "DB_USER": "",
  "DB_PASS": "",
  "API_USER_HOST": ""
}
```


## Building from Source

1. Install JDK 17- [JDK 17 Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. Install dependencies: mvn dependency:resolve
3. Run clean: mvn clean
4. Run compile: mvn compile


## Run Database Migrations

1. Install PostgreSQL 16 - [PostgreSQL 16 Download](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads)
2. Install Liquibase - [Liquibase Download](https://docs.liquibase.com/start/install/home.html)
3. Configure Database host, Database port, Database name, Schema, User, Password in liquibase configuration `liquibase/liquibase.properties`
4. Running migrations:

```bash
liquibase --defaultsFile=liquibase/liquibase.properties --changeLogFile=liquibase/changelog.yaml update
```

## Run in Local Mode

1. Install dependencies: mvn dependency:resolve
2. Run clean: mvn clean
3. Run compile: mvn compile
4. Run server: mvn spring-boot:run -Dspring-boot.run.profiles=local

### Documentation

Document Reference: [Docs File](swagger/swagger.yaml)

Postman collection: [Docs File](api-inventory.postman_collection.json)

📄 License
This project is free to use for educational and evaluation purposes.

🙋‍♂️ Contact
Developed by Brayan Guardo – [brayanguardodiaz0@gmail.com]
Repository: GitHub
