package com.eurodyn.service.blockchain_api;

import com.eurodyn.dto.blockchain_api.RequestDTO;
import com.eurodyn.resttemplates.blockchain_api.BlockchainApiRestTemplate;
import com.eurodyn.resttemplates.sofia.SofiaBlockChainRestTemplate;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@ToString
public class BlockchainApiService {

    @Autowired
    BlockchainApiRestTemplate blockchainApiRestTemplate;

    @Autowired
    SofiaBlockChainRestTemplate sofiaBlockChainRestTemplate;

    public String postObjectData(RequestDTO requestDTO, Map<String, String> headers) {
        Map<String, Object> pnrRequestOutgoing = requestDTO.getRequest().get("pnr_request_outgoing");

        String reqId = (String) pnrRequestOutgoing.get("id");
        if ((reqId == null ? "" : reqId).equals("")) {
            return reqId;
        }

        String id = this.sofiaBlockChainRestTemplate.post(requestDTO.getRequest(), headers);

        pnrRequestOutgoing.put("id", id);

        Map<String, Object> pnrRequestOutgoingSubEntities =
                (Map<String, Object>) pnrRequestOutgoing.get("sub-entities");

        Map<String, Object> piuObj =
                (Map<String, Object>) pnrRequestOutgoingSubEntities.get("piu_obj");

        String uuid = (String) piuObj.get("uuid");

        this.blockchainApiRestTemplate.post(requestDTO, uuid);

        return id;
    }

    public void syncPnrs(Map<String, String> headers) {

        List<Map<String, Object>> pnrs = this.blockchainApiRestTemplate.getPnrs();
        for (Map<String, Object> pnr : pnrs) {

            if (pnr.get("state").equals("Pending")) {
                continue;
            }

            RequestDTO requestDTO = (RequestDTO) pnr.get("data");
            Map<String, Object> pnrRequestOutgoing = requestDTO.getRequest().get("pnr_request_outgoing");

            pnrRequestOutgoing.put("blockchain_request_id", pnr.get("requestId"));
            pnrRequestOutgoing.put("blockchain_state", pnr.get("state"));
            pnrRequestOutgoing.put("request_type", pnr.get("in"));


            Map<String, Object> rules = this.sofiaBlockChainRestTemplate.postRules(requestDTO.getRules(), headers);
            String ruleId = (String) rules.get("id");
            pnrRequestOutgoing.put("rule_id", ruleId);

            this.sofiaBlockChainRestTemplate.post(requestDTO.getRequest(), headers);

        }
    }

    public void syncPius() {

//        List<Map<String, String>> pius = this.blockchainApiRestTemplate.getPius();
//        for (Map<String, String> piu : pius) {
//
//            String id = piu.get("id");
//            String name = piu.get("name");
//            String adminEmail = piu.get("adminEmail");
//
//            String strQuery =
//                    " INSERT INTO piu (uuid, name,  adminmail)  " +
//                            " SELECT :uuid, :name, :adminmail " +
//                            " WHERE (SELECT COUNT(uuid) FROM piu WHERE uuid = :uuid ) = 0 ";
//
//            Query spQuery = entityManager.createNativeQuery(strQuery);
//
//            spQuery.setParameter("uuid", id);
//            spQuery.setParameter("name", name);
//            spQuery.setParameter("adminmail", adminEmail);
//
//
//            transactionTemplate.execute(transactionStatus -> {
//                spQuery.executeUpdate();
//                transactionStatus.flush();
//                return null;
//            });
//
//        }
    }

}
