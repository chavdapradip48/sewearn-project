package com.pradip.sewearn.dto;

import com.pradip.sewearn.dto.submit.SewEarnSubmitRequest;
import com.pradip.sewearn.model.receive.ReceivedItem;
import com.pradip.sewearn.model.submit.SewEarnSubmit;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AllocationContext {
    private SewEarnSubmit submit;
    private SewEarnSubmitRequest request;

    private Map<Long, ReceivedItem> receivedMap;
    private Map<Long, Integer> alreadySubmittedMap;
}

