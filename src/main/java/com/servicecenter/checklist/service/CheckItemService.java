package com.servicecenter.checklist.service;

import com.servicecenter.checklist.entity.CheckItem;
import com.servicecenter.checklist.repository.CheckItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckItemService {

    private final CheckItemRepository checkItemRepository;

    @Cacheable(value = "checkItems", key = "#deviceType")
    @Transactional(readOnly = true)
    public List<CheckItem> getActiveChecksForDevice(String deviceType){
        log.debug("Fetching active checks for device type {}", deviceType);

        List<CheckItem> checks = checkItemRepository.findActiveChecksForDevice(deviceType);
        log.debug("Found {} checks for {}", checks.size(), deviceType);

        return checks;
    }

    @CacheEvict(value = "checkItems", allEntries = true)
    @Transactional
    public void refreshCheckItemsCache() {
        log.debug("Clearing checkItems cache");
    }


}
