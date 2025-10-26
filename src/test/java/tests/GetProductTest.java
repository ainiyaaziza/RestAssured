package tests;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GetProductTest {
    @Test
    public void testGetAllProducts() {
        baseURI = "http://127.0.0.1:5000/api";

        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }
}
