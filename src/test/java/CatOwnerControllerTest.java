import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.kozlov.controllers.CatOwnerController;
import ru.kozlov.models.*;
import ru.kozlov.services.CatOwnerService;

import java.util.List;

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

    @Test
    void testGetAllOwners() throws Exception {
        List<CatOwnerDto> mockOwners = List.of(
                new CatOwnerDto("Nana", "2020-01-01"),
                new CatOwnerDto("Lala", "2019-05-10")
        );

        Mockito.when(catOwnerService.getAll(new OwnerFilter(null, null), PageRequest.of(0, 20))).thenReturn(mockOwners);

        mockMvc.perform(get("/owners/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockOwners.size()))
                .andExpect(jsonPath("$[0].name").value("Nana"))
                .andExpect(jsonPath("$[0].birthDate").value("2020-01-01"))
                .andExpect(jsonPath("$[1].name").value("Lala"))
                .andExpect(jsonPath("$[1].birthDate").value("2019-05-10"));
    }

    @Test
    void testCreateOwner() throws Exception {
        CatOwnerDto owner = new CatOwnerDto("Барсик", "2020-01-01");
        Mockito.when(catOwnerService.save(owner)).thenReturn(owner);

        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Барсик",
                                    "birthDate": "2020-01-01"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Барсик"))
                .andExpect(jsonPath("$.birthDate").value("2020-01-01"));
    }

    @Test
    void testGetOwnerById() throws Exception {
        CatOwnerDto owner = new CatOwnerDto("Барсик", "2020-01-01");
        Mockito.when(catOwnerService.getById(1L)).thenReturn(owner);

        mockMvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Барсик"))
                .andExpect(jsonPath("$.birthDate").value("2020-01-01"));
    }

    @Test
    void testDeleteById() throws Exception {
        mockMvc.perform(delete("/owners/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAll() throws Exception {
        mockMvc.perform(delete("/owners"))
                .andExpect(status().isOk());
    }


    @Test
    void testAddFriend() throws Exception {
        mockMvc.perform(post("/owners/1/cats/2"))
                .andExpect(status().isOk());
    }
}
