package com.easy.marketgo.web.model.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 7:50 PM
 * Describe:
 */
@Data
@EqualsAndHashCode
public class OfflineUserGroupMessage {
    @ExcelProperty("external_user_id")
    private String externalUserId;
    @ExcelProperty("member_id")
    private String memberId;
}
