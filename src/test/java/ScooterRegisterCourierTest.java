import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ScooterRegisterCourierTest {

    CourierClient courierClient;
    Courier courier;
    int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandom();
    }

    @After
    public void tearDown(){
            if(courierId != 0){
                courierClient.delete(courierId);
            }
    }

    @Test
    public void courierCanBeCreated(){
        ValidatableResponse createResponse = courierClient.create(new Courier(courier.getLogin(), courier.getPassword(), courier.getFirstName()));
        int statusCode = createResponse.extract().statusCode();
        boolean result = createResponse.extract().path("ok");

        assertThat("Can't create courier", statusCode, equalTo(SC_CREATED));
        assertThat(String.valueOf(result), true);
    }

    @Test
    public void courierCantBeCreatedWithoutLogin(){
        ValidatableResponse createResponse = courierClient.create(new Courier("", courier.getPassword(), courier.getFirstName()));
        int statusCode = createResponse.extract().statusCode();
        String result = createResponse.extract().path("message");

        assertThat("Can't create courier", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(result, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void courierCantBeCreatedWithoutPassword(){
        ValidatableResponse createResponse = courierClient.create(new Courier(courier.getLogin(), "", courier.getFirstName()));
        int statusCode = createResponse.extract().statusCode();
        String result = createResponse.extract().path("message");

        assertThat("Courier created", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(result, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void courierCantBeCreatedWithoutName(){
        ValidatableResponse createResponse = courierClient.create(new Courier(courier.getLogin(), courier.getPassword(), ""));
        int statusCode = createResponse.extract().statusCode();
        String result = createResponse.extract().path("message");

        assertThat("Courier created", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(result, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void courierCantBeCreatedWithExistingLogin(){
        courierClient.create(new Courier("Login", courier.getPassword(), courier.getFirstName()));
        ValidatableResponse createResponse = courierClient.create(new Courier("Login", courier.getPassword(), courier.getFirstName()));
        int statusCode = createResponse.extract().statusCode();
        String result = createResponse.extract().path("message");

        assertThat("Courier created", statusCode, equalTo(SC_CONFLICT));
        assertThat(result, equalTo("Этот логин уже используется"));
    }
}