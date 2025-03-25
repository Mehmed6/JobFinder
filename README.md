# JobFinder
JobFinder is a job search platform built with Spring Boot and Thymeleaf. Users can register, log in, and search for job opportunities based on various filters. After registration, users can check the status of their job applications and view all the job positions they've applied for.

## Features

* **User Registration and Authentication:** User registration is done through a form. After successful registration, they can log in. Users have three attempts to enter their password correctly. After three failed attempts, their account is locked for security reasons.
* **Job Listings:** Users can filter job listings according to their preferences or view all job postings. Users cannot apply for jobs they have already applied to or jobs that are closed.
* **Application Status:** Users can check whether their job applications have been accepted or not.
* **Admin Roles:** Only users with the admin role can approve or reject job applications. Admins have access to all job applications and users. Admins can also create new job postings and register new companies.
* **Filtering and Pagination:** Job listings can be filtered, and pagination has been implemented for large datasets.
* **Security:** Secure password handling is ensured with Spring Security, allowing users to perform only their own actions.
* **Error Messages and Thymeleaf Forms:** Thymeleaf forms have been designed for error messages (403, 404, and general errors) and are presented in a user-friendly way.

This project is developed to provide a simple and secure platform for job seekers, with admin users able to manage job postings, applications, and users.

## 💻  Technologies Used

* Java 17
* Spring Boot
* Spring Security
* Thymeleaf
* Lombok
* PostgreSQL
* Mapstruct
* Spring AOP (Aspect-Oriented Programming)
* Java Reflection API
* BufferedWriter
* Pagination & Sorting

## 🔗  Services

* **RegisterService:** Handles user registration operations, including creating new user accounts and saving user data to the database.
* **LoginService:** Manages user login functionality by validating credentials and redirects the user to a page where they can perform actions after successful authentication.
* **UserService:** Manages user operations. Updates user information, allows job applications, recommends suitable job postings, and deletes users. It also checks job applications and ensures security measures like password validation. All operations are logged.
* **CompanyService:** Handles operations related to companies. Allows adding new companies, retrieving company details, and fetching associated users and job postings. It validates company uniqueness and ensures all data is fetched and displayed with pagination.
* **JobPostingService:** Manages job posting-related operations, including creating, retrieving, and searching for job postings. It supports pagination and filtering by company, experience, location, and keywords. Also handles job posting creation and associates it with the appropriate company.
* **JobApplicationService:** Handles operations related to job applications, such as submitting applications, finding applications by user or job posting, and filtering by status. It includes methods for approving or rejecting job applications, ensuring that a user can't apply for the same job multiple times and that the job posting is active.
* **LogEntryService:** Manages logging operations by saving log entries to the repository. The logger method creates a new log entry with the provided details (performedBy, message, logType) and stores it using the ILogEntryRepository.

# 🌐 API Endpoints

## RegisterController

* URL: `http://localhost:6767/register`
  Users can register on this page by filling out the form here.

* ## LoginController

* URL: `http://localhost:6767/auth/login`
  Users can log in with their email and password on this page.

## DashboardController

* URL: `http://localhost:6767/dashboard`
  On this page, users can perform my applications, job postings, edit my profile, and log out operations.

## UserController

* URL: `http://localhost:6767/user/show/{userId}`
  Users can view their account details on this page.

* URL: `http://localhost:6767/user/update`
  Users can update their account information on this page.

* URL: `http://localhost:6767/user/experience`
  Users can view users based on their experience range (min/max) on this page.

* URL: `http://localhost:6767/user/all`
  Users can view all users on this page.

* URL: `http://localhost:6767/user/all/company/{companyId}`
  Users can view all users associated with a specific company on this page.

* URL: `http://localhost:6767/user/search/{keyword}`
  Users can search for other users by skills or experience on this page.

* URL: `http://localhost:6767/user/job/posting/{userId}`
  Users can view recommended job postings based on userId on this page.

* URL: `http://localhost:6767/user/apply/job`
  Users can apply for a job by submitting a job application on this page. 

## CompanyController

* URL: `http://localhost:6767/company/save`
  Admins can save a new company on this page.

