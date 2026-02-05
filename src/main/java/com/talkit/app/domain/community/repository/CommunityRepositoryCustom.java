package com.talkit.app.domain.community.repository;

import com.talkit.app.domain.community.dto.CommunitySearchConditionDto;
import com.talkit.app.domain.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommunityRepositoryCustom {

    Page<Community> searchByCondition(CommunitySearchConditionDto condition, Pageable pageable);
}
