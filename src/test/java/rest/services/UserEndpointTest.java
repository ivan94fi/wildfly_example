package rest.services;

import static domain.User.Role.ADMIN;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static rest.security.SecurityBypass.PASSWORD_ENV_VAR;
import static rest.security.SecurityBypass.TEST_HEADER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response.Status;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import domain.Booking;
import domain.Structure;
import domain.User;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class UserEndpointTest {

    private static ResponseSpecification okJSONResponseSpecification;
    private static EntityManagerFactory emf;
    private static RequestSpecification bypassAuthenticationRequestSpecification;

    private static void clearDatabase() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM User_roles").executeUpdate();
        em.createNativeQuery("DELETE FROM Booking").executeUpdate();
        em.createNativeQuery("DELETE FROM User").executeUpdate();
        em.createNativeQuery("DELETE FROM Structure").executeUpdate();
        em.getTransaction().commit();
        em.clear();
        em.close();
    }

    @BeforeClass
    public static void setupClass() {

        emf = Persistence.createEntityManagerFactory("test-swam-example");

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/swam-example/rest";

        okJSONResponseSpecification = new ResponseSpecBuilder().expectStatusCode(
                200).expectContentType(ContentType.JSON).build();

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(
                System.getenv(PASSWORD_ENV_VAR));
        bypassAuthenticationRequestSpecification = new RequestSpecBuilder().addHeader(
                TEST_HEADER, encryptedPassword).build();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        clearDatabase();
        emf.close();
    }

    @Before
    public void setUp() {
        clearDatabase();
    }

    @Test
    public void testGetAllWhenNoUserShouldReturnEmptyArrayWithOkCode() {
        // @formatter:off
        given()
            .spec(bypassAuthenticationRequestSpecification)
        .when()
            .get("/users")
        .then()
            .spec(okJSONResponseSpecification)
            .body("", empty());
        // @formatter:on
    }

    @Test
    public void testGetAllWhenUsersArePresentShouldReturnArrayWithOkCode() {
        EntityManager em = emf.createEntityManager();
        User user1 = new User("test_user1");
        User user2 = new User("test_user2");
        em.getTransaction().begin();
        em.persist(user1);
        em.persist(user2);
        em.getTransaction().commit();

        // @formatter:off
        given()
            .spec(bypassAuthenticationRequestSpecification)
        .when()
            .get("/users")
        .then()
            .spec(okJSONResponseSpecification)
            .body("size()", is(2))
            .body("username", containsInAnyOrder(user1.getUsername(), user2.getUsername()));
        // @formatter:on
    }

    @Test
    public void testGetAllWhenAuthorizationNotSuppliedShouldReturnUnauthorized() {
        // @formatter:off
        when()
            .get("/users")
        .then()
            .statusCode(Status.UNAUTHORIZED.getStatusCode());
        // @formatter:on
    }

    @Test
    public void testGetAllWhenWrongAuthorizationSuppliedShouldReturnForbidden() {
        EntityManager em = emf.createEntityManager();
        User user1 = new User("not_an_admin");
        em.getTransaction().begin();
        em.persist(user1);
        em.getTransaction().commit();

        // @formatter:off
        given()
            .auth()
            .preemptive()
            .basic("wrong_username", "wrong_password")
        .when()
            .get("/users")
        .then()
            .statusCode(Status.FORBIDDEN.getStatusCode());
        // @formatter:on

        // @formatter:off
        given()
            .auth()
            .preemptive()
            .basic("not_an_admin", "wrong_password")
        .when()
            .get("/users")
        .then()
            .statusCode(Status.FORBIDDEN.getStatusCode());
        // @formatter:on

    }

    @Test
    public void testGetAllWhenCorrectAuthorizationSuppliedShouldReturnOk() {
        EntityManager em = emf.createEntityManager();
        User user1 = new User("admin");
        user1.setRoles(EnumSet.of(ADMIN));
        em.getTransaction().begin();
        em.persist(user1);
        em.getTransaction().commit();

        // @formatter:off
        given()
            .auth()
            .preemptive()
            .basic("admin", "password")
        .when()
            .get("/users")
        .then()
            .statusCode(Status.OK.getStatusCode());
        // @formatter:on
    }

    @Test
    public void testGetSingleUserWhenUserNotFoundShouldReturnNotFound() {
        // @formatter:off
        given()
            .spec(bypassAuthenticationRequestSpecification)
            .pathParam("id", "1")
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(Status.NOT_FOUND.getStatusCode())
            .body(is(emptyOrNullString()));
        // @formatter:on
    }

    @Test
    public void testGetSingleUserWhenUserIsFoundShouldReturnUser() {
        EntityManager em = emf.createEntityManager();
        User user1 = new User("test_user1");
        em.getTransaction().begin();
        em.persist(user1);
        em.getTransaction().commit();

        // @formatter:off
        given()
            .spec(bypassAuthenticationRequestSpecification)
            .pathParam("id", user1.getId().toString())
        .when()
            .get("/users/{id}")
        .then()
            .spec(okJSONResponseSpecification)
            .body("username", equalTo(user1.getUsername()));
        // @formatter:on
    }

    @Test
    public void testGetSingleUserWhenUserHasNoBookingsShouldReturnEmptyBookings() {
        EntityManager em = emf.createEntityManager();
        User user1 = new User("test_user1");
        em.getTransaction().begin();
        em.persist(user1);
        em.getTransaction().commit();

        // @formatter:off
        given()
            .spec(bypassAuthenticationRequestSpecification)
            .pathParam("id", user1.getId().toString())
        .when()
            .get("/users/{id}")
        .then()
            .spec(okJSONResponseSpecification)
            .body("bookings", empty());
        // @formatter:on
    }

    @Test
    public void testGetSingleUserWhenUserHasBookingsShouldReturnAllBookings() {
        EntityManager em = emf.createEntityManager();
        Structure structure1 = new Structure("Hotel Stella Alpina",
                "Via Trento 1");
        Structure structure2 = new Structure("Hotel Bellosguardo",
                "Via Case Sparse 42");

        User user1 = new User("test_user1");
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = new Booking(now.minusDays(5), now.plusDays(1),
                now.plusDays(8), structure1);
        Booking booking2 = new Booking(now.minusDays(1), now.plusDays(15),
                now.plusDays(25), structure2);
        user1.setBookings(Arrays.asList(booking1, booking2));
        em.getTransaction().begin();
        em.persist(structure1);
        em.persist(structure2);
        em.persist(user1);
        em.getTransaction().commit();

        // @formatter:off
        given()
            .spec(bypassAuthenticationRequestSpecification)
            .pathParam("id", user1.getId().toString())
        .when()
            .get("/users/{id}")
        .then()
            .spec(okJSONResponseSpecification)
            .body("username", equalTo(user1.getUsername()))
            .body("bookings.creationDate", containsInAnyOrder(
                   booking1.getCreationDate()
                       .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                   booking2.getCreationDate()
                       .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            )
            .body("bookings.id", containsInAnyOrder(
                   booking1.getId().intValue(),
                   booking2.getId().intValue())
            );
        // @formatter:on
    }

    @Test
    public void testGetSingleUserWhenAuthorizationNotSuppliedShouldReturnUnauthorized() {
        EntityManager em = emf.createEntityManager();
        User user1 = new User("test_user1");
        em.getTransaction().begin();
        em.persist(user1);
        em.getTransaction().commit();

        // @formatter:off
        given()
            .pathParam("id", user1.getId().toString())
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(Status.UNAUTHORIZED.getStatusCode());
        // @formatter:on
    }

}
