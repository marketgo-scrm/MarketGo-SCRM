package com.easy.marketgo.core.entity.customer;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/7/22 9:43 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_relation_member_external_user")
public class WeComRelationMemberExternalUserEntity extends BaseEntity {
    private String corpId;
    private String memberId;
    private String externalUserId;
    private String externalUserName;
    /**
     * 外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户
     */
    private Integer type;
    private String avatar;
    private String unionid;
    /**
     * 外部联系人性别 0-未知 1-男性 2-女性。第三方不可获取，上游企业不可获取下游企业客户该字段，返回值为0，表示未定义
     */
    private Integer gender;
    private String corpName;
    private String corpFullName;
    private String remark;
    private String description;
    /**
     * 0 是好友
     * 1 员工删除客户
     * 2 客户删除员工
     */
    private Integer relationType;
    private String tags;
    private Date addTime;
    /**
     * 0	未知来源
     * 1	扫描二维码
     * 2	搜索手机号
     * 3	名片分享
     * 4	群聊
     * 5	手机通讯录
     * 6	微信联系人
     * 8	安装第三方应用时自动添加的客服人员
     * 9	搜索邮箱
     * 10	视频号添加
     * 11	通过日程参与人添加
     * 12	通过会议参与人添加
     * 13	添加微信好友对应的企业微信
     * 14	通过智慧硬件专属客服添加
     * 201	内部成员共享
     * 202	管理员/负责人分配
     */
    private Integer addWay;
    private String addSourceUserId;
}
