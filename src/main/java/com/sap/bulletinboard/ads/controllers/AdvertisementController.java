package com.sap.bulletinboard.ads.controllers;

import com.sap.bulletinboard.ads.models.Advertisement;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

@Validated
@RequestScope
@RestController
@RequestMapping(path = AdvertisementController.PATH)
public class AdvertisementController {

    static final String PATH = "/api/v1/ads";
    static final String DELETE = "/delete";
    static final String ID = "/{id}";
    private AdvertisementRepository advertisementRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementController.class);

    @Inject
    public AdvertisementController(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    @GetMapping
    public Iterable<Advertisement> advertisements() {
        return advertisementRepository.findAll();
    }


    @GetMapping(ID)
    public Advertisement advertisementById(@PathVariable("id") @Min(0) @NotNull final Integer id) throws Exception {
        LOGGER.info("id: " + id);
        //Advertisement ad = advertisementMap.get(id);
        Advertisement ad = advertisementRepository.findOne(id);
        if (ad == null) {
            throw new NotFoundException("Not found.");
        }
        return ad;
    }

    @PutMapping
    public ResponseEntity put(UriComponentsBuilder uriComponentsBuilder) {
        LOGGER.warn("Put request without id not allowed");
        UriComponents uriComponents = uriComponentsBuilder.path(PATH).build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).location(uriComponents.toUri()).allow(HttpMethod.POST, HttpMethod.GET, HttpMethod.DELETE).build();
    }

    @PutMapping(ID)
    @ResponseBody
    public ResponseEntity<Advertisement> putById(@RequestBody @NotNull final Advertisement paramAd, @PathVariable("id") @NotNull @Min(0) final Integer id, UriComponentsBuilder uriComponentsBuilder) throws NotFoundException {


        Advertisement foundAd = advertisementRepository.findOne(id);
        UriComponents components = uriComponentsBuilder.path(PATH + ID).buildAndExpand(id);
        if (foundAd != null) {
            foundAd.setTitle(paramAd.getTitle());
            advertisementRepository.save(foundAd);
            return ResponseEntity.ok().location(components.toUri()).body(foundAd);
        } else {
            throw new NotFoundException("Not found.");
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Advertisement> add(@RequestBody @NotNull Advertisement advertisement, UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        LOGGER.info("Got post req", advertisement.toString());
//        advertisement.setId(random.nextInt());
//        advertisementMap.put(advertisement.getId(), advertisement);
        advertisementRepository.save(advertisement);
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(advertisement.getId());
        return ResponseEntity.created(new URI(uriComponents.getPath())).body(advertisement);


    }

    @DeleteMapping(DELETE)
    public ResponseEntity delete(UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        LOGGER.info("Got del req");
        UriComponents uri = uriComponentsBuilder.path(DELETE).build();
        advertisementRepository.deleteAll();
        return ResponseEntity.noContent().location(uri.toUri()).build();
    }

    @DeleteMapping(DELETE + ID)
    public ResponseEntity deleteById(@PathVariable("id") final Integer id, UriComponentsBuilder uriComponentsBuilder) {
        boolean exists = advertisementRepository.exists(id);
        UriComponents uri = uriComponentsBuilder.path(DELETE + ID).buildAndExpand(id);
        if (!exists) {
            return ResponseEntity.notFound().location(uri.toUri()).build();
        } else {
            advertisementRepository.delete(id);
            return ResponseEntity.noContent().location(uri.toUri()).build();
        }
    }
}