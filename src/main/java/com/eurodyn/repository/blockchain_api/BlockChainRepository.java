package com.eurodyn.repository.blockchain_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service
public class BlockChainRepository {

    @Autowired
    private EntityManager entityManager;

    public Boolean pnrAlreadyExistsByRequestId(String requestId) {

        Query query = this.entityManager.createNativeQuery(
                " SELECT id, created_by FROM pnr_request_outgoing " +
                        " WHERE blockchain_request_id = :request_id " +
                        " AND request_type = 'in' ");
        query.setParameter("request_id", requestId);

        List<Object[]> rows = query.getResultList();

        return !rows.isEmpty();
    }

}
