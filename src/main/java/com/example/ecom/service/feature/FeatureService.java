package com.example.ecom.service.feature;

import java.util.Map;
import java.util.Optional;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.feature.FeatureResponse;

public interface FeatureService {
    Optional<ListWrapperResponse<FeatureResponse>> getFeatures(Map<String, String> allParams, String keySort, int page,
            int pageSize, String sortField);

    void changeStatusFeature(String id);
}