* URL: `http://localhost:6767/company/all`
  Users can view all companies on this page.

* URL: `http://localhost:6767/company/all/users/{companyId}`
  Users can view all users associated with a specific company on this page.

* URL: `http://localhost:6767/company/show/{companyId}`
  Users can view detailed information about a specific company based on its ID on this page.

* URL: `http://localhost:6767/company/show/all/job/postings/{companyId}`
  Users can view all job postings for a specific company based on the company ID on this page.

## JobPostingController

* URL: `http://localhost:6767/job/posting/save`
  Admins can add a new job posting on this page.

* URL: `http://localhost:6767/job/posting/show/{jobPostingId}`
  Users can view the details of a specific job posting based on the jobPostingId on this page.

* URL: `http://localhost:6767/job/posting/show/all`
  Users can view all job postings on this page.

* URL: `http://localhost:6767/job/posting/show/all/by-company/{companyId}`
  Users can view all job postings for a specific company based on companyId on this page.

* URL: `http://localhost:6767/job/posting/search/{keyword}`
  Users can search for job postings based on the entered keyword on this page.

* URL: `http://localhost:6767/job/posting/show/all/experience/{minExperience}`
  Users can view job postings based on the minimum required experience on this page.

* URL: `http://localhost:6767/job/posting/show/all/active`
  Users can view all active job postings on this page.

* URL: `http://localhost:6767/job/posting/show/all/by-location/{location}`
  Users can view all job postings for a specific location on this page.

## JobApplicationController

* URL: `http://localhost:6767/job/application/save`
  Admins can submit a job application on this page.

* URL: `http://localhost:6767/job/application/show/by-user/{userId}`
  Admins can view a user's job applications based on their userId on this page.

* URL: `http://localhost:6767/job/application/show/by-job/posting/{jobPostingId}`
  Admins can view all job applications for a specific job posting based on the jobPostingId on this page.

* URL: `http://localhost:6767/job/application/show/status/{status}`
  Admins can view all job applications based on their status (e.g., pending, approved, rejected) on this page.

* URL: `http://localhost:6767/job/application/show/all`
  Admins can view all job applications on this page.

* URL: `http://localhost:6767/job/application/approve`
  This endpoint is responsible for approving job applications, but it is not directly accessible. It is triggered when the user clicks the "Approve" button on the job application list. Once clicked, the application is approved, and the user is redirected to the page displaying all job applications with a success message.

* URL: `http://localhost:6767/job/application/reject`
  This endpoint handles the rejection of job applications, but it is not directly accessible. It is triggered when the user clicks the "Reject" button on the job application list. Once clicked, the application is rejected, and the user is redirected to the page displaying all job applications with a success message. 

# NOTES
### AdminInitializer
This class automatically creates an admin user with the email `admin@gmail.com` and password `admin` at the beginning of the program

### CustomAuthenticationProvider
This class is responsible for authenticating users. It checks the user's email and password against the database and returns a user object if the credentials are correct. If the user's account is locked, the user is redirected to the login page with an error message. If the user's account is not locked but the password is incorrect, the user is redirected to the login page with an error message. If the user's account is not locked and the password is correct, the user is redirected to the dashboard page.

### CustomAuthenticationEntryPoint
Handles requests that require authentication but are made by unauthenticated users. When such a request is received, it redirects the user to the login page (/auth/login). This class helps in managing the flow for unauthenticated access attempts in the application.

### ApplicationContextProvider
Provides static access to the Spring ApplicationContext. It allows retrieving beans of a specified type from the application context.

### LogUtil
Provides utility methods to log actions in the application. The log method records a log entry for a specified action, performed by a user, and associates it with a log type. It retrieves the LogEntryService bean to handle the actual logging process.

### LoggingAspect
This class is responsible for logging method executions in the controller and service layers of the application using Aspect-Oriented Programming (AOP). It logs HTTP request details before controller methods are executed, logs successful method executions after they return, and logs exceptions when they occur. Logs are saved to a file named ActivityLog.log under the logs directory. This class helps monitor the application's flow and detect any issues by recording method executions and exceptions.