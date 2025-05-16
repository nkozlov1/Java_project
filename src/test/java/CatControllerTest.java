import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.kozlov.controllers.CatController;
import ru.kozlov.models.CatDto;
import ru.kozlov.models.ColorsDto;
import ru.kozlov.services.CatService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatController.class)
@ContextConfiguration(classes = {CatController.class})
public class CatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatService catService;

    private CatDto catDto;

    @BeforeEach
    void setUp() {
        catDto = new CatDto("Барсик", "2020-01-01", "Siam", ColorsDto.Black);
        catDto.setId(1L);
    }


    @Test
    @WithMockUser(username = "user1", password = "user1", roles = "USER")
    void testCreateCat_Authenticated() throws Exception {
        Mockito.when(catService.save(Mockito.any(CatDto.class))).thenReturn(catDto);

        mockMvc.perform(post("/cats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Барсик",
                                    "birthDate": "2020-01-01",
                                    "breed": "Siam",
                                    "color": "Black"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Барсик"))
                .andExpect(jsonPath("$.birthDate").value("2020-01-01"))
                .andExpect(jsonPath("$.breed").value("Siam"))
                .andExpect(jsonPath("$.color").value("Black"));
    }

    @Test
    void testCreateCat_Unauthenticated() throws Exception {
        Mockito.when(catService.save(catDto)).thenReturn(catDto);

        mockMvc.perform(post("/cats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                    "name": "Барсик",
                                    "birthDate": "2020-01-01",
                                    "breed": "Siam",
                                    "color": "Black"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "user1", roles = "USER")
    void testDeleteById_AsOwner() throws Exception {
        Mockito.when(catService.isCatOwner(1L, "user1")).thenReturn(true);

        mockMvc.perform(delete("/cats/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catService, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void testDeleteById_AsAdmin() throws Exception {
        mockMvc.perform(delete("/cats/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catService, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_Unauthenticated() throws Exception {
        mockMvc.perform(delete("/cats/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void testDeleteAll_AsAdmin() throws Exception {
        mockMvc.perform(delete("/cats")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catService, times(1)).deleteAll();
    }


    @Test
    void testDeleteAll_Unauthenticated() throws Exception {
        mockMvc.perform(delete("/cats")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "user1", roles = "USER")
    void testGetAllCatsById_Authenticated() throws Exception {
        Mockito.when(catService.getAllCatsById(1L)).thenReturn(List.of(catDto));

        mockMvc.perform(get("/cats/1/all-cats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Барсик"))
                .andExpect(jsonPath("$[0].birthDate").value("2020-01-01"))
                .andExpect(jsonPath("$[0].breed").value("Siam"))
                .andExpect(jsonPath("$[0].color").value("Black"));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void testAddFriend_AsAdmin() throws Exception {
        mockMvc.perform(post("/cats/1/friends/2")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catService, times(1)).addFriend(1L, 2L);
    }

    @Test
    void testAddFriend_Unauthenticated() throws Exception {
        mockMvc.perform(post("/cats/1/friends/2")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
