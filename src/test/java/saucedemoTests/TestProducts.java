package saucedemoTests;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestProducts {
    private static final String URL = "https://www.saucedemo.com/";
    private static WebDriver driver;
    private WebDriverWait wait;

    String username = "standard_user";
    String password = "secret_sauce";
    @BeforeTest
    public void setUp() {
        // Configuration du navigateur
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();;
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(URL);

        // Connexion avec un utilisateur standard
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
        if (driver != null) {
            driver.quit();
        }
    }
}