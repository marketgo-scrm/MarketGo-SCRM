package com.easy.marketgo.core.model.taskcenter;

import com.easy.marketgo.common.enums.WeComMassTaskExternalUserStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMemberStatusEnum;
import com.easy.marketgo.common.enums.WeComMassTaskMetricTypeEnum;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/4/22 6:30 PM
 * Describe:
 */
@Data
public class WeComTaskCenterMetrics {
    private String projectUuid;
    private String taskUuid;
    private String uuid;
    private String corpId;
    private Date planTime;
    private WeComMassTaskTypeEnum taskType;
    private WeComMassTaskMetricTypeEnum metricType;
    private MemberMessage memberMessage;
    private ExternalUserMessage externalUserMessage;
    private MomentCommentsMessage momentCommentsMessage;

    @Data
    public static class MemberMessage {
        private Boolean finish;
        private String sentTime;
        private List<MemberStatus> memberState;
    }

    @Data
    public static class ExternalUserMessage {
        private Boolean finish;
        private String msgId;
        private String memberId;
        private List<ExternalUserStatus> externalUserStatus;
        private List<ChatStatus> chatStatus;
    }

    @Data
    public static class MemberStatus {
        private String memberId;
        private WeComMassTaskMemberStatusEnum status;
        private Integer externalUserCount;
        private String time;
    }

    @Data
    public static class MomentCommentsMessage {
        private String memberId;
        private String momentId;
        private List<ExternalUserStatus> externalUserStatus;
    }

    @Data
    public static class ExternalUserStatus {
        private String externalUserId;
        private WeComMassTaskExternalUserStatusEnum status;
        private String type;
        private String time;
    }

    @Data
    public static class ChatStatus {
        private String chatId;
        private String status;
        private String time;
    }
}
