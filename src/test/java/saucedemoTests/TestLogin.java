package saucedemoTests;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TestLogin {
    private static final String URL = "https://www.saucedemo.com/";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    @Parameters("browser")
    public void setUp(@Optional("firefox") String browser) {
        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Browser " + browser + " is not supported.");
        }

        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(URL);
    }

    @Test(priority = 1)
    public void testValidLogin() {
        testLogin("standard_user", "secret_sauce");
        testLogin("error_user", "secret_sauce");
        testLogin("visual_user", "secret_sauce");
    }

    private void testLogin(String username, String password) {
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        // Attendre que la page se charge complètement
        wait.until(ExpectedConditions.titleIs("Swag Labs"));

        // Déconnexion pour revenir à la page de connexion
        WebElement menuButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-burger-menu-btn")));
        menuButton.click();

        WebElement logoutLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout_sidebar_link")));
        logoutLink.click();
    }

    @Test(priority = 2)
    public void testInvalidLogin() {
        testLoginInv("invalid_user", "invalid_password");
        testLoginInv("locked_out_user", "secret_sauce");
        testLoginInv("problem_us", "secret_sauce");
        testLoginInv("performance", "secret_sauce");
    }

    private void testLoginInv(String username, String password) {
        String msg_Erreur = "h3[data-test='error']";
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        // Effacer le champ Username et celui du mot de passe avant de saisir une nouvelle valeur
        usernameField.clear();
        passwordField.clear();

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        // Attendre que le message d'erreur soit affiché
        WebElement errorMessage =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(msg_Erreur)));

        // Vérifier que le message d'erreur est affiché
        String expectedErrorMessage = "Epic sadface: Username and password do not match any user in this service";
        if (!errorMessage.getText().equals(expectedErrorMessage)) {
            System.out.println("Le message d'erreur n'est pas affiché correctement");
        }

        // Réinitialiser les champs de saisie
        usernameField.clear();
        passwordField.clear();
    }

    @AfterTest
    public void tearDown() {
        System.out.println("Le teardown");
        driver.quit();
    }
}