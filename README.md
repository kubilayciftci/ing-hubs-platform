# ING Hubs Platform - Loan API Services

## Overview

This is a microservice-based loan management application built with Spring Boot. 
The application follows a clean architecture pattern and is designed to be easily extensible for additional microservices in the future.

## Architecture

The application implements a **Hexagonal Architecture (Ports and Adapters)** pattern, which makes it perfectly suitable for microservices architecture:

- **Domain Layer**: Contains business entities (Customer, Loan, LoanInstallment, User)
- **Application Layer**: Contains business logic and use cases (LoanFacade, LoanPort)
- **Infrastructure Layer**: Contains adapters for external concerns (LoanAdapter, repositories)
- **Presentation Layer**: Contains REST controllers and DTOs

## Features

### Core Features
- Create loan endpoint with validation
-  List loans with filtering capabilities
- List installments for a specific loan
- Pay loan installments with business rules
- Complete documentation
- Authorization with admin/customer roles
- Database storage with proper relationships

### Bonus 1 - Role-Based Authorization
- **ADMIN users** can operate on all customers
- **CUSTOMER users** can only operate on their own data
- Secure endpoint access with proper validation

### Bonus 2 - Reward and Penalty System
- **Early payment discount**: `installmentAmount * 0.001 * (days before due date)`
- **Late payment penalty**: `installmentAmount * 0.001 * (days after due date)`
- Automatic calculation of final payment amounts

## Database Schema

### Customer Table
```sql
customer: id, name, surname, creditLimit, usedCreditLimit
```

### Loan Table
```sql
loan: id, customerId, loanAmount, numberOfInstallment, createDate, isPaid
```

### Loan Installment Table
```sql
loanInstallment: id, loanId, amount, paidAmount, dueDate, paymentDate, isPaid
```

### User Table
```sql
users: id, username, password, role, customerId
```

## API Endpoints

### 1. Create Loan
**POST** `/loan`

**Authorization**: ADMIN only

**Request Body**:
```json
{
  "customerId": 1,
  "amount": 5000.00,
  "interest": 0.15,
  "numberOfInstallments": 12
}
```

**Response**:
```json
{
  "id": 1,
  "customerId": 1,
  "loanAmount": 5000.00,
  "interest": 0.15,
  "numberOfInstallments": 12,
  "createdAt": "2024-01-15T10:30:00Z",
  "isPaid": false
}
```

**Validation Rules**:
- Interest rate: 0.1 - 0.5
- Installments: 6, 9, 12, or 24
- Customer must have sufficient credit limit
- Total loan amount = amount × (1 + interest rate)

### 2. List Loans
**GET** `/loan?customerId=1&numberOfInstallments=12&isPaid=false`

**Authorization**: ADMIN (all customers) or CUSTOMER (own data only)

**Query Parameters**:
- `customerId` (optional): Filter by customer
- `numberOfInstallments` (optional): Filter by installment count
- `isPaid` (optional): Filter by payment status

**Response**:
```json
[
  {
    "id": 1,
    "customerId": 1,
    "loanAmount": 5000.00,
    "interest": 0.15,
    "numberOfInstallments": 12,
    "createdAt": "2024-01-15T10:30:00Z",
    "isPaid": false
  }
]
```

### 3. List Installments
**GET** `/loan/installments/{loanId}`

**Authorization**: ADMIN (all loans) or CUSTOMER (own loans only)

**Response**:
```json
[
  {
    "id": 1,
    "loanId": 1,
    "amount": 479.17,
    "paidAmount": 0.00,
    "dueDate": "2024-02-01T00:00:00Z",
    "paymentDate": null,
    "isPaid": false
  },
  {
    "id": 2,
    "loanId": 1,
    "amount": 479.17,
    "paidAmount": 0.00,
    "dueDate": "2024-03-01T00:00:00Z",
    "paymentDate": null,
    "isPaid": false
  }
]
```

### 4. Pay Loan
**POST** `/loan/pay/{loanId}`

**Authorization**: ADMIN (all loans) or CUSTOMER (own loans only)

**Request Body**:
```json
{
  "amount": 1000.00,
  "paymentDate": "2024-01-20T10:30:00Z"
}
```

**Response**:
```json
{
  "loanId": 1,
  "installmentsPaid": 2,
  "totalAmountSpent": 958.34,
  "loanFullyPaid": false
}
```

**Business Rules**:
- Installments must be paid wholly (no partial payments)
- Only installments due within 3 months can be paid
- Earliest installments are paid first
- Early payment discount and late payment penalty applied

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Gradle 7.0 or higher

### Build and Run

1. **Clone the repository**:
```bash
git clone https://github.com/kubilayciftci/ing-hubs-platform.git
cd ing-hubs-platform
```

2. **Build the project**:
```bash
./gradlew build
```

3. **Run the application**:
```bash
./gradlew :loan-api:bootRun
```

The application will start on `http://localhost:8080`

### Docker Support

The application includes Docker support:

```bash
# Build Docker image
docker build -t loan-api .

# Run with Docker Compose
docker-compose up
```

## Initial Data

The application comes with pre-configured users and customers:

### Users
 
- **Admin User**:
```tex
  - Username: admin
  - Password: admin123
  - Role: ADMIN
```

- **Customer Users**:
```text
  - Username: kubilay
  - Password: customer123
  - Role: CUSTOMER
  - Customer ID: 1
```
```text
  - Username: merve
  - Password: customer123
  - Role: CUSTOMER
  - Customer ID: 2
```

### Customers
```
- Kubilay Çiftci
  - ID: 1
  - Credit Limit: 10,000
  - Used Credit: 0
```
```
- Merve Çetinkaya:
  - ID: 2
  - Credit Limit: 15,000
  - Used Credit: 0
```
## Testing

### Unit Tests
Run unit tests:
```bash
./gradlew test
```

### API Testing

#### Option 1: Postman Collection (Recommended)
A complete Postman collection is available in the project:
- **File**: `loan-api/loan-api-postman-collection.json`
- **Import**: Open Postman → Import → Select the JSON file
- **Environment**: The collection includes pre-configured requests with authentication

#### Option 2: cURL Commands

##### 1. Create a Loan (Admin)
```bash
curl -X POST http://localhost:8080/loan \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "customerId": 1,
    "amount": 5000.00,
    "interest": 0.15,
    "numberOfInstallments": 12
  }'
```

##### 2. List Loans (Customer)
```bash
curl -X GET "http://localhost:8080/loan" \
  -u kubilay:customer123
```

##### 3. List Installments (Customer)
```bash
curl -X GET http://localhost:8080/loan/installments/1 \
  -u kubilay:customer123
```

##### 4. Pay Loan (Customer)
```bash
curl -X POST http://localhost:8080/loan/pay/1 \
  -H "Content-Type: application/json" \
  -u kubilay:customer123 \
  -d '{
    "amount": 1000.00,
    "paymentDate": "2024-01-20T10:30:00Z"
  }'
```

## Configuration

### Profiles
- **local**: Development with H2 in-memory database
- **dev**: Development environment
- **test**: Testing environment
- **prod**: Production environment

### Database
- **Local**: H2 in-memory database
- **Production**: Configure your preferred database in `application-prod.yml`

## Security

The application implements comprehensive security:

1. **HTTP Basic Authentication**
2. **Role-based access control (ADMIN/CUSTOMER)**
3. **Customer data isolation**
4. **BCrypt password encoding**

### Security Rules
- **ADMIN users** can access all customer data
- **CUSTOMER users** can only access their own data
- All endpoints require authentication
- Passwords are encrypted using BCrypt
