package saucedemoTests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class TestProducts {
    private static final String URL = "https://www.saucedemo.com/";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    @Parameters("browser")
    public void setup(@Optional("chrome") String browser) {
        // Configuration des pilotes en fonction du navigateur spécifié
        switch (browser.toLowerCase()) {
            case "chrome":
                // Si le navigateur est Chrome, utiliser le pilote Chrome
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                // Si le navigateur est Firefox, utiliser le pilote Firefox
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                // Si le navigateur est Edge, utiliser le pilote Edge
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                // Si le navigateur spécifié n'est pas pris en charge, lancer une exception
                throw new IllegalArgumentException("Browser " + browser + " is not supported.");
        }

        // Initialisation du WebDriverWait avec une durée d'attente de 10 secondes
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Ouverture de l'URL du site de démo Sauce
        driver.get(URL);

        // Connexion avec un utilisateur standard
        String username = "standard_user";
        String password = "secret_sauce";
        driver.findElement(By.id("user-name")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
    }

    @Test
    public void testProductSortingByNameAZ() {
        // Sélection du filtre par nom de A à Z
        Select filterDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        filterDropdown.selectByValue("az");

        // Attendre que les éléments soient chargés
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("inventory_item_name")));

        // Récupérer la liste des noms des produits
        List<WebElement> productNames = driver.findElements(By.className("inventory_item_name"));

        // Vérifier que les produits sont triés par nom de A à Z
        String previousName = "";
        boolean sorted = true;
        for (WebElement productName : productNames) {
            String currentName = productName.getText();
            if (currentName.compareToIgnoreCase(previousName) < 0) {
                sorted = false;
                break;
            }
            previousName = currentName;
        }

        // Afficher le résultat du test
        if (sorted) {
            System.out.println("Les produits sont triés par nom de A à Z.");
        } else {
            System.out.println("Les produits ne sont pas triés par nom de A à Z.");
        }
    }

    @AfterTest
    public void tearDown() {
        // Fermeture du navigateur après l'exécution des tests
        if (driver != null) {
            driver.quit();
        }
    }
}