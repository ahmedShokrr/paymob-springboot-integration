# 🌟 Spring Boot Paymob Integration

## 📚 Index
- [About](#about)
- [📓 Pre-Requisites](#-pre-requisites)
- [🛠️ Customization](#-customization)
- [🔌 Installation and Run](#-installation-and-run)
- [📁 File Structure](#-file-structure)
- [🤝 Contribution](#-contribution)
- [🔄 Features Not Implemented](#-features-not-implemented)
- [📖 Resources](#-resources)
- [🌟 Author](#-author)
- [📄 License](#-license)

## 📝 About
Integrating payment gateways can be a daunting task, especially when resources are scarce. This project provides a comprehensive guide and implementation for integrating **Paymob** into a Java Spring Boot application. Whether you're looking to support card payments, e-wallets, or handle webhooks for payment statuses, this repository aims to simplify the process and serve as a valuable resource for the developer community.

## 📓 Pre-Requisites
Before you begin, ensure you have met the following requirements:

- **Java Development Kit (JDK)**: Version 11 or higher
- **Spring Boot**: Version 2.5.x or higher
- **Maven**: For dependency management and building the project
- **Paymob Account**: Sign up for a Paymob account to obtain API keys and integration IDs
- **Database**: MySQL or any other supported relational database
- **Git**: For version control

## 🛠️ Customization
This project is designed to be easily customizable to fit your specific business needs. Here are some areas you might want to customize:

- **Payment Methods**: Add or remove supported payment methods such as additional e-wallets or alternative card types.
- **Refund Process**: Implement the refund functionality as per your business logic.
- **Security Enhancements**: Integrate additional security measures like enhanced HMAC verification or OAuth2.
- **User Enrollment**: Customize the post-payment enrollment process to fit your application's workflow.

*Note: Refund process and some other features are currently not implemented but can be added based on your requirements.*

## 🔌 Installation and Run
Follow these steps to set up and run the project locally:

1. **Clone the Repository**
    ```bash
    git clone https://github.com/yourusername/spring-boot-paymob-integration.git
    cd spring-boot-paymob-integration
    ```

2. **Configure Environment Variables**
    - Rename `application.properties.example` to `application.properties`.
    - Replace the placeholder values with your actual Paymob credentials and database configurations.
    ```properties
    # PayMob Configuration
    PAYMOB.API_KEY=YOUR_PAYMOB_API_KEY_HERE
    PAYMOB.AUTH_URL=https://accept.paymob.com/api/auth/tokens
    PAYMOB.ORDER_URL=https://accept.paymob.com/api/ecommerce/orders
    PAYMOB.PAYMENT_KEY_URL=https://accept.paymob.com/api/acceptance/payment_keys
    PAYMOB.IFRAME_ID=YOUR_IFRAME_ID_HERE
    PAYMOB.WALLET_INTEGRATION_ID=YOUR_WALLET_INTEGRATION_ID_HERE
    PAYMOB.CARD_INTEGRATION_ID=YOUR_CARD_INTEGRATION_ID_HERE
    PAYMOB.HMAC_SECRET=YOUR_HMAC_SECRET_HERE

    # Server Configuration
    server.port=8080

    # Database Configuration
    spring.datasource.url=jdbc:mysql://localhost:3306/your_database
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

    # Swagger Configuration
    springdoc.api-docs.path=/v3/api-docs
    springdoc.swagger-ui.path=/swagger-ui.html
    ```

3. **Build the Project**
    ```bash
    mvn clean install
    ```

4. **Run the Application**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

5. **Access Swagger UI**
    Navigate to `http://localhost:8080/swagger-ui.html` to explore the available APIs.

## 📁 File Structure
Here's an overview of the project's directory structure:
```
.
src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           ├── controller
│   │           │   ├── PaymentController.java
│   │           │   └── WebhookController.java
│   │           ├── dto
│   │           │   └── PaymentInitiationDto.java
│   │           ├── entity
│   │           │   └── Payment.java
│   │           ├── exception
│   │           │   ├── APIException.java
│   │           │   └── ResourceNotFoundException.java
│   │           ├── repository
│   │           │   └── PaymentRepository.java
│   │           ├── service
│   │           │   ├── PaymentService.java
│   │           │   ├── PaymentServiceImpl.java
│   │           │   ├── ProcessingService.java
│   │           │   └── ProcessingServiceImpl.java
│   │           └── SpringBootPaymobApplication.java
│   └── resources
│       └── application.properties
└── test
    └── java
        └── com
            └── example
                └── SpringBootPaymobApplicationTests.java

```



## 🤝 Contribution
Feel free to contribute to this project by following these steps:

1. **Fork the Repository**
2. **Create a New Branch**
    ```bash
    git checkout -b feature/YourFeatureName
    ```
3. **Commit Your Changes**
    ```bash
    git commit -m "Add some feature"
    ```
4. **Push to the Branch**
    ```bash
    git push origin feature/YourFeatureName
    ```
5. **Open a Pull Request**

Please contribute in separate branches if you come up with further advancements, new features, or discover a bug. Your contributions are highly appreciated! 🙏

*Note: The refund process and some other features are currently not implemented but can be added based on your requirements.*

## 📖 Resources
- [Paymob Documentation](https://developers.paymob.com/egypt/getting-started-egypt)

## 🌟 Author
**Ahmed Shokr**  
[GitHub](https://github.com/ahmedShokrr) | [LinkedIn](https://www.linkedin.com/in/ahmed-shokr-015426229/) | [Email](Gmail:shokra19@gmail.com)

## 📄 License
This project is licensed under the [MIT License](LICENSE).



