package com.sap.bulletinboard.ads.models;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends PagingAndSortingRepository<Advertisement, Integer> {

}
