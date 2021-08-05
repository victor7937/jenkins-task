package com.epam.esm;

import com.epam.esm.criteria.CertificateCriteria;
import com.epam.esm.criteria.UserCriteria;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.dto.UserDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.User;
import com.epam.esm.exception.*;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.UserService;
import com.epam.esm.service.impl.UserServiceImpl;
import com.epam.esm.validator.impl.UserCriteriaValidator;
import com.epam.esm.validator.impl.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository repository;

    UserService service;

    final User userSample = new User("someuser@gmail.com","Sergey", "Smith");
    final List<User> userListSample = List.of(new User("someuser@gmail.com","Sergey", "Smith"),
            new User("someuser2@gmail.com","Sergey2", "Smith2"),
            new User("someuser3@gmail.com","Sergey3", "Smith3"));
    final PagedDTO<User> userPagedDtoSample = new PagedDTO<>(userListSample, new PagedModel.PageMetadata(1,1,1));
    final UserCriteria criteriaSample = UserCriteria.createCriteria(new HashMap<>());
    final Long idSample = 1L;

    @BeforeEach
    void init(){
        service = new UserServiceImpl(repository, new UserCriteriaValidator(), new UserValidator());
    }

    @Nested
    class GetUsersTests{

        @Test
        void gettingWithIncorrectPageParamsShouldRaiseException(){
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class, () -> service.get(criteriaSample,-1,2)),
                    () -> assertThrows(IncorrectDataServiceException.class, () -> service.get(criteriaSample,2,-1)),
                    () -> assertThrows(IncorrectDataServiceException.class, () -> service.get(criteriaSample,0,0))
            );
            verify(repository, never()).findAll(any(Example.class), any(Pageable.class));
        }

        @Test
        void requestForNonExistentPageShouldReturnEmptyPagedDTO(){
            when(repository.findAll(any(Example.class), any(Pageable.class))).thenReturn(Page.empty());
            assertTrue(service.get(criteriaSample,1,999999).isEmpty());
            verify(repository).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class GettingByEmailTests {

        @Test
        void correctGettingByEmailShouldReturnUser() {
            User expected = userSample;
            when(repository.findByEmail(expected.getEmail())).thenReturn(Optional.of(expected));
            assertEquals(expected, service.getByEmail(expected.getEmail()));
            verify(repository).findByEmail(anyString());
        }

        @Test
        void gettingWithNotExistentEmailShouldRaiseException() {
            String notExistentEmail = "some_email@some.com";
            when(repository.findByEmail(notExistentEmail)).thenReturn(Optional.empty());
            assertThrows(NotFoundServiceException.class,() -> service.getByEmail(notExistentEmail));
            verify(repository).findByEmail(anyString());
        }

        @Test
        void gettingWithIncorrectEmailShouldRaiseException() {
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getByEmail(null)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getByEmail("")),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getByEmail("   ")),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getByEmail("someString")),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getByEmail("somnotemail111@ffffff.f.f.ff"))
            );
            verify(repository, never()).findByEmail(anyString());
        }
   }

    @Nested
    class GettingByIdTests {

        @Test
        void correctGettingByIdShouldReturnUser(){
            User expected = userSample;
            when(repository.findById(idSample)).thenReturn(Optional.of(expected));
            assertEquals(expected, service.getById(idSample));
            verify(repository).findById(anyLong());
        }

        @Test
        void gettingWithNotExistentIdShouldRaiseException() {
            Long notExistentId = 99999L;
            when(repository.findById(notExistentId)).thenReturn(Optional.empty());
            assertThrows(NotFoundServiceException.class,() -> service.getById(notExistentId));
            verify(repository).findById(anyLong());
        }

        @Test
        void gettingWithIncorrectIdShouldRaiseException() {
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getById(null)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getById(-1L)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.getById(0L))
            );
            verify(repository, never()).findById(anyLong());
        }
    }

    @Nested
    class RegistrationTest{

        final String passwordSample = "strong_password";
        final String emailSample = "good_email@email.com";
        final UserDTO userDTO = new UserDTO(emailSample, passwordSample ,userSample.getFirstName(), userSample.getLastName());


        @Test
        void correctRegistrationShouldNotRaiseException(){
            when(repository.save(any(User.class))).thenReturn(userSample);
            when(repository.existsUserByEmail(userDTO.getEmail())).thenReturn(false);
            service.registration(userDTO);
            verify(repository).save(any(User.class));
        }

        @Test
        void registrationWithAlreadyExistentUserShouldRaiseException(){
            when(repository.existsUserByEmail(userDTO.getEmail())).thenReturn(true);
            assertThrows(AlreadyExistServiceException.class, () -> service.registration(userDTO));
            verify(repository, never()).save(any(User.class));
        }

        @Test
        void registrationWithIncorrectDataShouldRaiseException(){
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.registration(null)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.registration(new UserDTO())),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.registration(new UserDTO(null, null))),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.registration(new UserDTO(emailSample, null))),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.registration(new UserDTO(null, passwordSample))),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.registration(new UserDTO("not_anemail@email.not@email.not", emailSample))),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> service.registration(new UserDTO(emailSample, "")))
            );
            verify(repository, never()).save(any(User.class));
        }
    }
}
