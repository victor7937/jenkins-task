package com.epam.esm;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.criteria.CertificateCriteria;
import com.epam.esm.dto.CertificateDTO;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.IncorrectDataServiceException;
import com.epam.esm.exception.NotFoundServiceException;
import com.epam.esm.hateoas.assembler.GiftCertificateAssembler;
import com.epam.esm.hateoas.assembler.OrderAssembler;
import com.epam.esm.security.SecurityConfig;
import com.epam.esm.security.jwt.JwtTokenFilter;
import com.epam.esm.security.provider.AuthenticationAndTokenProvider;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.hateoas.PagedModel;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GiftCertificateController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtTokenFilter.class}))
class CertificateControllerTests {

    @TestConfiguration
    static class GiftCertificateControllerConfiguration {

        @Bean
        public GiftCertificateAssembler giftCertificateAssembler() {
            return new GiftCertificateAssembler(new ModelMapper());
        }

        @Bean
        public OrderAssembler orderAssembler() {
            return new OrderAssembler(new ModelMapper());
        }
    }


    @Autowired
    private MockMvc mvc;

    @MockBean
    private GiftCertificateService certificateService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private AuthenticationAndTokenProvider authAndTokenProvider;

    private JacksonTester<CertificateDTO> jackson;

    final GiftCertificate certificateSample2 = new GiftCertificate(1L,"name","test GiftCertificate",23.5f,2, LocalDateTime.now(), LocalDateTime.now());
    final List<GiftCertificate> certificateListSample = List.of(
            new GiftCertificate("name1","test1",1.1f,1),
            new GiftCertificate("name2","test2",1.2f,2),
            new GiftCertificate("name3","test3",1.3f,3));
    final CertificateDTO certificateDTOSample = new CertificateDTO(certificateSample2.getName(),certificateSample2.getDescription(), certificateSample2.getPrice(), certificateSample2.getDuration(), certificateSample2.getTags());

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    void correctGettingByIdShouldBeOk() throws Exception {
        GiftCertificate expected = certificateSample2;
        expected.setDeleted(false);
        when(certificateService.getById(1L)).thenReturn(expected);

        mvc.perform(get("/certificates/{id}",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void gettingWithIncorrectIdShouldBeNotFound() throws Exception {
        when(certificateService.getById(anyLong())).thenThrow(NotFoundServiceException.class);

        mvc.perform(get("/certificates/{id}",99))
                .andExpect(status().isNotFound());
    }

    @Test
    void gettingWithIncorrectParamShouldBeBadRequest() throws Exception {
        when(certificateService.getById(anyLong())).thenThrow(IncorrectDataServiceException.class);

        mvc.perform(get("/certificates/{id}",-1))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/certificates/dfde"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void correctGettingAllShouldBeOk() throws Exception {
        int pageSize = 5;
        int pageNumber = 1;
        PagedDTO<GiftCertificate> expectedDto = new PagedDTO<>(certificateListSample, new PagedModel.PageMetadata(pageSize, pageNumber, certificateListSample.size()));
        when(certificateService.get(any(CertificateCriteria.class), anyInt(), anyInt())).thenReturn(expectedDto);

        mvc.perform(get("/certificates").param("size","" + pageSize).param("page", "" + pageNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.certificates.length()").value(certificateListSample.size()));

    }

    @Test
    void GettingEmptyResultShouldBeNoContent() throws Exception {
        PagedDTO<GiftCertificate> emptyDto = new PagedDTO<>();
        when(certificateService.get(any(CertificateCriteria.class), anyInt(), anyInt())).thenReturn(emptyDto);

        mvc.perform(get("/certificates")).andExpect(status().isNoContent());
    }


    @Test
    void correctDeletingShouldBeOk() throws Exception {
        doNothing().when(certificateService).delete(anyLong());

        mvc.perform(delete("/certificates/{id}",1)).andExpect(status().isOk());
    }

    @Test
    void deletingNotExistedIdShouldBeNotFound() throws Exception {
        doThrow(NotFoundServiceException.class).when(certificateService).delete(anyLong());

        mvc.perform(delete("/certificates/{id}",99))
                .andExpect(status().isNotFound());
    }

    @Test
    void addingWithCorrectDataShouldBeOk() throws Exception {
        GiftCertificate expected = certificateSample2;
        when(certificateService.add(any(CertificateDTO.class))).thenReturn(expected);

        mvc.perform(post("/certificates").contentType(MediaType.APPLICATION_JSON)
                .content(jackson.write(certificateDTOSample).getJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.price").value(expected.getPrice()))
                .andExpect(jsonPath("$.createDate").exists());
    }

    @Test
    void addingWithIncorrectDataShouldBeBadRequest() throws Exception {
        when(certificateService.add(any(CertificateDTO.class))).thenThrow(IncorrectDataServiceException.class);
        CertificateDTO certificateDTO = certificateDTOSample;
        certificateDTO.setName(null);

        mvc.perform(post("/certificates").contentType(MediaType.APPLICATION_JSON)
                        .content(jackson.write(certificateDTO).getJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatingWithCorrectDataShouldBeOk() throws Exception {
        GiftCertificate current = certificateSample2;
        current.setDeleted(false);

        when(certificateService.getById(current.getId())).thenReturn(current);
        String modifiedName = "Patched name";
        CertificateDTO modified = certificateDTOSample;
        modified.setName(modifiedName);
        ModelMapper modelMapper = new ModelMapper();
        GiftCertificate expected = new GiftCertificate();
        modelMapper.map(current, expected);
        expected.setName(modifiedName);
        when(certificateService.update(modified
                , current.getId())).thenReturn(expected);


        mvc.perform(patch("/certificates/{id}", 1).contentType("application/json-patch+json")
                        .content("[{\"op\":\"replace\", \"path\":\"/name\", \"value\":\"Patched name\"}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()));
    }

    @Test
    void updatingWithIncorrectParamsShouldBeBadRequest() throws Exception {
        mvc.perform(patch("/certificates/{id}", 1).contentType("application/json-patch+json")
                .content("[{\"op\":\"repace\", \"path\":\"/name\", \"value\":\"Patched name\"}]"))  //Here is 'repace' instead of 'replace'
                .andExpect(status().isBadRequest());
    }

    @Test
    void failTest(){
        fail();
    }



}
