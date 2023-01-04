package com.easy.marketgo.gateway.wecom.response.customer;

import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/5/22 7:09 PM
 * Describe:
 */
@Data
public class QueryDepartmentMembersResponse extends WeComBaseResponse {
    @JsonProperty("userlist")
    private List<WeComMemberMessage> userList;

    @Data
    public static class WeComMemberMessage {
        @JsonProperty("userid")
        private String userId;
        private String name;
        private List<Integer> department;
        private List<Integer> order;
        private String position;
        private String mobile;
        private String gender;
        private String email;
        @JsonProperty("biz_mail")
        private String bizMail;
        @JsonProperty("is_leader_in_dept")
        private List<Integer> isLeaderInDept;
        @JsonProperty("direct_leader")
        private List<String> directLeader;
        private String avatar;
        @JsonProperty("thumb_avatar")
        private String thumbAvatar;
        private String telephone;
        private String alias;
        private Integer status;
        private String address;
        @JsonProperty("english_name")
        private String englishName;
        @JsonProperty("main_department")
        private Integer mainDepartment;
        @JsonProperty("qr_code")
        private String qrCode;
        @JsonProperty("external_position")
        private String externalPosition;
    }
}
