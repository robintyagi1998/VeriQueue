**VeriQueue: Smart Digital Token System**

A smart, lightweight web-based token management system for businesses like clinics, banks, and service centers, offering real-time token generation and admin control.

📌 **Features**
🔐 OTP-based login for users (email verification)
🎟️ Token generation with real-time queue tracking
📊 Admin dashboard with token analytics
📢 Live display for current and upcoming tokens
🛠️ Role-based access (User / Admin)
🕒 Auto-expiry and call management for tokens

🚀 **Tech Stack**
Backend: Java, Spring Boot, Spring Security, Spring Scheduler
Frontend: Thymeleaf, HTML, CSS
Database: MySQL

### ⏰ Background Tasks (Spring Scheduler)

The application uses Spring's `@Scheduled` task to automatically clean up expired OTPs every 6 minutes to ensure security and memory efficiency.
- Cleans OTPs from in-memory store (e.g. `Map<String, OTPDetails>`)
- Reduces misuse risk and optimizes performance
  
Mail Service: JavaMailSender (for OTP and token updates)

🧭 **Application Flow & URLs**
👤 **User Flow**
Step	URL	Description
1	/user/login	Enter Name, Email, and Contact Number
2	/user/send-otp	Click "Send OTP" to receive OTP on email
3	/user/verify-otp	Enter the received OTP for verification
4	/user	Dashboard with options: Generate Token or Exit
5	/user/generate-token	Generate a new token (one per day unless expired)
(returns token-exists view if active/called token exists)	

🛡️ **Admin Flow**
Step	URL	Description
1	/admin/login	Secure login (username/password - password is Bcrypt hashed)
2	/admin/dashboard	Admin dashboard with token stats and action buttons
3	/admin/users	View users and active tokens; Call/Expire tokens
4	/admin/token/call	Mark a token as currently being served
5	/admin/token/expire	Mark a token as expired (user can re-generate next token)
6	/admin/tokens	View all tokens with any status
7	/admin/tokens/filter	View all called tokens filtered by current date
8	/admin/display	Live display showing current token & upcoming tokens

⚙️ **How to Run**
**Clone the repository:**
git clone https://github.com/robintyagi1998/veriqueue.git
cd veriqueue
**Configure your application.properties:**
spring.datasource.url=jdbc:mysql://localhost:3306/veriqueue
spring.datasource.username=db_username
spring.datasource.password=yourpassword
spring.mail.username=your_email
spring.mail.password=your_email_password

**Run the Spring Boot application:**
./mvnw spring-boot:run

**Access the app:**
User: http://localhost:8080/user/login
Admin: http://localhost:8080/admin/login

🔒 **Security**
User area is public and uses OTP + session for auth.
Admin area is protected using Spring Security with password hashing.
OTP flow is protected against URL tampering and bypass.
Sessions are used to persist verification and prevent abuse.

🌐 **Deployment**
Render

👨‍💻 **Author**
Robin Tyagi
Work Email: crazywonders001@gmail.com
GitHub: https://github.com/robintyagi1998

📢 **License**
This project is open-source and available for free use under the MIT License.
