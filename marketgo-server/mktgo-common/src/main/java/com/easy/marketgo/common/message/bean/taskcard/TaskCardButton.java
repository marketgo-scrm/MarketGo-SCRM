package com.easy.marketgo.common.message.bean.taskcard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-13 20:03:20
 * @description : 任务卡片按钮
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCardButton implements Serializable {
  private static final long serialVersionUID = -4301684507150486556L;

  private String key;
  private String name;
  private String replaceName;
  private String color;
  private Boolean bold;
}
