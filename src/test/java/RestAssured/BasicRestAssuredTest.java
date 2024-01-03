package RestAssured;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BasicRestAssuredTest {

    private static final RequestSpecification reqSpec = new RequestSpecBuilder()
            .setBaseUri("https://api.openweathermap.org")
            .addQueryParam("appid", "083bc49f3290e6340bcec5b79d3f1193")
            .build();

    private static final ResponseSpecification resSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .build();

    @ParameterizedTest
    @CsvSource({
            "London, uk, 2643743",
            "Oxford, uk, 2640729",
            "Gdańsk, pl, 3099434"
    })
    public void shouldGETWeatherForLocation(String city, String country, String id) {
        given().spec(reqSpec)
                .queryParam("id", id)
                .log().all()
                .when()
                .get("/data/2.5/forecast")
                .then()
                .spec(resSpec)
                .log().body()
                .body("city.name", equalTo(city));
    }
}
