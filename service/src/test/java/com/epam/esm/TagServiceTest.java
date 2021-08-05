package com.epam.esm;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.*;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.TagService;
import com.epam.esm.service.impl.TagServiceImpl;
import com.epam.esm.validator.impl.TagValidator;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    TagRepository tagRepository;

    TagService tagService;

    private final static Long CORRECT_ID_VALUE = 1L;
    private final static Long NOT_EXIST_ID_VALUE = 999L;

    @BeforeEach
    void init(){
        tagService = new TagServiceImpl(tagRepository, new TagValidator());
    }

    @Nested
    class GettingByIdTests {

        @Test
        void correctGettingByIdShouldReturnTag(){
            Tag expected = new Tag(CORRECT_ID_VALUE,"name");
            when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(expected));
            assertEquals(expected, tagService.getById(CORRECT_ID_VALUE));
            verify(tagRepository).findById(CORRECT_ID_VALUE);
        }

        @Test
        void gettingWithNotExistedIdShouldRaiseException() {
            when(tagRepository.findById(NOT_EXIST_ID_VALUE)).thenReturn(Optional.empty());
            assertThrows(NotFoundServiceException.class,() -> tagService.getById(NOT_EXIST_ID_VALUE));
            verify(tagRepository).findById(NOT_EXIST_ID_VALUE);
        }

        @Test
        void gettingWithIncorrectIdShouldRaiseException() {
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> tagService.getById(-1L)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> tagService.getById(null))
            );
            verify(tagRepository, never()).findById(any(Long.class));
        }
    }

    @Nested
    class DeletingTests {
        @Test
        void correctDeletingShouldNotRaiseException() {
            doNothing().when(tagRepository).deleteById(CORRECT_ID_VALUE);
            when(tagRepository.existsById(CORRECT_ID_VALUE)).thenReturn(true);
            tagService.delete(CORRECT_ID_VALUE);
            verify(tagRepository).deleteById(CORRECT_ID_VALUE);
        }

        @Test
        void gettingWithNotExistedIdShouldRaiseException() {
            when(tagRepository.existsById(anyLong())).thenReturn(false);
            assertThrows(NotFoundServiceException.class, () -> tagService.delete(NOT_EXIST_ID_VALUE));
            verify(tagRepository, never()).deleteById(anyLong());
        }

        @Test
        void gettingWithIncorrectIdShouldRaiseException() throws RepositoryException {
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> tagService.delete(-1L)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> tagService.delete(null))
            );
            verify(tagRepository, never()).deleteById(any(Long.class));
        }
    }


    @Nested
    class AddingTests {
        @Test
        void correctAddingNewTagShouldNotRaiseException () {
            Tag tagForAdding = new Tag("name");
            Tag tagForReturning = new Tag(CORRECT_ID_VALUE,"name");
            when(tagRepository.save(any(Tag.class))).thenReturn(tagForReturning);
            assertEquals(tagForReturning, tagService.add(tagForAdding));
            verify(tagRepository).save(tagForAdding);
        }

        @Test
        void addingExistedTagShouldRaiseException(){
            when(tagRepository.existsByName(anyString())).thenReturn(true);
            assertThrows(AlreadyExistServiceException.class, () -> tagService.add(new Tag("existed_tag")));
            verify(tagRepository, never()).save(any(Tag.class));
        }


        @Test
        void addingIncorrectTagShouldRaiseException(){
            assertAll(
                    () -> assertThrows(IncorrectDataServiceException.class,() -> tagService.add(null)),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> tagService.add(new Tag(null))),
                    () -> assertThrows(IncorrectDataServiceException.class,() -> tagService.add(new Tag("   ")))
            );
            verify(tagRepository, never()).save(any(Tag.class));
        }
    }
}
