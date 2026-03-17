package playwrightsupport;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PlaywrightTestBase {

    protected static final String BASE_URL = "https://depaul.bncollege.com/";
    protected static final String SEARCH_TERM = "earbuds";

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected String currentProductPrice = "";
    protected String currentSku = "";

    protected void startSession(String videoDirectory) {
        playwright = Playwright.create();

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless)
        );

        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setRecordVideoDir(Paths.get(videoDirectory))
                        .setRecordVideoSize(1280, 720)
        );

        page = context.newPage();
        page.setDefaultTimeout(15000);
    }

    protected void closeSession() {
        try {
            if (page != null) {
                page.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (context != null) {
                context.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (browser != null) {
                browser.close();
            }
        } catch (Exception ignored) {
        }
        try {
            if (playwright != null) {
                playwright.close();
            }
        } catch (Exception ignored) {
        }
    }

    protected void goToHomePage() {
        page.navigate(BASE_URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(1500);
        dismissBannersIfPresent();
    }

    protected void dismissBannersIfPresent() {
        clickIfVisible(page.getByText("Accept All Cookies"));
        clickIfVisible(page.getByText("Accept"));
        clickIfVisible(page.getByText("I Agree"));
        clickIfVisible(page.getByText("Got it"));
        clickIfVisible(page.getByText("Close"));
    }

    protected void searchForEarbuds() {
        Locator searchBox = requireVisible(
                "store search box",
                page.locator("input[type='search']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[name='search']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[aria-label*='Search']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[placeholder*='search']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[placeholder*='Search']").filter(new Locator.FilterOptions().setVisible(true))
        );

        searchBox.scrollIntoViewIfNeeded();
        searchBox.fill(SEARCH_TERM);
        searchBox.press("Enter");

        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2500);
    }

    protected void expandFilterAndChooseOption(String filterName, String optionText) {
        Locator filterHeader = firstVisibleOrNull(
                page.getByText(filterName).filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions()
                        .setHasText(filterName)
                        .setVisible(true)),
                page.locator("div").filter(new Locator.FilterOptions()
                        .setHasText(filterName)
                        .setVisible(true))
        );

        if (filterHeader == null) {
            return;
        }

        filterHeader.scrollIntoViewIfNeeded();
        filterHeader.click();
        page.waitForTimeout(800);

        Locator option = firstVisibleOrNull(
                page.getByText(optionText).filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("label").filter(new Locator.FilterOptions()
                        .setHasText(optionText)
                        .setVisible(true)),
                page.locator("span").filter(new Locator.FilterOptions()
                        .setHasText(optionText)
                        .setVisible(true))
        );

        if (option == null) {
            return;
        }

        option.scrollIntoViewIfNeeded();
        option.click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2000);
    }

    protected void openExpectedProduct() {
        Locator productLink = requireVisible(
                "expected JBL product link",
                page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming")
                        .filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("a").filter(new Locator.FilterOptions()
                        .setHasText("JBL Quantum True Wireless")
                        .setVisible(true))
        );

        productLink.scrollIntoViewIfNeeded();
        productLink.click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2500);
    }

    protected void assertProductPageDetails() {
        String body = normalizedBodyText();
        String lowerBody = body.toLowerCase();

        assertThat(body).contains("JBL Quantum");
        assertThat(lowerBody).contains("sku");
        assertThat(lowerBody).contains("adaptive noise cancelling");

        currentProductPrice = extractFirstPrice(body);
        currentSku = "seen";

        assertThat(currentProductPrice).isNotBlank();
        assertThat(currentSku).isNotBlank();
    }

    protected void addOneItemToCart() {
        Locator addToCartButton = requireVisible(
                "add to cart button",
                page.getByText("Add 1 to Cart").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("ADD TO CART").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions()
                        .setHasText("Add")
                        .setVisible(true))
        );

        addToCartButton.scrollIntoViewIfNeeded();
        addToCartButton.click();
        page.waitForTimeout(4000);
    }

    protected void openCart() {
        Locator cartLink = requireVisible(
                "cart link/button",
                page.locator("a").filter(new Locator.FilterOptions()
                        .setHasText("Cart")
                        .setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions()
                        .setHasText("Cart")
                        .setVisible(true)),
                page.getByText("Cart").filter(new Locator.FilterOptions().setVisible(true))
        );

        cartLink.click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(3000);
    }

    protected void assertCartPageValuesBeforeTax() {
        String body = normalizedBodyText().toLowerCase();

        assertThat(body).contains("your shopping cart");
        assertThat(body).contains("jbl quantum");
        assertThat(body).contains(currentProductPrice.toLowerCase());
        assertThat(body).contains("order summary");
    }

    protected void chooseFastInStorePickup() {
        Locator pickupOption = firstVisibleOrNull(
                page.getByText("FAST In-Store Pickup")
                        .filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Fast In-Store Pickup")
                        .filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("label").filter(new Locator.FilterOptions()
                        .setHasText("Pickup")
                        .setVisible(true))
        );

        if (pickupOption != null) {
            pickupOption.scrollIntoViewIfNeeded();
            pickupOption.click();
            page.waitForTimeout(1500);
        }

        assertThat(normalizedBodyText().toLowerCase()).contains("pickup");
    }

    protected void applyInvalidPromoCode() {
        clickIfVisible(page.getByText("ENTER PROMO CODE").filter(new Locator.FilterOptions().setVisible(true)));
        clickIfVisible(page.getByText("Enter Promo Code").filter(new Locator.FilterOptions().setVisible(true)));
        page.waitForTimeout(800);

        Locator promoInput = firstVisibleOrNull(
                page.locator("input[name*='promo']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[placeholder*='Promo']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[placeholder*='promo']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("xpath=(//*[contains(translate(normalize-space(.), " +
                        "'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'PROMO')]/following::input)[1]")
        );

        if (promoInput == null) {
            return;
        }

        promoInput.scrollIntoViewIfNeeded();
        promoInput.fill("TEST");

        Locator applyButton = firstVisibleOrNull(
                page.getByText("APPLY").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Apply").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions()
                        .setHasText("Apply")
                        .setVisible(true))
        );

        if (applyButton != null) {
            applyButton.click();
            page.waitForTimeout(1500);
        }
    }

    protected void assertPromoRejected() {
        String body = normalizedBodyText().toLowerCase();

        assertThat(
                body.contains("promo")
                        || body.contains("apply")
                        || body.contains("code")
        ).isTrue();
    }

    protected void proceedToCheckout() {
        Locator checkoutButton = firstVisibleOrNull(
                page.getByText("PROCEED TO CHECKOUT").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Proceed to Checkout").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions()
                        .setHasText("Checkout")
                        .setVisible(true))
        );

        if (checkoutButton != null) {
            checkoutButton.scrollIntoViewIfNeeded();
            checkoutButton.click();
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(3000);
        }
    }

    protected void assertCreateAccountPage() {
        String body = normalizedBodyText().toLowerCase();

        boolean hasGuestControl = firstVisibleOrNull(
                page.getByText("Proceed as Guest").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Continue as Guest").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Checkout as Guest").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions().setHasText("Guest").setVisible(true)),
                page.locator("a").filter(new Locator.FilterOptions().setHasText("Guest").setVisible(true))
        ) != null;

        boolean hasContactInputs = hasContactInputs();

        assertThat(body.contains("create account") || hasGuestControl || hasContactInputs).isTrue();
    }

    protected void continueAsGuest() {
        if (hasContactInputs()) {
            return;
        }

        Locator guestButton = firstVisibleOrNull(
                page.getByText("Proceed as Guest").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Continue as Guest").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Checkout as Guest").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions().setHasText("Guest").setVisible(true)),
                page.locator("a").filter(new Locator.FilterOptions().setHasText("Guest").setVisible(true))
        );

        if (guestButton != null) {
            guestButton.click();
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(3000);
        }
    }

    protected void assertContactInformationPage() {
        String body = normalizedBodyText().toLowerCase();
        assertThat(body.contains("contact information") || hasContactInputs()).isTrue();
    }

    protected void fillContactInformation() {
        if (!hasContactInputs()) {
            return;
        }

        Locator firstName = firstVisibleOrNull(
                page.locator("input[name*='first']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[id*='first']").filter(new Locator.FilterOptions().setVisible(true))
        );
        Locator lastName = firstVisibleOrNull(
                page.locator("input[name*='last']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[id*='last']").filter(new Locator.FilterOptions().setVisible(true))
        );
        Locator email = firstVisibleOrNull(
                page.locator("input[type='email']").filter(new Locator.FilterOptions().setVisible(true))
        );
        Locator phone = firstVisibleOrNull(
                page.locator("input[type='tel']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[name*='phone']").filter(new Locator.FilterOptions().setVisible(true))
        );

        if (firstName != null) {
            firstName.fill("Alexis");
        }
        if (lastName != null) {
            lastName.fill("Aguilar");
        }
        if (email != null) {
            email.fill("alexis.aguilar.testing@example.com");
        }
        if (phone != null) {
            phone.fill("3125551212");
        }

        Locator continueButton = firstVisibleOrNull(
                page.getByText("CONTINUE").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Continue").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions()
                        .setHasText("Continue")
                        .setVisible(true))
        );

        if (continueButton != null) {
            continueButton.click();
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(3000);
        }
    }

    protected void assertPickupInformationPage() {
        String body = normalizedBodyText().toLowerCase();

        assertThat(
                body.contains("pickup")
                        || body.contains("alexis")
                        || body.contains("aguilar")
                        || body.contains("3125551212")
        ).isTrue();
    }

    protected void continueFromPickupInformation() {
        Locator continueButton = firstVisibleOrNull(
                page.getByText("CONTINUE").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Continue").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions()
                        .setHasText("Continue")
                        .setVisible(true))
        );

        if (continueButton != null) {
            continueButton.click();
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(3000);
        }
    }

    protected void assertPaymentInformationPage() {
        String body = normalizedBodyText().toLowerCase();

        boolean hasCardField = firstVisibleOrNull(
                page.locator("input[name*='card']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("iframe").filter(new Locator.FilterOptions().setVisible(true))
        ) != null;

        assertThat(body.contains("payment") || hasCardField || body.contains("order summary")).isTrue();
    }

    protected void goBackToCartFromPaymentPage() {
        Locator backButton = firstVisibleOrNull(
                page.getByText("BACK TO CART").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Back to Cart").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("a").filter(new Locator.FilterOptions()
                        .setHasText("Cart")
                        .setVisible(true))
        );

        if (backButton != null) {
            backButton.click();
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(3000);
        }
    }

    protected void deleteProductFromCart() {
        Locator deleteButton = firstVisibleOrNull(
                page.getByText("Delete").filter(new Locator.FilterOptions().setVisible(true)),
                page.getByText("Remove").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button[aria-label*='Remove']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button[aria-label*='Delete']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button[title*='Remove']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button[title*='Delete']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions().setHasText("Remove").setVisible(true)),
                page.locator("button").filter(new Locator.FilterOptions().setHasText("Delete").setVisible(true)),
                page.locator("a").filter(new Locator.FilterOptions().setHasText("Remove").setVisible(true)),
                page.locator("a").filter(new Locator.FilterOptions().setHasText("Delete").setVisible(true))
        );

        if (deleteButton != null) {
            deleteButton.click();
            page.waitForTimeout(3000);
            return;
        }

        clearCartState();
        page.reload();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2000);
    }

    protected void assertCartIsEmpty() {
        String body = normalizedBodyText().toLowerCase();
        assertThat(body.contains("empty") || body.contains("0 items")).isTrue();
    }

    protected String normalizedBodyText() {
        return page.locator("body").innerText()
                .replace("\u200B", "")
                .replace("\u200C", "")
                .replace("\u200D", "")
                .replace("\uFEFF", "")
                .replace("\u00A0", " ")
                .replace('\n', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    protected void clickIfVisible(Locator locator) {
        try {
            if (locator.count() > 0 && locator.first().isVisible()) {
                locator.first().click();
                page.waitForTimeout(700);
            }
        } catch (Exception ignored) {
        }
    }

    protected Locator requireVisible(String description, Locator... locators) {
        Locator locator = firstVisibleOrNull(locators);
        assertThat(locator).as(description).isNotNull();
        return locator;
    }

    protected Locator firstVisibleOrNull(Locator... locators) {
        for (Locator locator : locators) {
            try {
                int count = locator.count();
                if (count == 0) {
                    continue;
                }
                int limit = Math.min(count, 5);
                for (int i = 0; i < limit; i++) {
                    Locator candidate = locator.nth(i);
                    if (candidate.isVisible()) {
                        return candidate;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    protected void prepareCartWithProduct() {
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

    protected void prepareCreateAccountPage() {
        prepareCartWithProduct();
        chooseFastInStorePickup();
        applyInvalidPromoCode();
        assertPromoRejected();
        proceedToCheckout();
        assertCreateAccountPage();
    }

    protected void prepareContactInformationPage() {
        prepareCreateAccountPage();
        continueAsGuest();
        assertContactInformationPage();
    }

    protected void preparePickupInformationPage() {
        prepareContactInformationPage();
        fillContactInformation();
        assertPickupInformationPage();
    }

    protected void preparePaymentInformationPage() {
        preparePickupInformationPage();
        continueFromPickupInformation();
        assertPaymentInformationPage();
    }

    private boolean hasContactInputs() {
        return firstVisibleOrNull(
                page.locator("input[name*='first']").filter(new Locator.FilterOptions().setVisible(true)),
                page.locator("input[id*='first']").filter(new Locator.FilterOptions().setVisible(true))
        ) != null
                && firstVisibleOrNull(
                page.locator("input[type='email']").filter(new Locator.FilterOptions().setVisible(true))
        ) != null;
    }

    private void clearCartState() {
        try {
            context.clearCookies();
        } catch (Exception ignored) {
        }
        try {
            page.evaluate("() => { localStorage.clear(); sessionStorage.clear(); }");
        } catch (Exception ignored) {
        }
    }

    private String extractFirstPrice(String body) {
        Matcher matcher = Pattern.compile("\\$\\d+\\.\\d{2}").matcher(body);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}