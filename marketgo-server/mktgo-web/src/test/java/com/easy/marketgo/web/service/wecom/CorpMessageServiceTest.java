package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.MktgoWebApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/17/22 4:11 PM
 * Describe:
 */
@SpringBootTest(classes = MktgoWebApplication.class)
class CorpMessageServiceTest {

    @Autowired
    private CorpMessageService corpMessageService;

    @Test
    public void testRpc() {
//        corpMessageService.checkAgentParams("wwa67b5f2bf5754641", "1000002", "IA6a9_onHKt6Tn6E3L3_GR" +
//                "-clQa6Vmy2kXYS_t1lZ6c");
//        corpMessageService.getDepartmentMembersList("wwa67b5f2bf5754641");

//        corpMessageService.getExternalUsers("wwa67b5f2bf5754641", "WangWeiNing");
        corpMessageService.getExternalUserDetail("wwa67b5f2bf5754641", "wmqPhANwAADkwwqT4B2as3tN6E6-6suA");
//        String test1 = "{\"access_token\":\"test11\", \"expires_in\":7200}";
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            TestLabel testLabel = objectMapper.readValue(test1, TestLabel.class);
//            int m = testLabel.getExpiresIn()+1;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


//    @Data
//    public static class TestLabel{
//        @JsonProperty(value = "access_token")
//        private String accessToken;
//        @JsonProperty(value = "expires_in")
//        private Integer expiresIn;
//    }

}