package com.eurodyn.service.blockchain_api;

import com.eurodyn.dto.blockchain_api.AcknowledgeDTO;
import com.eurodyn.dto.blockchain_api.PiuDTO;
import com.eurodyn.dto.blockchain_api.RequestDTO;
import com.eurodyn.repository.blockchain_api.BlockChainRepository;
import com.eurodyn.resttemplates.blockchain_api.BlockchainApiRestTemplate;
import com.eurodyn.resttemplates.sofia.SofiaBCRestTemplate;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        log.debug("*** createRequest ***");

        Map<String, Object> pnrRequestOutgoing = requestDTO.getRequest().get("pnr_request_outgoing_obj");

        pnrRequestOutgoing.put("blockchain_state", "Pending");

        /*If pnrId already exists, return because it is already created */
        String pnrId = (String) pnrRequestOutgoing.get("id");
        if (!(pnrId == null ? "" : pnrId).equals("")) {
            return pnrId;
        }

        /* Save To Sofia */
        Map<String, String> requestData = this.sofiaBCRestTemplate.postOutgoingRequest(requestDTO.getRequest(), headers);
        pnrRequestOutgoing.put("id", requestData.get("response"));

        /* Find receiver id to send it there */
        Map<String, Object> piuObj = (Map<String, Object>) pnrRequestOutgoing.get("piu_obj_obj");
        String uuid = (String) piuObj.get("uuid");

        /* Save it to Blockchain */
        String blockChainRequestId = this.blockchainApiRestTemplate.request(requestDTO, uuid);

        /* Save Blockchain request it to Sofia */
        this.sofiaBCRestTemplate.setRequestIdToPnr(pnrId, blockChainRequestId, headers);

        /* Finally return pnrId to Sofia */
        return pnrId;
    }

    public void syncPnrs(Map<String, String> headers) {
        log.debug("*** syncPnrs ***");

        List<Map<String, Object>> pnrs = this.blockchainApiRestTemplate.getPendingPnrs();
        for (Map<String, Object> pnr : pnrs) {

            if (blockChainRepository.pnrAlreadyExistsByRequestId(pnr.get("requestId").toString())) {
                continue;
            }

            Map<String, Object> requestData = (Map<String, Object>) pnr.get("requestData");
            Map<String, Map<String, Object>> request = (Map<String, Map<String, Object>>) requestData.get("request");

            Map<String, Object> pnrRequestOutgoing = request.get("pnr_request_outgoing_obj");

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

    @Transactional
    public void syncPius() {
        log.debug("*** syncPius ***");

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
        log.debug("*** acknowledge ***");

        Map<String, Object> pnrRequestOutgoing = acknowledgeDTO.getRequest().get("pnr_request_outgoing_obj");
        List<Object> riskΑssesmentResultsArray = (List<Object>) pnrRequestOutgoing.get("risk_assesment_result_obj");

        List<Map<String, Object>> passengerDatasets = new ArrayList<>();
        for (Object riskΑssesmentResult : riskΑssesmentResultsArray) {
            Map<String, Object> fields = (Map<String, Object>) riskΑssesmentResult;
            String passengerId = (String) fields.get("passenger_id");
            Map<String, Object> passengerDataset = this.sofiaBCRestTemplate.getPassenger(passengerId, headers);
            passengerDatasets.add(passengerDataset);
        }

        acknowledgeDTO.setPassengerDatasets(passengerDatasets);

        /* Find receiver id to send it there */
        Map<String, Object> piuObj = (Map<String, Object>) pnrRequestOutgoing.get("piu_obj_obj");
        String uuid = (String) piuObj.get("uuid");

        String requestId = (String) pnrRequestOutgoing.get("blockchain_request_id");
        this.blockchainApiRestTemplate.acknowledge(acknowledgeDTO, uuid, requestId);
        this.sofiaBCRestTemplate.postIncomingRequest(acknowledgeDTO.getRequest(), headers);
    }

    public void reject(Map<String, Map<String, Object>> request, Map<String, String> headers) {
        log.debug("*** reject ***");

        Map<String, Object> pnrRequest = request.get("pnr_request_outgoing");
        String uuid = (String) pnrRequest.get("piu_id");
        String requestId = (String) pnrRequest.get("blockchain_request_id");
        String blockchainRejectionMessage = (String) pnrRequest.get("blockchain_rejection_message");

        pnrRequest.put("blockchain_rejected", 1);

        this.sofiaBCRestTemplate.postIncomingRequest(request, headers);

        this.blockchainApiRestTemplate.reject(blockchainRejectionMessage, uuid, requestId);
    }

    public void syncAcknowledge(Map<String, String> headers) {
        log.debug("*** syncAcknowledge ***");

        List<Map<String, Object>> pnrs = this.blockchainApiRestTemplate.getAckPnrs();
        for (Map<String, Object> pnr : pnrs) {
//            if (blockChainRepository.pnrAlreadyExists(pnr.get("requestId").toString())) {
//                continue;
//            }

            Map<String, Object> responseData = (Map<String, Object>) pnr.get("responseData");
            List<Map<String, Object>> passengerDatasets = (List<Map<String, Object>>) responseData.get("passengerDatasets");

            Map<String, Object> requestData = (Map<String, Object>) pnr.get("requestData");
            Map<String, Map<String, Object>> request = (Map<String, Map<String, Object>>) requestData.get("request");


            Map<String, Object> pnrRequestOutgoing = request.get("pnr_request_outgoing_obj");
            // pnrRequestOutgoing.put("blockchain_request_id", pnr.get("requestId"));
            pnrRequestOutgoing.put("blockchain_state", "Ack");
            pnrRequestOutgoing.put("request_type", "out");
            // pnrRequestOutgoing.put("id", pnr.get("requestId"));
            pnrRequestOutgoing.put("piu_id", pnr.get("respondingPIU"));

            // Map<String, Object> piuObj = (Map<String, Object>) pnrRequestOutgoing.get("piu_obj_obj");

            /* Save Passenger Datasets */
            List<Object> riskAssesmentResults = new ArrayList<>();
            for (Map<String, Object> passengerDataset : passengerDatasets) {
                Map<String, String> passengerResponce =
                        this.sofiaBCRestTemplate.setPassenger(passengerDataset, headers);
                LinkedHashMap<String, String> resultObj = new LinkedHashMap<>();
                resultObj.put("passenger_id", passengerResponce.get("response"));
                riskAssesmentResults.add(resultObj);
            }

            pnrRequestOutgoing.put("risk_assesment_result_obj", riskAssesmentResults);

            /* Save Pnr */
            Object response = this.sofiaBCRestTemplate.postOutgoingRequest(request, headers);

            String respondingPIU = (String) pnr.get("respondingPIU");
            String requestId = (String) pnr.get("requestId");
            this.blockchainApiRestTemplate.confirm(respondingPIU, requestId);
        }

    }

    public void syncReject(Map<String, String> headers) {
        log.debug("*** syncReject ***");

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
