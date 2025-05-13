# 🚗 MyParkingBackend – Spring Boot Backend Application

Spring Boot REST API 

---

## 🔧 Prerequisites

- Java 17  
- Maven  
- PostgreSQL  
- Google API Key (για Maps & Autocomplete)

---

## 📥 1. Clone το Project

```bash
git clone https://github.com/KOSTISKONTO/MyParkingBackend.git
cd MyParkingBackend


### ⚙️ 2. Create file `application.properties`

Example:

```properties
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/myparking
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

security.jwt.secret-key=your-super-secret-key
google.api.key=your-google-api-key

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=20MB

```

---

### 🔨 3. Build

```bash
mvn clean install
```

---

### ▶️ 4. Run

```bash
mvn compile
mvn package
java -jar target/Backend-0.0.1-SNAPSHOT.jar
```


### 📂 5. (OPTIONALLY) FILES `uploads/`
```
uploads/
```



## 🛡️6.  Security

-Spring Boot Security
-JWT


