package saucedemoTests;

import org.openqa.selenium.edge.EdgeDriver;
import org.testng.annotations.AfterSuite;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.annotations.*;
import java.time.Duration;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TestLogin {
    private static final String URL = "https://www.saucedemo.com/";
    private static WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    public void setUp() {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(URL);
    }
    @Test(priority = 1)
    public void testValidLogin() {


        testLoginV("standard_user", "secret_sauce");
        testLoginV("error_user", "secret_sauce");
        testLoginV("visual_user", "secret_sauce");

    }

    private void testLoginV(String username, String password) {
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        // Attendre que la page se charge complètement
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Vérifier que le titre de la page est "Swag Labs" pour les valeurs valides
        String pageTitle = driver.getTitle();
        Assert.assertEquals(pageTitle, "Swag Labs");

        // Déconnexion pour revenir à la page de connexion
        WebElement menuButton =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-burger-menu-btn")));
        //WebElement menuButton = driver.findElement(By.id("react-burger-menu-btn"));
        menuButton.click();

        // Attendre que le bouton de déconnexion soit visible
        WebElement logoutLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout_sidebar_link")));
        logoutLink.click();



    }
    @Test(priority = 2)
    public void testInvalidLogin() throws InterruptedException{
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
        Assert.assertEquals(errorMessage.getText(), expectedErrorMessage, "Le message d'erreur n'est pas affiché correctement");

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