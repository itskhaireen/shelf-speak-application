package com.example.bookreviewapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import com.example.bookreviewapi.model.User;
import com.example.bookreviewapi.model.UserRole;
import com.example.bookreviewapi.repository.UserRepository;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads_andMockMvcIsInjected() {
        // This test just verifies that the Spring context loads and MockMvc is available
        assertThat(mockMvc).isNotNull();
    }

    // Helper: Register a user and return JWT token
    private String registerAndLoginUser(String username, String email, String password) throws Exception {
        String userJson = String.format("{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", username, email, password);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk());
        String loginJson = String.format("{\"usernameOrEmail\":\"%s\",\"password\":\"%s\"}", username, password);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        return "Bearer " + objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();
    }

    // Helper: Register a user, set role to ADMIN, and return JWT token
    private String registerAndLoginAdmin(String username, String email, String password) throws Exception {
        String token = registerAndLoginUser(username, email, password);
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
        return token;
    }

    @Test
    void createBook_shouldReturnSavedBook() throws Exception {
        String token = registerAndLoginUser("user1", "user1@example.com", "password123");
        String bookJson = """
        {
          \"title\": \"Integration Test Book\",
          \"author\": \"Test Author\",
          \"genre\": \"Test Genre\"
        }
        """;
        mockMvc.perform(post("/api/books")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Integration Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.genre").value("Test Genre"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void createBook_unauthenticated_shouldReturn401() throws Exception {
        String bookJson = """
        {
          \"title\": \"Integration Test Book\",
          \"author\": \"Test Author\",
          \"genre\": \"Test Genre\"
        }
        """;
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBooks_shouldReturnListOfBooks() throws Exception {
        String token = registerAndLoginUser("user2", "user2@example.com", "password123");
        String bookJson1 = """
        {
            \"title\": \"Book 1\",
            \"author\": \"Author 1\",
            \"genre\": \"Genre 1\"
        }
        """;
        String bookJson2 = """
        {
            \"title\": \"Book 2\",
            \"author\": \"Author 2\",
            \"genre\": \"Genre 2\"
        }
        """;
        mockMvc.perform(post("/api/books")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(bookJson1))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/books")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(bookJson2))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].title").value("Book 1"))
            .andExpect(jsonPath("$[0].author").value("Author 1"))
            .andExpect(jsonPath("$[0].genre").value("Genre 1"))
            .andExpect(jsonPath("$[1].title").value("Book 2"))
            .andExpect(jsonPath("$[1].author").value("Author 2"))
            .andExpect(jsonPath("$[1].genre").value("Genre 2"))
            .andExpect(jsonPath("$[0].id").isNumber())
            .andExpect(jsonPath("$[1].id").isNumber());
    }

    @Test
    void getBookById_shouldReturnCorrectBook () throws Exception {
        String token = registerAndLoginUser("user3", "user3@example.com", "password123");
        String bookJson = """
            {
              "title": "Book for ID Test",
              "author": "Author X",
              "genre": "Genre X"
            }
            """;
        MvcResult result = mockMvc.perform(post("/api/books")
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                                .andExpect(status().isOk())
                                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();
        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Book for ID Test"))
                .andExpect(jsonPath("$.author").value("Author X"))
                .andExpect(jsonPath("$.genre").value("Genre X"));
    }

    @Test
    void getBookById_whenBookDoesNotExist_shouldReturn404 () throws Exception {
        Long nonExistentID = 207L;
        mockMvc.perform(get("/api/books/{id}", nonExistentID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with ID: " + nonExistentID));
    }

    @Test
    void deleteBookById_asAdmin_shouldDeleteBook() throws Exception {
        String adminToken = registerAndLoginAdmin("admin1", "admin1@example.com", "password123");
        String bookJson = """
            {
              "title": "Book for ID Test",
              "author": "Author X",
              "genre": "Genre X"
            }
            """;
        MvcResult result = mockMvc.perform(post("/api/books")
                                .header("Authorization", adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                                .andExpect(status().isOk())
                                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();
        mockMvc.perform(delete("/api/books/{id}", id)
                .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with ID: " + id));
    }

    @Test
    void deleteBookById_asUser_shouldReturn403() throws Exception {
        String userToken = registerAndLoginUser("user4", "user4@example.com", "password123");
        String adminToken = registerAndLoginAdmin("admin2", "admin2@example.com", "password123");
        String bookJson = """
            {
              "title": "Book for Forbidden Test",
              "author": "Author Y",
              "genre": "Genre Y"
            }
            """;
        MvcResult result = mockMvc.perform(post("/api/books")
                                .header("Authorization", adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                                .andExpect(status().isOk())
                                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();
        mockMvc.perform(delete("/api/books/{id}", id)
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBookById_unauthenticated_shouldReturn401() throws Exception {
        String adminToken = registerAndLoginAdmin("admin3", "admin3@example.com", "password123");
        String bookJson = """
            {
              "title": "Book for Unauthorized Test",
              "author": "Author Z",
              "genre": "Genre Z"
            }
            """;
        MvcResult result = mockMvc.perform(post("/api/books")
                                .header("Authorization", adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson))
                                .andExpect(status().isOk())
                                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();
        mockMvc.perform(delete("/api/books/{id}", id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAverageRating_shouldReturnCorrectAverage () throws Exception {
        String token = registerAndLoginUser("user5", "user5@example.com", "password123");
        String bookJson = """
            {
                "title": "Book for Rating Test",
                "author": "Rating author",
                "genre": "Rating Genre"
            }
            """;
        MvcResult result = mockMvc.perform(post("/api/books")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bookJson))
                    .andExpect(status().isOk())
                    .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Long bookId = objectMapper.readTree(responseBody).get("id").asLong();
        // Step 2: Add reviews to the book
        String review1Json = """
            {
                "comment": "Great book!",
                "rating": 5
            }
            """;

        String review2Json = """
            {
                "comment": "Kind of a OK book",
                "rating": 3
            }
            """;

        // Add first review
        mockMvc.perform(post("/api/books/{bookId}/reviews", bookId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(review1Json))
                .andExpect(status().isOk());

        // Add second review  
        mockMvc.perform(post("/api/books/{bookId}/reviews", bookId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(review2Json))
                .andExpect(status().isOk());

        // Step 3: Get the average rating and verify it
        mockMvc.perform(get("/api/books/{bookId}/average-rating", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(4.0)); // Expected: (5 + 3) / 2 = 4.0
    }
}