package com.zinoviev.lms_system.service;


import com.zinoviev.lms_system.dto.group.*;

import java.util.UUID;

public interface GroupService {

    GroupSummaryDto createGroup(GroupCreateDto dto);

    GroupWithStudentsDto getGroup(UUID id);

    GroupSummaryDto upgradeGroup(UUID id, GroupUpgradeDto dto);

    void deleteGroup(UUID id);

    GroupWithStudentsDto uniteStudentsInGroup(UniteStudentsInGroupDto dto);


    
}
