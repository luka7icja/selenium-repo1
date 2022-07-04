import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProjektKoncowy {
    @FindBy(className = "login")
    private WebElement loginButton;
    @FindBy(id = "passwd")
    private WebElement passwordInput;
    @FindBy(id = "email")
    private WebElement loginInput;
    @FindBy(id = "SubmitLogin")
    private WebElement signInButton;
    @FindBy(css = "#contact-link > a")
    private WebElement contactButton;

    String login1 = "test_alicja@email.com";
    String haslo1 = "haslo123";
    String emailAddressRequired = "An email address required.";
    String passwordRequired = "Password is required.";
    String contactPageText = "SEND A MESSAGE";
    String homePageAddress = "http://automationpractice.com/index.php";
    String addedToCartConfirmation = "Product successfully added to your shopping cart";
    String emptyCartConfirmation = "Your shopping cart is empty.";

    public ProjektKoncowy() {
        PageFactory.initElements(driver,this);
    }
    static WebDriver driver = new ChromeDriver();
    @BeforeAll
    static void prepareBrowser() {
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @BeforeEach
    void openPageAndClearCookies() {
        driver.get("https://automationpractice.com");
        driver.manage().deleteAllCookies();
    }

    @AfterAll
    static void closeBrowser() {
        driver.quit();
    }

    //Zad 8.* (zadanie dodatkowe)
    //Zrefaktoruj logowanie. Utwórz metodę pomocniczą login(), która przymuje dwa parametry: login i hasło.
    //Użyj jej w teście sprawdzającym logowanie.
    void logIn(String login, String password) {
        loginButton.click();
        loginInput.sendKeys(login);
        passwordInput.sendKeys(password);
        signInButton.click();
    }

    //Zad 1.
    // Napisz test, który zweryfikuje działanie aplikacji,
    // gdy przy próbie logowania nie podano loginu.
    @Test
    void logInWithoutUsername() {
        logIn("", haslo1);
        Assertions.assertEquals(emailAddressRequired,
                driver.findElement(By.cssSelector(".alert.alert-danger > ol > li")).getText());
    }

    //Zad 2.
    // Napisz test, który zweryfikuje działanie aplikacji,
    // gdy przy próbie logowania nie podano hasła.
    @Test
    void logInWithoutPassword() {
        logIn(login1, "");
        Assertions.assertEquals(passwordRequired,
                driver.findElement(By.cssSelector(".alert.alert-danger > ol > li")).getText());
    }

    //Zad 3.
    //Sprawdź, czy strona główna oraz strona logowania zawiera logo i pole wyszukiwania.
    @Test
    void doesThePageContainLogoAndSearchField() {
        String source = driver.findElement(By.cssSelector("#header_logo > a > img")).getAttribute("src");
        Assertions.assertTrue(source.contains("logo.jpg"), "naStronieGlownejNieZnalezionoPlikuLogoJpg");
        int noOfSearchFields = driver.findElements(By.id("search_query_top")).size();
        Assertions.assertTrue(noOfSearchFields >= 1, "naStronieGlownejNieZnalezionoPolaWyszukiwania");
        driver.findElement(By.className("login")).click();
        Assertions.assertTrue(driver.findElement(By.cssSelector("#header_logo > a > img")).getAttribute("src").contains("logo.jpg"), "naStronieLogowaniaNieZnalezionoPlikuLogoJpg");
        int noOfSearchFields2 = driver.findElements(By.id("search_query_top")).size();
        Assertions.assertTrue(noOfSearchFields2 >= 1, "naStronieLogowaniaNieZnalezionoPolaWyszukiwania");
    }

    // Zad 4.
    // Napisz test sprawdzający przejście ze strony głównej do strony ”Kontakt”
    @Test
    void fromHomePageToContactPage() {
        contactButton.click();
        Assertions.assertEquals(contactPageText,
                driver.findElement(By.cssSelector("#center_column > form > fieldset > h3")).getText());
    }

    // Zad 5.
    // Napisz test sprawdzający przejście ze strony logowania do strony głównej. <--zaaw: po zalogowaniu
    @Test
    void fromLoggingPageToHomePage() {
        driver.get("http://automationpractice.com/index.php?controller=authentication&back=my-account");
        driver.findElement(By.className("logo")).click();
        String pageAddress = driver.getCurrentUrl();
        Assertions.assertEquals(homePageAddress, pageAddress);
    }

    //Zad 6.
    // Napisz test, który dodaje produkt do koszyka. Zweryfikuj, czy dodanie powiodło się.
    @Test
    void addingProductToCart() {
        driver.findElement(By.id("center_column")).findElement(By.className("product-name")).click();
        String productName = driver.findElement(By.cssSelector(".pb-center-column > h1")).getText();
        driver.findElement(By.cssSelector("#add_to_cart > button > span")).click();
        String confirmation = (driver.findElement(By.xpath("//*[@id='layer_cart']/div[1]/div[1]/h2")).getAttribute("textContent"));
        Assertions.assertEquals(addedToCartConfirmation, confirmation.trim());
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='layer_cart']//a")));
        driver.findElement(By.xpath("//*[@id='layer_cart']//a")).click();
        Assertions.assertEquals(productName, driver.findElement(By.xpath("//*[@id='product_1_1_0_0']//p/a")).getText());
    }

    // Zad 7.
    // Napisz test, który dodaje produkt do koszyka, a następnie usuwa go. Zweryfikuj, czy usunięcie powiodło się.
    @Test
    void removingFromCart() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.findElement(By.id("center_column")).findElement(By.className("product-name")).click();
        driver.findElement(By.cssSelector("#add_to_cart > button > span")).click();
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='layer_cart']//a")));
        driver.findElement(By.xpath("//*[@id='layer_cart']//a")).click();
        driver.findElement(By.cssSelector(".cart_quantity_delete > i")).click();
        Assertions.assertEquals(emptyCartConfirmation,
                driver.findElement(By.cssSelector("#center_column > p")).getAttribute("textContent"));
    }
}