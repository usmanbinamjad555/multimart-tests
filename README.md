# MultiMart E2E Tests

Selenium + TestNG end-to-end test suite for [MultiMart](https://github.com/usmanbinamjad555/multimart-project), a MERN multi-tenant marketplace. The suite drives a headless Chrome browser against the running frontend at `http://localhost:5173` and runs in Jenkins after each deployment.

![Java](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Maven-3-C71A36?logo=apachemaven)
![Selenium](https://img.shields.io/badge/Selenium-4.16.1-43B02A?logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.8.0-blue)
![Jenkins](https://img.shields.io/badge/CI-Jenkins-D24939?logo=jenkins)

## Test Coverage

All 15 tests are in `src/test/java/MultiMartTest.java` and run in priority order.

| # | Test | What it checks |
|---|------|----------------|
| 1 | `tc01_verifyHomepageTitle` | Homepage title or page source contains "MultiMart" |
| 2 | `tc02_verifyLoginPageLoads` | `/login` shows the email and password fields |
| 3 | `tc03_loginInvalidCredentials` | Logging in with wrong credentials shows an invalid/failed/error message |
| 4 | `tc04_verifyRegistrationForm` | `/register` has at least 3 input fields |
| 5 | `tc05_verifyStoresDisplayed` | `/stores` lists at least one store link (`/stores/...`) |
| 6 | `tc06_verifyCategoryFilterUpdatesPage` | Clicking a filter button on `/stores` keeps the user on the stores page |
| 7 | `tc07_cartQuantityIncrease` | Clicks the first `+` quantity button on `/cart` if one exists |
| 8 | `tc08_cartRemoveItem` | Clicks the first remove button on `/cart` if one exists |
| 9 | `tc09_registerStoreUrlValid` | `/register-store` loads at the expected URL |
| 10 | `tc10_verifySearchBarPresentOnHome` | Homepage shows a search input (placeholder contains "Search") |
| 11 | `tc11_verifyQuickLoginMenuPresent` | Login page renders the demo quick-login section |
| 12 | `tc12_verifyCartPageUrlAndRendering` | `/cart` loads and the page body is displayed |
| 13 | `tc13_verifyAppNavigationLinks` | Homepage renders at least 3 links |
| 14 | `tc14_verifyStoreRegistrationInputs` | `/register-store` has at least 2 input fields |
| 15 | `tc15_verifyGlobalAppRendering` | The React `#root` element is present |

Browser setup: Chrome via WebDriverManager, headless (`--headless=new`), `--no-sandbox`, `--disable-dev-shm-usage`, window size 1920x1080, 10-second explicit waits.

## Project Structure

```
multimart-tests/
├── Jenkinsfile                     # CI pipeline (maven-chrome container, email report)
├── pom.xml                         # Selenium, TestNG, WebDriverManager, Surefire
└── src/test/java/
    └── MultiMartTest.java          # The 15 E2E test cases
```

## Prerequisites

- JDK 17
- Maven
- Google Chrome (the matching ChromeDriver is downloaded by WebDriverManager)
- MultiMart running and reachable at `http://localhost:5173` with seeded data (see the [app repo](https://github.com/usmanbinamjad555/multimart-project))

## Running the Tests

```bash
mvn clean test
```

To run only this class (as the app's pipeline does):

```bash
mvn clean test -Dtest=MultiMartTest
```

Reports are written to `target/surefire-reports/`.

## CI

The `Jenkinsfile` runs the whole pipeline inside the `markhobson/maven-chrome:jdk-17` Docker image (as root, Docker socket mounted, `--shm-size=2g`):

1. **Checkout** — checks out the repository.
2. **Run Tests** — runs `mvn clean test`.
3. **Post (always)** — reads the committer's email from the latest commit and sends an HTML email with the build status, a link to the build, and the console log attached (requires the Jenkins Email Extension plugin).

The app repository's own pipeline also clones this repo and runs the suite after deploying MultiMart with Docker Compose.

---

Author: Muhammad Usman — [LinkedIn](https://www.linkedin.com/in/muhammad-usman-the-dev) · [Portfolio](https://portfolio-usman-46c4.vercel.app)
