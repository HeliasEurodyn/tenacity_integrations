package com.eurodyn.service.impl;

import com.eurodyn.dto.sofia.SensorValueDto;
import com.eurodyn.resttemplates.sofia.SofiaRestTemplate;
import com.eurodyn.service.NgsiV2SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service
public class NgsiV2SubscriptionServiceImpl implements NgsiV2SubscriptionService {
    @Autowired
    SofiaRestTemplate sofiaRestTemplate;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<Object[]> postSensor(SensorValueDto sensorValueDto, String bearerToken) throws Exception {

        this.sofiaRestTemplate.tokenValidationCheck(bearerToken);

        List<Object[]> ids = this.getSensorIdByLink(sensorValueDto.getSensorId());
        if (ids.isEmpty()) {
            throw new Exception();
        }
        Object[] idLine = ids.get(0);
        String id = (String) idLine[0];
        this.saveValue(id, sensorValueDto.getValue());
        return ids;
    }

    public List<Object[]> getSensorIdByLink(String linkId) {
        Query query = this.entityManager.createNativeQuery("SELECT `id`, sensor_name FROM `sensor_table` WHERE link_id = :linkId");
        query.setParameter("linkId", linkId);
        return query.getResultList();

    }

    public void saveValue(String id, double value) {
        Query query = entityManager.createNativeQuery(" INSERT INTO values_table (sensor_id_fk, value) VALUES ( :id, :value);");
        query.setParameter("id", id);
        query.setParameter("value", value);
        query.executeUpdate();
    }

}
