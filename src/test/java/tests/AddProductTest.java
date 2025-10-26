package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AddProductTest {
    @Test
    public void testAddProductSuccessfully() {
        baseURI = "http://127.0.0.1:5000/api";

        String payload = """
            {
              "name": "Mechanical Keyboard X",
              "price": 75.50,
              "stock": 20
            }
        """;

        Response res = given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("message", equalTo("Product added successfully"))
                .body("id", notNullValue())
                .extract().response();

        int newProductId = res.jsonPath().getInt("id");
        Assert.assertTrue(newProductId > 0, "Product ID should be positive");

        // Verify product is in list
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("id", hasItem(newProductId));
    }
}
