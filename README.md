# 📦 api-inventory
The api-inventory microservice is part of the system's distributed architecture and is responsible for managing products.

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
