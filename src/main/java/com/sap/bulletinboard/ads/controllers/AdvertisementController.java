package com.sap.bulletinboard.ads.controllers;

import com.sap.bulletinboard.ads.models.Advertisement;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Validated
@RequestScope
@RestController
@RequestMapping(path = AdvertisementController.PATH)
public class AdvertisementController {

    static final String PATH = "/api/v1/ads";
    static final String DELETE = "/delete";
    static final String ID = "/{id}";
    static final Random random = new Random();

    private static final Map<Integer, Advertisement> advertisementMap = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementController.class);



    @GetMapping
    public Iterable<Advertisement> advertisements() {
        return advertisementMap.values();
    }


    @GetMapping(ID)
    public Advertisement advertisementById(@PathVariable("id") @Min(0)  @NotNull    final Integer id) throws Exception{
        LOGGER.info("id: "+id);
        Advertisement ad = advertisementMap.get(id);
        if (ad == null) {
            throw new NotFoundException("Not found.");
        }
        return advertisementMap.get(id);
    }

    @PutMapping
    public ResponseEntity put(UriComponentsBuilder uriComponentsBuilder){
        LOGGER.warn("Put request without id not allowed");
        UriComponents uriComponents = uriComponentsBuilder.path(PATH).build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).location(uriComponents.toUri()).allow(HttpMethod.POST, HttpMethod.GET, HttpMethod.DELETE ).build();
    }

    @PutMapping(ID)
    @ResponseBody
    public ResponseEntity<Advertisement> putById(@RequestBody @NotNull @NotBlank @NotEmpty final Advertisement advertisement, @PathVariable("id") @NotNull @Min(0) final Integer id, UriComponentsBuilder uriComponentsBuilder) throws NotFoundException{
        boolean found = AdvertisementController.advertisementMap.containsKey(id);
        UriComponents components = uriComponentsBuilder.path(PATH+ID).buildAndExpand(id);
        if (found){
            AdvertisementController.advertisementMap.put(id,advertisement);
            return ResponseEntity.ok().location(components.toUri()).body(advertisement);
        } else {
            throw new NotFoundException("Not found.");
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Advertisement> add(@RequestBody @NotNull Advertisement advertisement, UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        LOGGER.info("Got post req", advertisement.toString());
        advertisement.setId(random.nextInt());
        advertisementMap.put(advertisement.getId(), advertisement);
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(advertisement.getId());
        return ResponseEntity.created(new URI(uriComponents.getPath())).body(advertisement);


    }

    @DeleteMapping(DELETE)
    public ResponseEntity delete(UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        LOGGER.info("Got del req");
        UriComponents uri = uriComponentsBuilder.path(DELETE).build();
        AdvertisementController.advertisementMap.clear();
        return ResponseEntity.noContent().location(uri.toUri()).build();
    }

    @DeleteMapping(DELETE+ID)
    public ResponseEntity deleteById(@PathVariable("id") final Integer id, UriComponentsBuilder uriComponentsBuilder){
        Advertisement deleted = AdvertisementController.advertisementMap.remove(id);
        UriComponents uri = uriComponentsBuilder.path(DELETE+ID).buildAndExpand(id);
        if (deleted == null){
            return ResponseEntity.notFound().location(uri.toUri()).build();
        } else {
            return ResponseEntity.noContent().location(uri.toUri()).build();
        }
    }
}