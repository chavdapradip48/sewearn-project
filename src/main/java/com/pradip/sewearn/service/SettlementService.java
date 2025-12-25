package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.payment.*;

import java.util.List;

public interface SettlementService {

    SettlementCalculateResponse calculate(SettlementCalculateRequest request);

    SettlementResponse create(SettlementCreateRequest request);

    SettlementResponse update(Long settlementId, SettlementUpdateRequest request);

    SettlementDetailResponse getById(Long settlementId);

    List<SettlementSummaryResponse> getSummaryList();
}
