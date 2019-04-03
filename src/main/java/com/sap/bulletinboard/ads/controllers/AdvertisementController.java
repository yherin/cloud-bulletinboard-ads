package com.sap.bulletinboard.ads.controllers;

import com.sap.bulletinboard.ads.models.Advertisement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RequestScope
@RestController
@RequestMapping(AdvertisementController.PATH)
public class AdvertisementController {

    static final String PATH = "api/v1/ads";

    private static final Map<Long, Advertisement> advertisementMap = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    @GetMapping
    public Iterable<Advertisement> advertisements(){
        return advertisementMap.values();
    }

    @GetMapping("/{id}")
    public Advertisement advertisementById(@PathVariable("id") Long id){
        Advertisement ad = advertisementMap.get(id);
        if (ad == null){
           throw new NotFoundException("OMEGALUL");
        }
        return advertisementMap.get(id);
    }

    @PostMapping
    public ResponseEntity<Advertisement> add(@RequestBody Advertisement advertisement, UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {
        Long uid = random.nextLong();
        advertisementMap.put(uid, advertisement);
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(uid);
        return ResponseEntity.created(new URI(uriComponents.getPath())).body(advertisement);


    }


}
