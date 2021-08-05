package com.epam.esm.controller;

import com.epam.esm.dto.CertificateDTO;
import com.epam.esm.dto.OrderDTO;
import com.epam.esm.dto.OrderRequestDTO;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.criteria.CertificateCriteria;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Permission;
import com.epam.esm.hateoas.assembler.GiftCertificateAssembler;
import com.epam.esm.hateoas.assembler.OrderAssembler;
import com.epam.esm.hateoas.model.GiftCertificateModel;
import com.epam.esm.hateoas.model.OrderModel;
import com.epam.esm.security.provider.AuthenticationAndTokenProvider;
import com.epam.esm.service.GiftCertificateService;


import com.epam.esm.service.OrderService;
import com.epam.esm.util.PatchUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.hateoas.IanaLinkRelations;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for REST operations with gift certificates
 * Makes get, get by id, add, delete, update and buy operations
 */
@RestController
@RequestMapping("/certificates")
public class GiftCertificateController {

    private final GiftCertificateService giftCertificateService;

    private final GiftCertificateAssembler certificateAssembler;

    private final OrderAssembler orderAssembler;

    private final OrderService orderService;

    private final AuthenticationAndTokenProvider authAndTokenProvider;


    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService,
                                     GiftCertificateAssembler certificateAssembler,
                                     OrderAssembler orderAssembler, OrderService orderService, AuthenticationAndTokenProvider authAndTokenProvider) {
        this.giftCertificateService = giftCertificateService;
        this.certificateAssembler = certificateAssembler;
        this.orderAssembler = orderAssembler;
        this.orderService = orderService;
        this.authAndTokenProvider = authAndTokenProvider;
    }

    /**
     * Get method for receiving paged list of certificates by some criteria
     * @return List of certificates in JSON
     */

    @GetMapping(produces = { "application/prs.hal-forms+json" })
    public PagedModel<GiftCertificateModel> getCertificates ( @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                              @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                              @RequestParam Map<String, String> criteriaParams){
        CertificateCriteria criteria = CertificateCriteria.createCriteria(criteriaParams);
        PagedDTO<GiftCertificate> pagedDTO = giftCertificateService.get(criteria, size, page);
        if (pagedDTO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return certificateAssembler.toPagedModel(pagedDTO.getPage(), pagedDTO.getPageMetadata(), criteria);
    }

    /**
     * Get method for receiving one certificate by id if it exists
     * @param id - id of certificate
     * @return certificate found in JSON
     */
    @GetMapping(value = "/{id}", produces = { "application/prs.hal-forms+json" })
    public GiftCertificateModel getCertificateById (@PathVariable("id") Long id){
        GiftCertificate giftCertificate = giftCertificateService.getById(id);
        GiftCertificateModel certificateModel = certificateAssembler.toModel(giftCertificate);
        if (!giftCertificate.getDeleted()){
            addAffordances(certificateModel);
        }
        return certificateModel;
    }

    /**
     * Post method for adding a new gift certificate
     * @param giftCertificate - certificate for adding
     * @return certificate that was added in JSON
     */
    @PostMapping( produces = { "application/prs.hal-forms+json" })
    @PreAuthorize("hasAuthority('certificates:write')")
    public GiftCertificateModel addNewCertificate(@RequestBody CertificateDTO giftCertificate){
        GiftCertificate certificateForResponse = giftCertificateService.add(giftCertificate);
        return certificateAssembler.toModel(certificateForResponse);
    }

    /**
     * Delete method for deleting one certificate by id if it exists
     * @param id - id of the certificate
     * @return OK response if certificate was deleted
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('certificates:write')")
    public ResponseEntity<Object> deleteCertificate (@PathVariable("id") Long id){
        giftCertificateService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Patch method for modifying existed gift certificate.
     * Accept patch commands in JSON format RFC6901.
     * @param id - id of certificate for modifying
     * @param patch - RFC6901 patch commands
     * @return modified certificate
     */
    @PreAuthorize("hasAuthority('certificates:write')")
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json",  produces = { "application/prs.hal-forms+json" })
    public GiftCertificateModel updateCertificate(@PathVariable Long id, @RequestBody JsonPatch patch) {
        GiftCertificate certificateForResponse;
        ModelMapper modelMapper = new ModelMapper();
        GiftCertificate giftCertificate = giftCertificateService.getById(id);
        if (giftCertificate.getDeleted()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found");
        }
        CertificateDTO current = modelMapper.map(giftCertificate, CertificateDTO.class);
        CertificateDTO modified;
        try {
            modified = PatchUtil.applyPatch(patch, current, CertificateDTO.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JsonPatch error");
        }
        certificateForResponse = giftCertificateService.update(modified, id);
        return certificateAssembler.toModel(certificateForResponse);
    }

    /**
     * Post method for buying gift certificate by user
     * @param orderRequest contains certificates id
     * @return Order with all data about purchase
     */
    @PreAuthorize("hasAuthority('certificates:buy')")
    @PostMapping(value = "/buy", produces = { "application/prs.hal-forms+json" })
    public OrderModel buyCertificate(@RequestBody OrderRequestDTO orderRequest){
        OrderDTO orderDTO = new OrderDTO(authAndTokenProvider.getUserName(), orderRequest.getId());
        Order orderForResponse = orderService.makeOrder(orderDTO);
        return orderAssembler.toModel(orderForResponse);
    }

    private void addAffordances(GiftCertificateModel model){

        if (!authAndTokenProvider.hasAuthentication()) {
            return;
        }
        if (authAndTokenProvider.containsAuthority(Permission.CERTIFICATES_WRITE.name)){
            model.mapLink(IanaLinkRelations.SELF, l -> l
                    .andAffordance(afford(methodOn(GiftCertificateController.class).addNewCertificate(null)))
                    .andAffordance(afford(methodOn(GiftCertificateController.class).updateCertificate(model.getId(),null)))
                    .andAffordance(afford(methodOn(GiftCertificateController.class).deleteCertificate(model.getId())))
                    .andAffordance(afford(methodOn(GiftCertificateController.class).buyCertificate(new OrderRequestDTO()))));
        } else {
            model.mapLink(IanaLinkRelations.SELF, l -> l
                    .andAffordance(afford(methodOn(GiftCertificateController.class).buyCertificate(new OrderRequestDTO()))));
        }
    }

}
