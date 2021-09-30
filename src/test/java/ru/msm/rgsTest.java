package ru.msm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class rgsTest extends BaseClass {

    private static Stream<Arguments> valuesFIO() {
        return Stream.of(
                Arguments.of("Иванов", "Иван", "Иванович", "77",
                        "4959999999", "qwertyqwerty", "18112021", "Первый комментарий"),
                Arguments.of("Петров", "Петр", "Петрович", "78",
                        "8128887799", "qweruiop", "22112021", "Второй комментарий"),
                Arguments.of("Елисеева", "Екатерина", "Александровна", "28",
                        "4162200769", "asdfjkl;", "05112021", "Второй комментарий")
        );
    }

    @ParameterizedTest(name = "Заполнение ФИО")
    @MethodSource("valuesFIO")
    public void testApplicationDMS(String lastName, String firstName, String middleName, String regionN, String phone,
                                   String email, String date, String comm) {

        cleanScreenForClick();
        // выбрать Меню
        String menuButtonXPath = "//a[@data-toggle='dropdown' and contains(text(), 'Меню')]";
        WebElement menuButton = driver.findElement(By.xpath(menuButtonXPath));
        waitUtilElementToBeClickable(menuButton);
        menuButton.click();

        cleanScreenForClick();
        //2. Выбрать пункт меню – Страхование (Здоровье???)
        String healthTitleXPath = "//a[@href='https://www.rgs.ru/products/private_person/health/index.wbp' and contains(text(), 'Здоровье')]";
        WebElement healthTitle = driver.findElement(By.xpath(healthTitleXPath));
        waitUtilElementToBeClickable(healthTitle);
        healthTitle.click();

        cleanScreenForClick();
        //3. Выбрать категорию – ДМС
        String dmsHealthXPath = "//a[@class='list-group-item adv-analytics-navigation-line4-link' and contains(text(), 'обровольное')]";
        WebElement dmsHealth = driver.findElement(By.xpath(dmsHealthXPath));
        waitUtilElementToBeClickable(dmsHealth);
        dmsHealth.click();

        //4. Проверить наличие заголовка - Добровольное медицинское страхование
        String pageHeaderXPath = "//h1";
        waitUtilElementToBeVisible(By.xpath(pageHeaderXPath));
        WebElement pageHeader = driver.findElement(By.xpath(pageHeaderXPath));
        Assertions.assertEquals("ДМС — добровольное медицинское страхование",
                pageHeader.getText(),
                "Заголовок отсутствует/не соответствует требуемому");

        cleanScreenForClick();
        //5. Нажать на кнопку - Отправить заявку
        String sendRequestXPath = "//a[contains(@data-product, 'VoluntaryMedicalInsurance') and contains(text(), 'Отправить заявку')]";
        WebElement sendRequestEl = driver.findElement(By.xpath(sendRequestXPath));
        waitUtilElementToBeClickable(sendRequestEl);
        sendRequestEl.click();

        //6. Проверить, что открылась страница , на которой присутствует текст - Заявка на добровольное медицинское страхование
        pageHeaderXPath = "//h4";   //или лучше создать новую.отдельную переменную?
        waitUtilElementToBeVisible(By.xpath(pageHeaderXPath));
        WebElement pageTitle = driver.findElement(By.xpath(pageHeaderXPath));
        Assertions.assertEquals("Заявка на добровольное медицинское страхование",
                pageTitle.getText(),
                "Заголовок отсутствует/не соответствует требуемому");

        //7. Заполнить поля: Имя, Фамилия, Отчество, Регион, Телефон, Эл. почта – qwertyqwerty, Комментарии, Я согласен на обработку
        //8. Проверить, что все поля заполнены введенными значениями
        String fieldXPath = "//form//*[@data-bind and @name='%s']";

        //  Фамилия
        fillInputField(String.format(fieldXPath, "LastName"), lastName);
        //  Имя
        fillInputField(String.format(fieldXPath, "FirstName"), firstName);
        //  Отчество
        fillInputField(String.format(fieldXPath, "MiddleName"), middleName);

        //  Регион    (переделать?)
        String regionBaseXP = "//form//*[@data-bind and @name='Region']";
        WebElement region = driver.findElement(By.xpath(regionBaseXP));
        region.click();
        WebElement moscow = driver.findElement(By.xpath(regionBaseXP + String.format("//option[@value='%s']", regionN)));
        moscow.click();
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(region, "value", regionN));
        Assertions.assertTrue(checkFlag, "Поле было заполнено некорректно");

        //  Эл. почта
        fillInputField(String.format(fieldXPath, "Email"), email);

        //  Телефон
        //String value = "4959999999";
        String valueAtt = String.format("+7 (%s) %s-%s-%s", phone.substring(0, 3), phone.substring(3, 6),
                phone.substring(6, 8), phone.substring(8, 10));
        fillInputField("//form//*[@data-bind and not(@name)]", phone, valueAtt);

        //  Предпочитаемая дата контакта
        //value = "18112021";
        valueAtt = String.format("%s.%s.%s", date.substring(0, 2), date.substring(2, 4), date.substring(4, 8));
        fillInputField("//form//*[@data-bind and @name='ContactDate']", date, valueAtt);

        //Комментарии
        fillInputField(String.format(fieldXPath, "Comment"), comm);

        //  Я согласен на обработку моих персональных данных...
        WebElement checkbox = driver.findElement(By.xpath("//form//*[@class='checkbox']"));
        checkbox.click();
        checkFlag = wait.until(ExpectedConditions.attributeContains(checkbox, "checked", "true"));
        Assertions.assertTrue(checkFlag, "Поле было заполнено некорректно");

        //9. Нажать Отправить
        WebElement send = driver.findElement(By.xpath("//form//button[@type='button']"));
        send.click();

        //10. Проверить, что у поля – Эл. почта присутствует сообщение об ошибке – Введите корректный email
        checkErrorMessageAtField(driver.findElement(By.xpath(String.format(fieldXPath, "Email"))), "Введите адрес электронной почты");

    }

    /**
     * Проверка ошибка именно у конкретного поля
     *
     * @param element - веб элемент (поле какое-то) которое не заполнили
     * @param errorMessage - ожидаемая ошибка под полем которое мы не заполнили
     */
    private void checkErrorMessageAtField(WebElement element, String errorMessage) {
        element = element.findElement(By.xpath("./..//span"));
        Assertions.assertEquals(errorMessage,
                element.getText(), "Проверка ошибки у поля не была пройдена");
    }

    /**
     * Заполнение полей определёнными значениями
     *
     * @param completeFieldXPath - XPath веб элемента (какого-то поля), которое планируется заполнить
     * @param value - значение, которым заполнится веб элемент (какое-то поле)
     * @param valueAtt - значение атрибута "value" веб элемента (какого-то поля)
     */
    private void fillInputField(String completeFieldXPath, String value, String valueAtt) {
        WebElement element = driver.findElement(By.xpath(completeFieldXPath));
        scrollToElementJs(element);
        waitUtilElementToBeClickable(element);
        element.click();
        element.clear();
        element.click();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", valueAtt));
        Assertions.assertTrue(checkFlag, "Поле было заполнено некорректно");
    }

    private void fillInputField(String completeFieldXPath, String value) {
        fillInputField(completeFieldXPath, value, value);
    }

    private void switchToTabByText(String text) {
        String curTab = driver.getWindowHandle();
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        for (String tab : tabs) {
            if (!tab.equals(curTab)) {
                driver.switchTo().window(tab);
                if (driver.getTitle().contains(text)) {
                    return;
                }
            }
        }
        Assertions.fail("Вкладка " + text + " не найдена.");
    }

    /**
     * Освобождение экрана от окон рекламы и Cookies
     */
    private void cleanScreenForClick() {
        closeCookies(By.xpath("//div[@class='btn btn-default text-uppercase']"));
        closeAddFrame(By.xpath("//iframe[@class='flocktory-widget']"),
                By.xpath("//button[@class='CloseButton']"));
        closeAddFrame(By.xpath("//iframe[@class='flocktory-widget']"),
                By.xpath("//button[@data-fl-close]"));
    }

    /**
     * Закрытие рекламы, находящейся в iframe
     * @param iframeBy - локатор iframe
     * @param by - локатор элемента, закрывающего рекламу
     */
    private void closeAddFrame(By iframeBy, By by) {        //подходит только для одной вложенности
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            WebElement iframe = driver.findElement(iframeBy);
            driver.switchTo().frame(iframe);
            closeAdd(by);
            driver.switchTo().parentFrame();
        } catch (NoSuchElementException | ElementNotInteractableException ignore) {

        } finally {
            driver.manage().timeouts().implicitlyWait(defaultWaitingTime, TimeUnit.SECONDS);
        }
    }

    private void closeCookies(By by) {
        closeAdd(by);
    }

    private void closeAdd(By by) {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            WebElement addEl = driver.findElement(by);
            addEl.click();
        } catch (NoSuchElementException | ElementNotInteractableException ignore) {

        } finally {
            driver.manage().timeouts().implicitlyWait(defaultWaitingTime, TimeUnit.SECONDS);
        }

    }

    /**
     * Скрол до элемента на js коде
     *
     * @param element - веб элемент до которого нужно проскролить
     */
    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Явное ожидание того что элемент станет кликабельный
     *
     * @param element - веб элемент до которого нужно проскролить
     */
    private void waitUtilElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Явное ожидание того что элемент станет видемым
     *
     * @param locator - локатор до веб элемент который мы ожидаем найти и который виден на странице
     */
    private void waitUtilElementToBeVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

}