import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class ScooterCreateOrderTest{

    String[] color;
    OrderClient orderClient;
    Order order;
    int trackNumber;

    public ScooterCreateOrderTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColorData() {
        return new Object[][]{
                {new String[]{"Black"}},
                {new String[]{"Grey"}},
                {new String[]{"Black", "Grey"}},
                {new String[]{"", ""}},
        };
    }

    @Before
    public void setUp() {
        order = Order.getDefault();
        orderClient = new OrderClient();
    }

    @After
    public void tearDown(){
        orderClient.delete(trackNumber);
    }

    @Test
    public void orderCanBeCreated(){
        order.setColor(color);
        ValidatableResponse createResponse = orderClient.create(order);
        int statusCode = createResponse.extract().statusCode();
        int trackNumber = createResponse.extract().path("track");

        assertThat("Can't create order", statusCode, equalTo(SC_CREATED));
        assertThat("Empty number of order", trackNumber, notNullValue());
    }
}