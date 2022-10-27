package com.easy.marketgo.react.bean;

import lombok.Data;
import lombok.ToString;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-01 20:22:29
 * @description : WeChatCropEventCallBack.java
 */
@Data
@ToString(callSuper = true)
public class WeChatCropEventCallBack extends BaseSuiteRes {
    /**
     * <xml>
     * <ToUserName><![CDATA[ww095366cf26acaafd]]></ToUserName>
     * <Encrypt>
     * <![CDATA[JPljzE4+vRyzEIgNhGgOQQ4nlAK/PucpDihs7JD9RO8lWaM3OXErxhKtoLK18CgoeNDPc5wzRiie9zkQj8hmdodSG+n5x/wWscP9uL9+b5js10m+wbvKKFjxN0DOxLKKEXTQyLStN30uvdkpivzxAjD40CVztwHnGsdVRk6IqnCU66KtdpDJlOjRjahwO0hoyr7VN1txPR1MmiI2zSTrb254/7Y3zCG4hMMZ7bxYJVIeiDf+eqd4FqxaZo3uzoa6JYpJhoPaWpZkYSlyk+I5Xblcd58xUHYP2DicG2b5EzxYM1XiQs8M3UtXpMdpbjJsPw0PqsafXmxmx6blklSk7sMGBWy5kEyOP5UvxPxHd2K557fopwA5JglJHUPTM7D2z5cxRYRE+oj5rqFmAKnp1krzfEKzGbyGmNdxivbEZ75U7LtpnID+ipsWkTh6Ngw0N9oX/RVJeK0iLPswlTRfKNgWbhttKR3Vg0uVktwxE5RHaj/o+XbqJAbPmG7tKY3DR0WKPj1VWBQV15g14jgIr3L4DLsSfRyU1my/NuMHR5xl/HAY75LJBhFABTPZjroAPL8KGeIotXWDK78uvqaR2al5VKJd/ExwS6lJMtpAzULuqnmmce+9AaBPnDsA+oH3]]></Encrypt>
     * <AgentID><![CDATA[2000003]]></AgentID>
     * </xml>
     */
    private String toUserName;
    private String encrypt;
    private String agentID;

}
