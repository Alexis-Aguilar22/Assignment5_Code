package playwrightLLM;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import playwrightsupport.PlaywrightTestBase;

class BookstoreLLMTest extends PlaywrightTestBase {

    @AfterEach
    void tearDown() {
        closeSession();
    }

    @Test
    @DisplayName("LLM-generated style: DePaul bookstore guest checkout flow")
    void generatedGuestCheckoutFlow() {
        startSession("videos/llm");

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

        chooseFastInStorePickup();
        applyInvalidPromoCode();
        assertPromoRejected();

        proceedToCheckout();
        assertCreateAccountPage();
        continueAsGuest();

        assertContactInformationPage();
        fillContactInformation();

        assertPickupInformationPage();
        continueFromPickupInformation();

        assertPaymentInformationPage();
    }
}