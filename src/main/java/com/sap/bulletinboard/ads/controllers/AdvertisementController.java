package com.sap.bulletinboard.ads.controllers;

import com.sap.bulletinboard.ads.models.Advertisement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RequestScope
@RestController
@RequestMapping(path = AdvertisementController.PATH)
public class AdvertisementController {

    static final String PATH = "/api/v1/ads";
    static final String DELETE = "/delete";
    static final String ID = "/{id}";

    private static final Map<Long, Advertisement> advertisementMap = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementController.class);



    @GetMapping
    public Iterable<Advertisement> advertisements() {
        return advertisementMap.values();
    }

    @GetMapping(ID)
    public Advertisement advertisementById(@PathVariable("id") Long id) {
        Advertisement ad = advertisementMap.get(id);
        if (ad == null) {
            throw new NotFoundException("Not found.");
        }
        return advertisementMap.get(id);
    }

    @PostMapping
    public ResponseEntity<Advertisement> add(@RequestBody Advertisement advertisement, UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        LOGGER.info("Got post req", advertisement);
        final Long uid = Math.abs(random.nextLong());
        advertisementMap.put(uid, advertisement);
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(uid);
        return ResponseEntity.created(new URI(uriComponents.getPath())).body(advertisement);


    }

    @DeleteMapping(DELETE)
    public ResponseEntity<Collection<Advertisement>> delete(UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        LOGGER.info("Got del req");
        Collection<Advertisement> values = AdvertisementController.advertisementMap.values();
        AdvertisementController.advertisementMap.clear();
        return ResponseEntity.ok().body(values);

    }
}