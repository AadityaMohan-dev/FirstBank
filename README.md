# Banking Application Backend

This is a web application backend that provides basic banking functionalities using **Spring Boot** and **Java**. The project is designed for API testing with **Postman** and version control with **Git & GitHub**.

## Features
- User Authentication & Authorization
- Account Creation & Management
- Fund Transfers (Internal & External)
- Transaction History
- Balance Inquiry

## Tech Stack
- **Backend:** Java, Spring Boot
- **API Testing:** Postman
- **Version Control:** Git, GitHub

## Getting Started

### Prerequisites
Ensure you have the following installed:
- Java 17+
- Spring Boot
- Postman (for API testing)
- Git

### Installation & Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/your-repo.git
   ```
2. Navigate to the project directory:
   ```bash
   cd your-repo
   ```
3. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

### API Endpoints
| Method | Endpoint           | Description          |
|--------|-------------------|----------------------|
| GET    | /accounts/{id}    | Get account details |
| POST   | /accounts         | Create an account   |
| POST   | /transactions     | Transfer funds      |
| GET    | /transactions     | Get transaction history |

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature-name`)
3. Commit your changes (`git commit -m 'Add feature'`)
4. Push to the branch (`git push origin feature-name`)
5. Create a Pull Request

## License
This project is licensed under the MIT License.
