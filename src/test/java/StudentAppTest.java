import com.github.javafaker.Faker;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import page_object.AddStudentPage;
import page_object.AllStudentsPage;
import page_object.Notifications;

import java.lang.reflect.Method;
import java.time.Duration;

import static constants.AllConstants.GenderConstants.MALE;
import static constants.AllConstants.Messages.STUDENT_SUCCESSFULLY_ADDED;
import static constants.AllConstants.Messages.WAS_ADDED_TO_THE_SYSTEM;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static utils.ConfigHelper.getConfig;
import static utils.DriverManager.*;

public class StudentAppTest {
    private WebDriverWait driverWait;
    private AllStudentsPage allStudentsPage;
    private AddStudentPage addStudentPage;
    private Notifications notifications;

    private final Faker dataFaker = new Faker();

    @BeforeMethod(alwaysRun = true)
    public void initialize(Method method) {
        testName = (method.getName());
        driverWait = new WebDriverWait(getInstance(), Duration.ofSeconds(10));
        getInstance().get(getConfig().getString("student.app.hostname"));
        allStudentsPage = new AllStudentsPage();
        addStudentPage = new AddStudentPage();
        notifications = new Notifications();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!getConfig().getBoolean("student.app.run.locally")) markRemoteTest(result);
        closeDriver();
    }

    @Test(description = "Add student and check successful message")
    public void openStudentApp() {
        allStudentsPage.waitAndClickOnAddStudentButton();

        String name = dataFaker.name().firstName();
        String email = dataFaker.internet().emailAddress();
        addStudentPage.waitAndSetValueForNameField(name);
        addStudentPage.waitAndSetValueForEmailField(email);
        addStudentPage.waitAndSetGender(MALE);
        addStudentPage.clickOnSubmitButton();

        assertEquals(notifications.getMessageFromNotification(), STUDENT_SUCCESSFULLY_ADDED);
        assertEquals(notifications.getDescriptionFromNotification(), String.format(WAS_ADDED_TO_THE_SYSTEM, name));

        notifications.getPopUpCloseButton().click();
        assertTrue(driverWait.until(ExpectedConditions.invisibilityOf(notifications.getPopUpCloseButton())));
    }

    @Test()
    public void checkTitle() {
        assertEquals(getInstance().getTitle(), "acodemy - React App");
    }
}



