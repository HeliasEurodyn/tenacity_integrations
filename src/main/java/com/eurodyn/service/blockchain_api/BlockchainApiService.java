package com.eurodyn.service.blockchain_api;

import com.eurodyn.dto.blockchain_api.PiuDTO;
import com.eurodyn.dto.blockchain_api.AcknowledgeDTO;
import com.eurodyn.dto.blockchain_api.RequestDTO;
import com.eurodyn.repository.blockchain_api.BlockChainRepository;
import com.eurodyn.resttemplates.blockchain_api.BlockchainApiRestTemplate;
import com.eurodyn.resttemplates.sofia.SofiaBCRestTemplate;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Slf4j
@Service
@ToString
public class BlockchainApiService {

    @Autowired
    BlockchainApiRestTemplate blockchainApiRestTemplate;

    @Autowired
    SofiaBCRestTemplate sofiaBCRestTemplate;

    @Autowired
    BlockChainRepository blockChainRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionTemplate transactionTemplate;

    public String createRequest(RequestDTO requestDTO, Map<String, String> headers) {
        Map<String, Object> pnrRequestOutgoing = requestDTO.getRequest().get("pnr_request_outgoing");

        String reqId = (String) pnrRequestOutgoing.get("id");
        if ((reqId == null ? "" : reqId).equals("")) {
            return reqId;
        }

        String pnrId = this.sofiaBCRestTemplate.postOutgoingRequest(requestDTO.getRequest(), headers);
        pnrRequestOutgoing.put("id", pnrId);

        Map<String, Object> pnrRequestOutgoingSubEntities = (Map<String, Object>) pnrRequestOutgoing.get("sub-entities");

        Map<String, Object> piuObj = (Map<String, Object>) pnrRequestOutgoingSubEntities.get("piu_obj");

        String uuid = (String) piuObj.get("id");

        String blockChainRequestId = this.blockchainApiRestTemplate.request(requestDTO, uuid);

        this.sofiaBCRestTemplate.setRequestIdToPnr(pnrId, blockChainRequestId, headers);

        return pnrId;
    }

    public void syncPnrs(Map<String, String> headers) {

        List<Map<String, Object>> pnrs = this.blockchainApiRestTemplate.getPendingPnrs();
        for (Map<String, Object> pnr : pnrs) {

            if (blockChainRepository.pnrAlreadyExists(pnr.get("requestId").toString())) {
                continue;
            }

            Map<String, Object> requestData = (Map<String, Object>) pnr.get("requestData");
            Map<String, Map<String, Object>> request = (Map<String, Map<String, Object>>) requestData.get("request");

            Map<String, Object> pnrRequestOutgoing = request.get("pnr_request_outgoing");


            pnrRequestOutgoing.put("blockchain_request_id", pnr.get("requestId"));
            pnrRequestOutgoing.put("blockchain_state", pnr.get("state"));
            pnrRequestOutgoing.put("request_type", "in");
            pnrRequestOutgoing.put("piu_id", pnr.get("requestingPIU"));

            Map<String, Object> rules = (Map<String, Object>) requestData.get("rules");
            Map<String, Object> rulesCreated = this.sofiaBCRestTemplate.postRules(rules, headers);
            String ruleId = (String) rulesCreated.get("id");
            pnrRequestOutgoing.put("rule_id", ruleId);

            this.sofiaBCRestTemplate.postIncomingRequest(request, headers);

        }
    }

    //    public void syncPius() {
//
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
//    }

    @Transactional
    public void syncPius() {
        List<PiuDTO> pius = this.blockchainApiRestTemplate.getPius();
        for (PiuDTO piu : pius) {
            String id = piu.getId();
            String name = piu.getName();
            String adminEmail = piu.getAdminEmail();

            String strQuery =
                    "INSERT INTO piu (uuid, name, adminmail) " +
                            "SELECT :uuid, :name, :adminmail " +
                            "WHERE NOT EXISTS (SELECT 1 FROM piu WHERE uuid = :uuid)";

            Query spQuery = entityManager.createNativeQuery(strQuery);

            spQuery.setParameter("uuid", id);
            spQuery.setParameter("name", name);
            spQuery.setParameter("adminmail", adminEmail);

            transactionTemplate.execute(transactionStatus -> {
                spQuery.executeUpdate();
                transactionStatus.flush();
                return null;
            });
        }
    }

