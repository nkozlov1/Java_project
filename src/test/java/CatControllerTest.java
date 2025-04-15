import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.kozlov.controllers.CatController;
import ru.kozlov.models.CatDto;
import ru.kozlov.models.CatFilter;
import ru.kozlov.models.ColorsDto;
import ru.kozlov.services.CatService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatController.class)
@ContextConfiguration(classes = {CatController.class})
public class CatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatService catService;

    @Test
    void testGetAllCats() throws Exception {
        List<CatDto> mockCats = List.of(
                new CatDto("Барсик", "2020-01-01", "Siam", ColorsDto.Black),
                new CatDto("Мурка", "2019-05-10", "Main-kyn", ColorsDto.Grey)
        );

        Mockito.when(catService.getAll(new CatFilter(null, null, null, null), PageRequest.of(0, 20))).thenReturn(mockCats);

        mockMvc.perform(get("/cats/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockCats.size()))
                .andExpect(jsonPath("$[0].name").value("Барсик"))
                .andExpect(jsonPath("$[0].birthDate").value("2020-01-01"))
                .andExpect(jsonPath("$[0].breed").value("Siam"))
                .andExpect(jsonPath("$[0].color").value("Black"))
                .andExpect(jsonPath("$[1].name").value("Мурка"))
                .andExpect(jsonPath("$[1].birthDate").value("2019-05-10"))
                .andExpect(jsonPath("$[1].breed").value("Main-kyn"))
                .andExpect(jsonPath("$[1].color").value("Grey"));
    }

    @Test
    void testCreateCat() throws Exception {
        CatDto cat = new CatDto("Барсик", "2020-01-01", "Siam", ColorsDto.Black);
        Mockito.when(catService.save(cat)).thenReturn(cat);

        mockMvc.perform(post("/cats")
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
                .andExpect(jsonPath("$.color").value("Black"))
        ;
    }

    @Test
    void testGetCatById() throws Exception {
        CatDto cat = new CatDto("Барсик", "2020-01-01", "Siam", ColorsDto.Black);
        Mockito.when(catService.getById(1L)).thenReturn(cat);

        mockMvc.perform(get("/cats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Барсик"))
                .andExpect(jsonPath("$.birthDate").value("2020-01-01"))
                .andExpect(jsonPath("$.breed").value("Siam"))
                .andExpect(jsonPath("$.color").value("Black"))
        ;
    }

    @Test
    void testDeleteById() throws Exception {
        mockMvc.perform(delete("/cats/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAll() throws Exception {
        mockMvc.perform(delete("/cats"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllFriends() throws Exception {
        List<CatDto> mockCats = List.of(
                new CatDto("Барсик", "2020-01-01", "Siam", ColorsDto.Black)
        );
        Mockito.when(catService.getAllFriends(1L)).thenReturn(mockCats);

        mockMvc.perform(get("/cats/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(mockCats.size()))
                .andExpect(jsonPath("$[0].name").value("Барсик"))
                .andExpect(jsonPath("$[0].birthDate").value("2020-01-01"))
                .andExpect(jsonPath("$[0].breed").value("Siam"))
                .andExpect(jsonPath("$[0].color").value("Black"));
    }

    @Test
    void testAddCat() throws Exception {
        mockMvc.perform(post("/cats/1/friends/2"))
                .andExpect(status().isOk());
    }
}
