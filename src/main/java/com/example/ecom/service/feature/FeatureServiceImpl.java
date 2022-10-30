package com.example.ecom.service.feature;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.feature.FeatureResponse;
import com.example.ecom.repository.feature.Feature;
import com.example.ecom.repository.feature.FeatureRepository;
import com.example.ecom.service.AbstractService;

@Service
public class FeatureServiceImpl extends AbstractService<FeatureRepository> implements FeatureService {

        @Override
        public Optional<ListWrapperResponse<FeatureResponse>> getFeatures(Map<String, String> allParams, String keySort,
                        int page, int pageSize, String sortField) {
                List<Feature> features = repository.getFeatures(allParams, keySort, page, pageSize, sortField).get();
                return Optional.of(new ListWrapperResponse<FeatureResponse>(
                                features.stream()
                                                .map(f -> new FeatureResponse(f.get_id().toString(), f.getName(),
                                                                f.getPath(),
                                                                f.getDeleted()))
                                                .collect(Collectors.toList()),
                                page, pageSize, repository.getTotal(allParams)));
        }
}
