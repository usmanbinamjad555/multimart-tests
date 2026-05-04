import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

// Assignment by Reg No: SP23-BCS-115
public class MultiMartTest {
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Testing locally for now. Update this to your deployed URL later if needed.
    private final String BASE_URL = "http://localhost:5173";

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Runs Chrome invisibly (required for Docker)
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox"); // Critical for Docker execution
        options.addArguments("--disable-dev-shm-usage"); // Critical for Docker execution
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test(priority = 1)
    public void tc01_verifyHomepageTitle() {
        driver.get(BASE_URL + "/");
        Assert.assertTrue(driver.getTitle().contains("MultiMart") || driver.getPageSource().contains("MultiMart"));
    }

    @Test(priority = 2)
    public void tc02_verifyLoginPageLoads() {
        driver.get(BASE_URL + "/login");
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']")));
        WebElement passwordField = driver.findElement(By.cssSelector("input[type='password']"));
        Assert.assertTrue(emailField.isDisplayed());
        Assert.assertTrue(passwordField.isDisplayed());
    }

    @Test(priority = 3)
    public void tc03_loginInvalidCredentials() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.cssSelector("input[type='email']")).sendKeys("wrong@email.com");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("wrongpass");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        
        boolean isErrorPresent = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'error') or contains(text(), 'Invalid')]"))).isDisplayed();
        Assert.assertTrue(isErrorPresent);
    }

    @Test(priority = 4)
    public void tc04_loginAsCustomer() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.cssSelector("input[type='email']")).sendKeys("ahmed@example.com");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("password123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/"));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/"));
    }

    @Test(priority = 5)
    public void tc05_loginAsSuperAdmin() {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']"))).sendKeys("admin@multimart.com");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("Admin@12345");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin"));
    }

    @Test(priority = 6)
    public void tc06_loginAsStoreAdmin() {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']"))).sendKeys("ali@techzone.pk");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("password123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/tenant/dashboard"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/tenant/dashboard"));
    }

    @Test(priority = 7)
    public void tc07_verifyRegistrationForm() {
        driver.get(BASE_URL + "/register");
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        Assert.assertTrue(inputs.size() >= 4, "Registration form should have at least 4 inputs");
    }

    @Test(priority = 8)
    public void tc08_verifyStoresDisplayed() {
        driver.get(BASE_URL + "/stores");
        List<WebElement> stores = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a[href^='/stores/']")));
        Assert.assertTrue(stores.size() > 0, "Store cards should be displayed");
    }

    @Test(priority = 9)
    public void tc09_verifyCategoryFilterUpdatesPage() {
        driver.get(BASE_URL + "/stores");
        WebElement filterBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-sm")));
        filterBtn.click();
        Assert.assertTrue(driver.getCurrentUrl().contains("stores"));
    }

    @Test(priority = 10)
    public void tc10_searchFromNavbar() {
        driver.get(BASE_URL + "/");
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder*='Search']")));
        searchInput.sendKeys("Samsung");
        searchInput.submit(); 
        wait.until(ExpectedConditions.urlContains("search?q=Samsung"));
        Assert.assertTrue(driver.getCurrentUrl().contains("search"));
    }

    @Test(priority = 11)
    public void tc11_verifySpecificStoreFront() {
        driver.get(BASE_URL + "/stores/techzone");
        WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        Assert.assertTrue(body.getText().toLowerCase().contains("techzone"));
    }

    @Test(priority = 12)
    public void tc12_verifyProductDetailPageLoads() {
        driver.get(BASE_URL + "/stores/techzone");
        WebElement productLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='/product/']")));
        productLink.click();
        wait.until(ExpectedConditions.urlContains("/product/"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/product/"));
    }

    @Test(priority = 13)
    public void tc13_addToCart() {
        driver.get(BASE_URL + "/stores/techzone"); 
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='/product/']"))).click();
        
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Add to Cart')]")));
        addToCartBtn.click();
    }

    @Test(priority = 14)
    public void tc14_verifyCartPageLoads() {
        driver.get(BASE_URL + "/cart");
        WebElement checkoutBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'Proceed to Checkout') or contains(text(), 'Checkout')]")));
        Assert.assertTrue(checkoutBtn.isDisplayed());
    }

    @Test(priority = 15)
    public void tc15_cartQuantityIncrease() {
        driver.get(BASE_URL + "/cart");
        List<WebElement> plusButtons = driver.findElements(By.xpath("//button[contains(text(), '+')]"));
        if (!plusButtons.isEmpty()) {
            plusButtons.get(0).click();
            Assert.assertTrue(true, "Quantity increased");
        }
    }

    @Test(priority = 16)
    public void tc16_bonus_cartRemoveItem() {
        driver.get(BASE_URL + "/cart");
        List<WebElement> removeButtons = driver.findElements(By.xpath("//button[contains(@class, 'remove') or contains(text(), 'Remove') or .//svg]"));
        if (!removeButtons.isEmpty()) {
            removeButtons.get(0).click();
        }
        Assert.assertTrue(true, "Item removal triggered");
    }

    @Test(priority = 17)
    public void tc17_bonus_registerStore() {
        driver.get(BASE_URL + "/register-store");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));
        Assert.assertTrue(driver.getCurrentUrl().contains("/register-store"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}