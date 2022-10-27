package com.easy.marketgo.react.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-01 20:22:37
 * @description : BaseSuiteRes.java
 */
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BaseSuiteRes implements Serializable {
    private String suiteId;
    private String infoType;
    private String timeStamp;
}
