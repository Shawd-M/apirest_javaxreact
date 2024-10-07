package com.quest.etna;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quest.etna.config.token.JwtTokenUtil;
import com.quest.etna.model.User;
import org.junit.jupiter.api.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = Logger.getLogger(ControllerTests.class.getName());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WebApplicationContext context;

    private String validJwtToken;

    private String validJwtTokenAdmin;

    private String testValid;

    private String testValid2;

    //Je vais créer un Account pour tout les tests , un account admin et un account qui va être supprimé

    String validJwtTokenAdmin2;

    String validJwtTokenUser2;

    String accountUser = "accountuser";
    String accountDelete = "/user/1";
    String addrDelete = "/address/1";

    User userTest = new User("user", "test");
    User userToDelete = new User("userToDel", "test");

    User AccountTest = new User(accountUser, "pwd");

    private String convertToJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @BeforeEach
    public void setup() {
        validJwtTokenAdmin = jwtTokenUtil.generateToken("testadmin");
        validJwtToken = jwtTokenUtil.generateToken(accountUser);
        validJwtTokenAdmin2 = jwtTokenUtil.generateToken("testadmin2");
        validJwtTokenUser2 = jwtTokenUtil.generateToken("user2");

        testValid = validJwtToken;
        testValid2 = validJwtTokenAdmin;


        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    void contextLoads() {
        jdbcTemplate.execute("DELETE FROM address, user USING address INNER JOIN user WHERE address.user_id = user.id");
        jdbcTemplate.execute("DELETE FROM user");
        jdbcTemplate.execute("ALTER TABLE user AUTO_INCREMENT = 1");
        jdbcTemplate.execute("ALTER TABLE address AUTO_INCREMENT = 1");
        jdbcTemplate.execute("INSERT INTO user (username, password) VALUES ('userToDel', 'test')");

    }


    @Test
    @Order(1)
    public void testAuthenticate() throws Exception {
        contextLoads();
        String jsonUser = convertToJson(AccountTest);
        int expectedStatusRegisterCreated = 201;

        logger.info("Test /register avec création");
        MvcResult resultCreated = mockMvc.perform(MockMvcRequestBuilders
                        .post("/register")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(accountUser))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        int statusCreated = resultCreated.getResponse().getStatus();
        int actualStatusCreated = resultCreated.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour Created : " + statusCreated);
        assertEquals(expectedStatusRegisterCreated, actualStatusCreated, "Le statut HTTP attendu est 201 mais a été " + actualStatusCreated);

        logger.info("Test /register avec duplication");
        MvcResult resultDup = mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("L'utilisateur existe déjà."))
                .andReturn();
        int statusDup = resultDup.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour Duplicate: " + statusDup);

        logger.info("Testing /authenticate pour l'auth");
        MvcResult resultAuth = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();
        int statusAuth = resultAuth.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour Auth: " + statusAuth);

        logger.info("Testing /me pour l'user détail");
        MvcResult resultMe = mockMvc.perform(MockMvcRequestBuilders.get("/me")
                        .header("Authorization", "Bearer " + testValid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(accountUser))
                .andReturn();
        int statusMe = resultMe.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour /me: " + statusMe);
    }

    @WithMockUser(username="testadmin", roles = {"ADMIN"}) // Adjust roles accordingly
    @Test
    @Order(2)
    public void testUser() throws Exception {
        User admin = new User("testadmin", "pwd");
        String jsonAdmin = convertToJson(admin);

        logger.info("Test /register avec création admin");
        MvcResult resultCreated = mockMvc.perform(MockMvcRequestBuilders
                        .post("/register")
                        .content("{\"username\":\"testadmin\",\"password\":\"pwd\",\"role\":\"ROLE_ADMIN\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        //Nouvel auth pour test
        logger.info("Testing /authenticate pour l'auth en mode admin");
        MvcResult resultAuth = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .content(jsonAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();
        int statusAuth = resultAuth.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour Auth ADMIN: " + statusAuth);



        logger.info("Testing /user sans token");
        MvcResult resultUserWO = mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.unwantedKey").doesNotExist())
                .andReturn();
        int statusUserWO = resultUserWO.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour /user sans token: " + statusUserWO);

        logger.info("Testing /user avec token");
        MvcResult resultUserW = mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .header("Authorization", "Bearer " + testValid))
                .andExpect(status().isOk())
                .andReturn();
        int statusUserW = resultUserW.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour /user avec token: " + statusUserW);


        logger.info("Testing delete user avec compte user ");
        MvcResult resultDeleteWithUser = mockMvc.perform(MockMvcRequestBuilders.delete(accountDelete)
                        .header("Authorization", "Bearer " + testValid))
                .andExpect(status().isForbidden())
                .andReturn();
        int statusDeleteWithUser = resultDeleteWithUser.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour /user avec token et compte user : " + statusDeleteWithUser);

        logger.info("Testing delete user avec compte admin ");
        MvcResult resultDeleteWithAdmin = mockMvc.perform(MockMvcRequestBuilders.delete(accountDelete)
                        .header("Authorization", "Bearer " + testValid2))
                .andExpect(status().isOk())
                .andReturn();
        int statusDeleteWithAdmin = resultDeleteWithAdmin.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour /user avec token et compte admin : " + statusDeleteWithAdmin);

    }

   @Order(3)
    @Test
    public void testAddress() throws Exception {

       User admin = new User("testadmin2", "pwd");
       String jsonAdmin = convertToJson(admin);
       User user2 = new User("user2", "pwd");
       String jsonUser2 = convertToJson(user2);

       logger.info("Test /register avec création");
       MvcResult resultCreated = mockMvc.perform(MockMvcRequestBuilders
                       .post("/register")
                       .content("{\"username\":\"testadmin2\",\"password\":\"pwd\",\"role\":\"ROLE_ADMIN\"}")
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
               .andExpect(jsonPath("$.password").doesNotExist())
               .andReturn();

       //Nouvel auth pour test
       logger.info("Testing /authenticate pour l'auth en mode admin");
       MvcResult resultAuth = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                       .content(jsonAdmin)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").exists())
               .andReturn();
       int statusAuth = resultAuth.getResponse().getStatus();
       logger.info("Code d'état réellement retourné pour Auth ADMIN: " + statusAuth);


       logger.info("Test /register avec création");
       MvcResult resultCreated2 = mockMvc.perform(MockMvcRequestBuilders
                       .post("/register")
                       .content(jsonUser2)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.role").value("ROLE_USER"))
               .andExpect(jsonPath("$.password").doesNotExist())
               .andReturn();

       logger.info("Testing /authenticate pour l'auth en mode admin");
       MvcResult resultAuth2 = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                       .content(jsonUser2)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").exists())
               .andReturn();
       int statusAuth2 = resultAuth.getResponse().getStatus();
       logger.info("Code d'état réellement retourné pour Auth ADMIN: " + statusAuth);

       logger.info("Test / avec création adresse");
       MvcResult resultCreatedAddr = mockMvc.perform(MockMvcRequestBuilders
                       .post("/address")
                       .header("Authorization", "Bearer " + validJwtTokenAdmin2)
                       .content("{\"street\":\"23 Rue Enghien les bains\",\"postalCode\":\"77000\",\"city\":\"Meaux\",\"country\":\"France\"}")
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.street").value("23 Rue Enghien les bains"))
               .andExpect(jsonPath("$.postalCode").value("77000"))
               .andExpect(jsonPath("$.city").value("Meaux"))
               .andExpect(jsonPath("$.country").value("France"))
               .andReturn();
       int statusCreatedAddr = resultCreatedAddr.getResponse().getStatus();
       logger.info("Code d'état réellement retourné pour Created : " + statusCreatedAddr);



        logger.info("Testing get adress sans token ");
        MvcResult resultGetAddrFail =  mockMvc.perform(MockMvcRequestBuilders.get("/address"))
                .andExpect(status().isUnauthorized())
                .andReturn();
        int statusGetAddrFail = resultGetAddrFail.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour l'echec de address : " + statusGetAddrFail);

        logger.info("Testing get adress  avec token");
        MvcResult resultGetAddrOk =  mockMvc.perform(MockMvcRequestBuilders.get("/address")
                        .header("Authorization", "Bearer " + validJwtTokenAdmin2))
                .andExpect(status().isOk())
                .andReturn();
        int statusGetAddrOk = resultGetAddrOk.getResponse().getStatus();
        logger.info("Code d'état réellement retourné get address : " + statusGetAddrOk);


       logger.info("Testing delete user with account user");
        MvcResult resultDeleteAddrFail = mockMvc.perform(MockMvcRequestBuilders.delete(addrDelete)
                        .header("Authorization", "Bearer " + validJwtTokenUser2))
                .andExpect(status().isForbidden())
                .andReturn();
        int statusDeleteAddrFail = resultDeleteAddrFail.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour l'échec de suppression avec compte user " + statusDeleteAddrFail);


        logger.info("Testing delete user with account admin");
        MvcResult resultDeleteAddrOk = mockMvc.perform(MockMvcRequestBuilders.delete(addrDelete)
                        .header("Authorization", "Bearer " + validJwtTokenAdmin2))
                .andExpect(status().isOk())
                .andReturn();
        int statusDeleteAddrOk = resultDeleteAddrOk.getResponse().getStatus();
        logger.info("Code d'état réellement retourné pour l'échec de suppression avec compte user " + statusDeleteAddrOk);


    }


}

