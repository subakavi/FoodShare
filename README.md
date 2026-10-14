# FoodShare — Member 1 starter project

This project implements all six FoodShare modules: registration/login, food listings, donation requests, donor-recipient matching, collection/distribution tracking, and summary reports. It uses an H2 file-based database for local development.

## Requirements
- Java JDK 17 or later
- Maven 3.9+ (or open this Maven project in IntelliJ IDEA)
- Internet access the first time Maven downloads dependencies

## Run in IntelliJ IDEA
1. Extract the ZIP file.
2. Open IntelliJ IDEA → Open → select the extracted `FoodShare-Member1` folder (the folder containing `pom.xml`).
3. Allow Maven to import dependencies.
4. Open `src/main/java/com/foodshare/FoodShareApplication.java`.
5. Click the green Run icon next to `main`.
6. Open http://localhost:8080 in a browser.

## Run from a terminal
Open a terminal in the folder containing `pom.xml` and run:
- Windows: `mvnw.cmd spring-boot:run` if a Maven wrapper is present; otherwise `mvn spring-boot:run`
- Tests: `mvn test`

If Maven is not installed, use IntelliJ IDEA's Maven panel or install Maven.

## Demo
1. Register one account with role DONOR.
2. Log in as the donor.
3. Add a food listing with quantity between 1 and 500 and an expiry date today or later.
4. View the listing and try deleting it.
5. Register another account with role RECIPIENT and verify that it can view listings but cannot add/delete them.
6. Try invalid quantity, expired date, duplicate email, and wrong password for testing evidence.

## Routes
- `/register`, `/login`, `/dashboard`
- `/food-listings`, `/food-listings/new`
- `/matching`, `/requests`
- `/tracking`, `/reports`

## Routes
- `/register`, `/login`, `/dashboard`
- `/food-listings`, `/food-listings/new`
- `/matching`, `/requests`
- `/tracking`, `/reports`

## Important prototype notes
- The H2 database is stored locally in `./data/foodshare-db`.
- This is a student prototype, not a production authentication system. For production, use Spring Security, CSRF protection, stronger authorization, and secure deployment settings.
- Quantity range 1–500 is a project rule chosen for boundary-value testing. Keep it consistent in the SRS and test cases.
- The remaining four modules (requests, matching, collection tracking, and reports) are to be implemented by the other team members and integrated using the same `com.foodshare` package and database.
