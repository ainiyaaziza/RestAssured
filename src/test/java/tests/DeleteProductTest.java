package tests;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class DeleteProductTest {
    private static String authToken;
    private static int productId;

    @BeforeClass
    public void setupAndCreateProduct() {
        baseURI = "http://127.0.0.1:5000/api";

        // Login to get token
        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .body("{\"username\":\"qa_user\",\"password\":\"password123\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().response();

        authToken = loginResponse.jsonPath().getString("token");

        // Create a new product for deletion
        String payload = """
            {
              "name": "Temporary Product",
              "price": 49.99,
              "stock": 8
            }
        """;

        Response res = given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract().response();

        productId = res.jsonPath().getInt("id");
    }

    @Test
    public void testDeleteProductWithAuth() {
        given()
                .header("Authorization", authToken)
                .when()
                .delete("/products/" + productId)
                .then()
                .statusCode(204);

        // Verify product no longer exists
        given()
                .when()
                .get("/products/" + productId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteWithoutTokenShouldFail() {
        // Create another product
        String payload = """
            {
              "name": "Unauthorized Product",
              "price": 10.0,
              "stock": 3
            }
        """;

        Response res = given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract().response();

        int newId = res.jsonPath().getInt("id");

        // Try to delete without token
        given()
                .when()
                .delete("/products/" + newId)
                .then()
                .statusCode(403)
                .body("message", containsString("Unauthorized"));
    }
}
