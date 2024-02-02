package com.eurodyn.controller.blockchain_api;

import com.eurodyn.dto.blockchain_api.RequestDTO;
import com.eurodyn.service.blockchain_api.BlockchainApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/blockchain_api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST})
public class BlockchainApiController {

    private final BlockchainApiService blockchainApiService;

    public BlockchainApiController(BlockchainApiService blockchainApiService) {
        this.blockchainApiService = blockchainApiService;
    }

    @PostMapping("/request")
    public void createRequest(@RequestBody RequestDTO requestDTO,
                              @RequestHeader Map<String, String> headers) {
        blockchainApiService.postObjectData(requestDTO, headers);
    }

}
