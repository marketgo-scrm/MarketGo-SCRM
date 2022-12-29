package com.easy.marketgo.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@ImportResource(value = "dubbo-consumer.xml")
@SpringBootApplication(scanBasePackages = {"com.easy.marketgo"})
@EnableCaching
@EnableJdbcRepositories(value = {"com.easy.marketgo"})
@EnableOpenApi
@EntityScan("com.easy.marketgo")
@EnableAsync
public class MktgoWebApplication {

    public static void main(String[] args) {

        try {
            System.setProperty("zookeeper.sasl.client", "false");
            SpringApplication.run(MktgoWebApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

//
//    @Autowired
//    private WeComSysBasePermissionsRepository basePermissionsRepository;
//
//    @PostConstruct
//    public void createBasePermission() {
//
//        String data = "[    {\n" +
//                "        \"path\": \"settings\",\n" +
//                "        \"name\": \"settings\",\n" +
//                "        \"title\": \"系统设置\",\n" +
//                "        \"children\": [\n" +
//                "            {\n" +
//                "                \"path\": \"membermanagement\",\n" +
//                "                \"name\": \"membermanagement\",\n" +
//                "                \"title\": \"成员管理\",\n" +
//                "            },\n" +
//                "            {\n" +
//                "                \"path\": \"permissionmanagement\",\n" +
//                "                \"name\": \"permissionmanagement\",\n" +
//                "                \"title\": \"权限管理\",\n" +
//                "            }\n" +
//                "        ]\n" +
//                "    }]\n";
//
//
//        insertData(JSONUtil.parseArray(data), "", "", "");
//
//
//    }
//
//    private void insertData(JSONArray jsonArray, String parentName, String parentCode, String parentTitle) {
//
//        Iterator<JSONObject> iterator = jsonArray.jsonIter().iterator();
//        while (iterator.hasNext()) {
//            JSONObject next = iterator.next();
//            String path = next.getStr("path");
//            String name = next.getStr("name");
//            String title = next.getStr("title");
//            WeComSysBasePermissionsEntity entity = new WeComSysBasePermissionsEntity();
//            entity.setCode(path);
//            entity.setName(name);
//            entity.setTitle(title);
//            entity.setParentCode(parentCode);
//            entity.setParentName(parentName);
//            entity.setParentTitle(parentTitle);
//            entity.setUuid(UuidUtils.generateUuid());
//            entity.setProjectUuid("59I4zvzWvA24zV1p");
//
//            basePermissionsRepository.save(entity);
//
//
//            if (next.containsKey("children")) {
//                insertData(next.getJSONArray("children"), path, name, title);
//            }
//
//
//        }
//    }
}
