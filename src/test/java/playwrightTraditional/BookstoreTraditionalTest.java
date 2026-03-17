package playwrightTraditional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import playwrightsupport.PlaywrightTestBase;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookstoreTraditionalTest extends PlaywrightTestBase {

    @BeforeAll
    void setUp() {
        startSession("videos/traditional");
    }

    @AfterAll
    void tearDown() {
        closeSession();
    }

    @Test
    @Order(1)
    @DisplayName("Traditional TestCase Bookstore")
    void testCaseBookstore() {
        goToHomePage();
        searchForEarbuds();
        expandFilterAndChooseOption("Brand", "JBL");
        expandFilterAndChooseOption("Color", "Black");
        expandFilterAndChooseOption("Price", "Over $50");
        openExpectedProduct();
        assertProductPageDetails();
        addOneItemToCart();
        openCart();
        assertCartPageValuesBeforeTax();
    }

    @Test
    @Order(2)
    @DisplayName("Traditional TestCase Your Shopping Cart Page")
    void testCaseYourShoppingCartPage() {
        chooseFastInStorePickup();
        applyInvalidPromoCode();
        assertPromoRejected();
        proceedToCheckout();
        assertCreateAccountPage();
    }

    @Test
    @Order(3)
    @DisplayName("Traditional TestCase Create Account Page")
    void testCaseCreateAccountPage() {
        assertCreateAccountPage();
        continueAsGuest();
    }

    @Test
    @Order(4)
    @DisplayName("Traditional TestCase Contact Information Page")
    void testCaseContactInformationPage() {
        assertContactInformationPage();
        fillContactInformation();
    }

    @Test
    @Order(5)
    @DisplayName("Traditional TestCase Pickup Information")
    void testCasePickupInformation() {
        assertPickupInformationPage();
        continueFromPickupInformation();
    }

    @Test
    @Order(6)
    @DisplayName("Traditional TestCase Payment Information")
    void testCasePaymentInformation() {
        assertPaymentInformationPage();
        goBackToCartFromPaymentPage();
    }

    @Test
    @Order(7)
    @DisplayName("Traditional TestCase Your Shopping Cart Delete Item")
    void testCaseDeleteFromCart() {
        openCart();
        deleteProductFromCart();
        assertCartIsEmpty();
    }
}