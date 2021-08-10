package com.epam.esm;

import com.epam.esm.controller.TagController;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.dto.TagDTO;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.IncorrectDataServiceException;
import com.epam.esm.exception.NotFoundServiceException;
import com.epam.esm.security.SecurityConfig;
import com.epam.esm.security.jwt.JwtTokenFilter;
import com.epam.esm.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TagController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtTokenFilter.class}))
class TagControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TagService tagService;

    private JacksonTester<TagDTO> jackson;

    private final Tag tagSample1 = new Tag(1L, "name");

    private final List<Tag> tagListSample = List.of(new Tag(1L,"name1"), new Tag(2L,"name2"), new Tag(3L,"name3"));

    private final TagDTO tagDTOSample = new TagDTO("name");

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    void correctGettingTagByIdShouldBeOk() throws Exception {
        when(tagService.getById(1L)).thenReturn(tagSample1);

        mvc.perform(get("/tags/{id}",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void gettingWithIncorrectIdShouldBeNotFound() throws Exception {
        when(tagService.getById(anyLong())).thenThrow(NotFoundServiceException.class);

        mvc.perform(get("/tags/{id}",99))
                .andExpect(status().isNotFound());
    }

    @Test
    void correctGettingAllShouldBeOk() throws Exception {
        int pageSize = 5;
        int pageNumber = 1;
        PagedDTO<Tag> expectedDto = new PagedDTO<>(tagListSample, new PagedModel.PageMetadata(pageSize, pageNumber, tagListSample.size()));
        when(tagService.get(anyString(), anyInt(), anyInt())).thenReturn(expectedDto);

        mvc.perform(get("/tags").param("size","" + pageSize).param("page", "" + pageNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tagList.length()").value(tagListSample.size()));

    }

    @Test
    void GettingEmptyResultShouldBeNoContent() throws Exception {
        PagedDTO<Tag> emptyDto = new PagedDTO<>();
        when(tagService.get(anyString(), anyInt(), anyInt())).thenReturn(emptyDto);

        mvc.perform(get("/tags")).andExpect(status().isNoContent());
    }

    @Test
    void correctDeletingShouldBeOk() throws Exception {
        doNothing().when(tagService).delete(anyLong());

        mvc.perform(delete("/tags/{id}",1)).andExpect(status().isOk());
    }

    @Test
    void deletingNotExistedIdShouldRaiseException() throws Exception {
        doThrow(NotFoundServiceException.class).when(tagService).delete(anyLong());

        mvc.perform(delete("/tags/{id}",99))
                .andExpect(status().isNotFound());
    }

    @Test
    void addingWithCorrectDataShouldBeOk() throws Exception {
        Tag expected = tagSample1;
        when(tagService.add(any(TagDTO.class))).thenReturn(expected);

        mvc.perform(post("/tags").contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.write(tagDTOSample).getJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()));
    }

    @Test
    void addingWithIncorrectDataShouldBeBadRequest() throws Exception {
        when(tagService.add(any(TagDTO.class))).thenThrow(IncorrectDataServiceException.class);

        mvc.perform(post("/tags").contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.write(tagDTOSample).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void gettingMostUserShouldBeOk() throws Exception {
        Tag expected = tagSample1;
        when(tagService.getMostUsedTagOfValuableCustomer()).thenReturn(expected);

        mvc.perform(get("/tags/most-used-tag").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()));
    }

}
