package com.easy.marketgo.web.service.user;

import com.easy.marketgo.web.model.request.user.OrganizationalStructureQueryRequest;
import com.easy.marketgo.web.model.request.user.OrganizationalStructureRequest;
import com.easy.marketgo.web.model.response.user.OrganizationalStructureQueryResponse;
import com.easy.marketgo.web.model.response.user.OrganizationalStructureResponse;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-17 19:49:06
 * @description : IOrganizationalStructureService.java
 */
public interface IOrganizationalStructureService {

    OrganizationalStructureResponse fetchStructures(OrganizationalStructureRequest request);

    OrganizationalStructureQueryResponse fetchStructures(OrganizationalStructureQueryRequest request);

}
