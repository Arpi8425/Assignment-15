package com.example.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    // Public page test
    @Test
    public void testPublicPagesAccessible() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk());
    }

    // Unauthenticated access should redirect
    @Test
    public void testBooksRequiresLogin() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection());
    }

    // Student access test
    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    public void testStudentCanViewBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    // Guest forbidden test
    @Test
    @WithMockUser(username = "guest", roles = {"GUEST"})
    public void testGuestCannotViewBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isForbidden());
    }

    // Admin access test
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdminCanAccessReports() throws Exception {
        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk());
    }
}