    public void acknowledge(AcknowledgeDTO acknowledgeDTO, Map<String, String> headers) {

        Map<String, Object> pnrRequestOutgoing = acknowledgeDTO.getRequest().get("pnr_request_outgoing");

        Map<String, Object> pnrRequestOutgoingSubEntities = (Map<String, Object>) pnrRequestOutgoing.get("sub-entities");

        Map<String, Object> riskΑssesmentResult = (Map<String, Object>) pnrRequestOutgoingSubEntities.get("risk_assesment_result");

        List<Map<String, Object>> passengerDatasets = new ArrayList<>();
        for (Map.Entry<String, Object> riskΑssesmentPair : riskΑssesmentResult.entrySet()) {
            Map<String, Object> fields = (Map<String, Object>) riskΑssesmentPair.getValue();
            String passengerId = (String) fields.get("passenger_id");
            Map<String, Object> passengerDataset = this.sofiaBCRestTemplate.getPassenger(passengerId, headers);
            passengerDatasets.add(passengerDataset);
        }

        acknowledgeDTO.setPassengerDatasets(passengerDatasets);

        String uuid = (String) pnrRequestOutgoing.get("piu_id");
        String requestId = (String) pnrRequestOutgoing.get("blockchain_request_id");
        this.blockchainApiRestTemplate.acknowledge(acknowledgeDTO, uuid, requestId);
        this.sofiaBCRestTemplate.postIncomingRequest(acknowledgeDTO.getRequest(), headers);
    }


    public void reject(Map<String, Map<String, Object>> request, Map<String, String> headers) {

        Map<String, Object> pnrRequest = request.get("pnr_request_outgoing");
        String uuid = (String) pnrRequest.get("piu_id");
        String requestId = (String) pnrRequest.get("blockchain_request_id");
        String blockchainRejectionMessage = (String) pnrRequest.get("blockchain_rejection_message");

        pnrRequest.put("blockchain_rejected", 1);

        this.sofiaBCRestTemplate.postIncomingRequest(request, headers);

        this.blockchainApiRestTemplate.reject(blockchainRejectionMessage, uuid, requestId);
    }

    public void syncAcknowledge(Map<String, String> headers) {

        List<Map<String, Object>> pnrs = this.blockchainApiRestTemplate.getAckPnrs();
        for (Map<String, Object> pnr : pnrs) {
//            if (blockChainRepository.pnrAlreadyExists(pnr.get("requestId").toString())) {
//                continue;
//            }

            Map<String, Object> responseData = (Map<String, Object>) pnr.get("responseData");

            Map<String, Map<String, Object>> request = (Map<String, Map<String, Object>>) responseData.get("request");
            List<Map<String, Object>> passengerDatasets = (List<Map<String, Object>>) responseData.get("passengerDatasets");

            /* Save Passenger Datasets */
            for (Map<String, Object> passengerDataset : passengerDatasets) {
                this.sofiaBCRestTemplate.setPassenger(passengerDataset, headers);
            }

            /* Save Pnr */
            Map<String, Object> pnrRequestOutgoing = request.get("pnr_request_outgoing");
            pnrRequestOutgoing.put("blockchain_request_id", pnr.get("requestId"));
            pnrRequestOutgoing.put("blockchain_state", "Ack");
            pnrRequestOutgoing.put("request_type", "out");
            // pnrRequestOutgoing.put("id", pnr.get("requestId"));
            pnrRequestOutgoing.put("piu_id", pnr.get("respondingPIU"));
            this.sofiaBCRestTemplate.postIncomingRequest(request, headers);

            String respondingPIU = (String) pnr.get("respondingPIU");
            String requestId = (String) pnr.get("requestId");
            this.blockchainApiRestTemplate.confirm(respondingPIU, requestId);
        }

    }

    public void syncReject(Map<String, String> headers) {

        List<Map<String, Object>> pnrs = this.blockchainApiRestTemplate.getNackPnrs();
        for (Map<String, Object> pnr : pnrs) {

            Map<String, Object> responseData = (Map<String, Object>) pnr.get("responseData");
            String message = (String) responseData.get("message");
            String requestId = (String) pnr.get("requestId");
            String respondingPIU = (String) pnr.get("respondingPIU");

            this.sofiaBCRestTemplate.setRejectionToPnr(requestId, message, headers);
            this.blockchainApiRestTemplate.confirm(respondingPIU, requestId);
        }

    }

}
