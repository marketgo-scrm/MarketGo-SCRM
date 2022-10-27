package com.easy.marketgo.api.model.response.customer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/5/22 7:09 PM
 * Describe:
 */
@Data
public class WeComQueryDepartmentMembersClientResponse  implements Serializable {
    private List<WeComMemberMessage> userList;

    @Data
    public static class WeComMemberMessage  implements Serializable {
        private String userId;
        private String name;
        private List<Integer> department;
        private List<Integer> order;
        private String position;
        private String mobile;
        private String gender;
        private String email;
        private String bizMail;
        private List<Integer> isLeaderInDept;
        private List<String> directLeader;
        private String avatar;
        private String thumbAvatar;
        private String telephone;
        private String alias;
        private Integer status;
        private String address;
        private String englishName;
        private Integer mainDepartment;
        private String qrCode;
        private String externalPosition;
    }
}
