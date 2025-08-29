# Bajaj Finserv Health Qualifier

A Spring Boot application that solves the Bajaj Finserv Health Qualifier challenge.

## Project Overview

This application automatically:
1. Generates a webhook on startup
2. Solves a SQL problem based on the provided database schema
3. Submits the solution to the webhook URL using JWT authentication

## Problem Statement

The application solves a SQL problem involving three tables:
- **DEPARTMENT**: Contains department information
- **EMPLOYEE**: Contains employee details with department references
- **PAYMENTS**: Contains salary payment records

**Task**: Find the highest salary credited to an employee that was NOT made on the 1st day of any month, along with employee name, age, and department.

## Solution

The SQL query finds:
- Highest salary amount (excluding 1st day payments)
- Employee full name (concatenated first and last name)
- Employee age (calculated from DOB)
- Department name

## Technical Details

- **Framework**: Spring Boot 3.2.0
- **Java Version**: 17
- **Build Tool**: Maven
- **HTTP Client**: RestTemplate
- **JSON Processing**: Jackson ObjectMapper

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── bajajfinserv/
│   │           ├── BajajHealthQualifierApplication.java  # Main application class
│   │           ├── QualifierService.java                 # Core business logic
│   │           └── AppConfig.java                        # Configuration beans
│   └── resources/
│       └── application.properties                        # Application configuration
├── pom.xml                                              # Maven dependencies
└── README.md                                            # This file
```

## How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build and Run
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/bajaj-health-qualifier-1.0.0.jar
```

## API Endpoints

The application automatically executes the qualifier process on startup. No manual endpoints are exposed.

## Expected Output

On successful execution, you should see:
```
Starting Bajaj Health Qualifier process...
Webhook generated successfully: [webhook_url]
SQL problem solved. Final query: [SQL_QUERY]
Solution submitted successfully!
```

## SQL Query

The final SQL query that solves the problem:

```sql
SELECT 
    p.AMOUNT AS SALARY,
    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
    FLOOR(DATEDIFF(CURDATE(), e.DOB) / 365.25) AS AGE,
    d.DEPARTMENT_NAME
FROM PAYMENTS p
JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE DAY(p.PAYMENT_TIME) != 1
ORDER BY p.AMOUNT DESC
LIMIT 1
```

## Notes

- The application runs automatically on startup
- No controllers or manual endpoints are exposed
- Uses RestTemplate for HTTP communication
- JWT token is automatically included in the Authorization header
- Error handling and logging are implemented 