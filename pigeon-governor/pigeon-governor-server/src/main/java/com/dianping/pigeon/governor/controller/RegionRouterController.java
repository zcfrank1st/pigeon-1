package com.dianping.pigeon.governor.controller;

import com.dianping.pigeon.governor.model.Project;
import com.dianping.pigeon.governor.model.User;
import com.dianping.pigeon.governor.service.ProjectOwnerService;
import com.dianping.pigeon.governor.service.ProjectService;
import com.dianping.pigeon.governor.service.RegionRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.dianping.pigeon.governor.util.GsonUtils.gson;

/**
 * Created by shihuashen on 16/6/15.
 */
@Controller
public class RegionRouterController extends BaseController{
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RegionRouterService regionRouterService;
    @Autowired
    private ProjectOwnerService projectOwnerService;

    @RequestMapping(value = {"/region/regionRouter"},method = RequestMethod.GET)
    public String mainRegionConfig(HttpServletRequest request , HttpServletResponse response , ModelMap modelMap){
        List<Project> projects = projectService.retrieveAllIdNamesByCache();
        modelMap.addAttribute("projects", gson.toJson(projects));
        commonnav(modelMap,request);
        return "/config/RegionRouter";
    }

    @RequestMapping(value = {"/region/projectRegionRouter"},method = RequestMethod.POST)
    public String ajaxLoadProjectRegionConfig(HttpServletRequest request , HttpServletResponse response , ModelMap modelMap){
        String projectName = request.getParameter("projectName");
        if(projectService.findProject(projectName)!=null){
            System.out.println(projectName);
            boolean enableState = regionRouterService.getEnableState(projectName);
            System.out.println("The init region router state: "+ enableState);
            modelMap.put("projectName",projectName);
            commonnav(modelMap,request);
            User user = getUserInfo(request);
            String dpAccount = user!=null?user.getDpaccount():"null";
            if(projectOwnerService.isProjectOwner(dpAccount,projectName)){
                modelMap.put("empowered","true");
                System.out.println("is Owner");
            }
            else{
                modelMap.put("empowered","false");
                System.out.println("not a Owner");
            }
            modelMap.put("enableState",String.valueOf(enableState));
            return "/config/project/projectRegionRouterConfig";
        }else{
            return "/config/project/notFound";
        }
    }

    @RequestMapping(value = {"/region/setRegionEnableState"},method = RequestMethod.POST)
    public void switchProjectRegionState(HttpServletRequest request,HttpServletResponse response,ModelMap modelMap){
        String state = request.getParameter("state");
        String projectName = request.getParameter("projectName");
        regionRouterService.setEnableState(projectName,state);
    }
}
