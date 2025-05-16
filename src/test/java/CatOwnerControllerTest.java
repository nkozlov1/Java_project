import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.kozlov.controllers.CatOwnerController;
import ru.kozlov.models.CatOwnerDto;
import ru.kozlov.models.OwnerFilter;
import ru.kozlov.services.CatOwnerService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatOwnerController.class)
@ContextConfiguration(classes = {CatOwnerController.class})
public class CatOwnerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatOwnerService catOwnerService;

    private CatOwnerDto ownerDto;

    @BeforeEach
    void setUp() {
        ownerDto = new CatOwnerDto("Иван Иванов", "1990-05-15");
        ownerDto.setId(1L);
    }

    @Test
    @WithMockUser(username = "user1", password = "user1", roles = "USER")
    void testSave_Authenticated() throws Exception {
        Mockito.when(catOwnerService.save(any(CatOwnerDto.class))).thenReturn(ownerDto);

        mockMvc.perform(post("/owners")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Иван Иванов",
                                    "birthDate": "1990-05-15"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-15"));
    }

    @Test
    void testSave_Unauthenticated() throws Exception {
        mockMvc.perform(post("/owners")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Иван Иванов",
                                    "birthDate": "1990-05-15"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "user1", roles = "USER")
    void testDeleteOwnerById_AsOwner() throws Exception {
        Mockito.when(catOwnerService.isOwner(1L, "user1")).thenReturn(true);

        mockMvc.perform(delete("/owners/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catOwnerService, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void testDeleteOwnerById_AsAdmin() throws Exception {
        mockMvc.perform(delete("/owners/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catOwnerService, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteOwnerById_Unauthenticated() throws Exception {
        mockMvc.perform(delete("/owners/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void testDeleteAll_AsAdmin() throws Exception {
        mockMvc.perform(delete("/owners")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catOwnerService, times(1)).deleteAll();
    }


    @Test
    void testDeleteAll_Unauthenticated() throws Exception {
        mockMvc.perform(delete("/owners")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "user1", roles = "USER")
    void testGetOwnerById_Authenticated() throws Exception {
        Mockito.when(catOwnerService.getById(1L)).thenReturn(ownerDto);

        mockMvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-15"));
    }

    @Test
    void testGetOwnerById_Unauthenticated() throws Exception {
        mockMvc.perform(get("/owners/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "user1", roles = "USER")
    void testGetAllOwners() throws Exception {
        List<CatOwnerDto> mockOwners = List.of(
                new CatOwnerDto("Иван Иванов", "1990-05-15"),
                new CatOwnerDto("Мария Петрова", "1985-10-20")
        );
        mockOwners.get(0).setId(1L);
        mockOwners.get(1).setId(2L);

        Mockito.when(catOwnerService.getAll(any(OwnerFilter.class), eq(PageRequest.of(0, 20)))).thenReturn(mockOwners);

        mockMvc.perform(get("/owners/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockOwners.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Иван Иванов"))
                .andExpect(jsonPath("$[0].birthDate").value("1990-05-15"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Мария Петрова"))
                .andExpect(jsonPath("$[1].birthDate").value("1985-10-20"));
    }

    @Test
    void testGetAllOwners_Unauthenticated() throws Exception {
        mockMvc.perform(get("/owners/getAll"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "user1", roles = "USER")
    void testAddCat_AsOwner() throws Exception {
        Mockito.when(catOwnerService.isOwner(1L, "user1")).thenReturn(true);

        mockMvc.perform(post("/owners/1/cats/2")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catOwnerService, times(1)).addCat(1L, 2L);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void testAddCat_AsAdmin() throws Exception {
        mockMvc.perform(post("/owners/1/cats/2")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(catOwnerService, times(1)).addCat(1L, 2L);
    }

    @Test
    void testAddCat_Unauthenticated() throws Exception {
        mockMvc.perform(post("/owners/1/cats/2")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
