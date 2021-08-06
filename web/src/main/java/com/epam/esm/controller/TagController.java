package com.epam.esm.controller;

import com.epam.esm.dto.PagedDTO;
import com.epam.esm.dto.TagDTO;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


/**
 * Controller for REST operations with tags
 * Makes get, get by id, add, delete and special calculating operations
 */
@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * @param page current page
     * @param size size of the page
     * @param namePart part of the tags name for searching
     * @return page with tags found
     */
    @GetMapping(produces = { "application/prs.hal-forms+json" })
    @PreAuthorize("hasAuthority('tags:read')")
    public PagedModel<Tag> getTags(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                   @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                   @RequestParam (name = "part", required = false, defaultValue = "") String namePart) {
        PagedDTO<Tag> pagedDTO = tagService.get(namePart, size, page);
        if (pagedDTO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return PagedModel.of(pagedDTO.getPage(), pagedDTO.getPageMetadata());
    }

    /**
     * Get method for receiving one tag by id if it exists
     * @param id - id of tag
     * @return tag found in JSON
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('tags:read')")
    public Tag getTagById (@PathVariable("id") Long id){
        return tagService.getById(id);
    }

    /**
     * Post method for adding a new tag
     * @param tagDTO - tag name for adding
     * @return tag that was added in JSON
     */
    @PostMapping()
    @PreAuthorize("hasAuthority('tags:write')")
    public Tag addNewTag(@RequestBody TagDTO tagDTO) {
        return tagService.add(tagDTO);
    }

    /**
     * Delete method for deleting one tag by id if it exists
     * @param id - id of the tag
     * @return OK response if tag was deleted
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('tags:write')")
    public ResponseEntity<Object> deleteTag (@PathVariable("id") Long id){
        tagService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get method for finding the most widely used tag of a user with the highest cost of all orders
     * @return Tag that was found
     */
    @GetMapping("/most-used-tag")
    @PreAuthorize("hasAuthority('tags:read')")
    Tag getMostUsedTagOfValuableCustomer() {
        return tagService.getMostUsedTagOfValuableCustomer();
    }

}
