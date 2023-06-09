package com.example.ctsbe.service;

import com.example.ctsbe.dto.promotionLevel.PromotionLevelDTO;
import com.example.ctsbe.entity.PromotionLevel;

import java.util.List;

public interface PromotionLevelService {
    PromotionLevel getPromotionLevelById(int id);

    List<PromotionLevel> getAllLevels();
}
