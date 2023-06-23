package com.li.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.li.usercenter.common.BaseResponse;
import com.li.usercenter.common.ErrorCode;
import com.li.usercenter.common.ResultUtils;
import com.li.usercenter.contant.UserConstant;
import com.li.usercenter.exception.BusinessException;
import com.li.usercenter.model.domain.Team;
import com.li.usercenter.model.domain.User;
import com.li.usercenter.model.domain.UserTeam;
import com.li.usercenter.model.domain.dto.TeamQuery;
import com.li.usercenter.model.domain.request.*;
import com.li.usercenter.model.domain.vo.TeamUserVO;
import com.li.usercenter.model.domain.vo.UserVO;
import com.li.usercenter.service.TeamService;
import com.li.usercenter.service.UserService;

import com.li.usercenter.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 队伍接口
 *
 * @author li
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000/"})
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request)
    {
        if(teamAddRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team =new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        User loginUser = userService.getloginUser(request);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        User loginUser = userService.getloginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    @PostMapping("/update")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request)
    {
        if(teamUpdateRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getloginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if(!result)
        {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeam(@RequestBody long id)
    {
        if(id <= 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
       Team team = teamService.getById(id);
        if(team == null)
        {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> ListTeam(TeamQuery teamQuery, HttpServletRequest request)
{
    if (teamQuery ==null)
    {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    boolean isAdmin = userService.isAdmin(request);
    List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
    //2.判断当前用户是否加入了队伍
    List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
    QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
    try {
        User loginUser = userService.getloginUser(request);
        userTeamQueryWrapper.eq("userId",loginUser.getId());
        userTeamQueryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        //用户已经加入队伍的集合
        Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
        teamList.forEach(team->{
            boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
            team.setHasJoin(hasJoin);
        });
    }catch (Exception e)
    {

    }
    //3.已经加入队伍的人数
    QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
    userTeamJoinQueryWrapper.in("teamId",teamIdList);
    List<UserTeam>  userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
    //队伍id->加入这个队伍的人数
    Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
    //getOrDefault(key, default)如果存在key, 则返回其对应的value, 否则返回给定的默认值
    teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
    return ResultUtils.success(teamList);

}

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> ListTeamByPage(TeamQuery teamQuery)
    {
        if (teamQuery ==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team,teamQuery);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper =  new QueryWrapper<>(team);
        Page<Team> resultPage =teamService.page(page,queryWrapper);

        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getloginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getloginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 查找自己创的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> ListMYTeams(TeamQuery teamQuery, HttpServletRequest request)
    {
        if (teamQuery ==null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);

        User loginUser = userService.getloginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        return ResultUtils.success(teamList);
    }

    /**
     * 查询我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getloginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        //userTeamList 自己加入的所有队伍信息
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        //取出不重复的队伍 id
        //正常情况下一个用户只能加入一次队伍，所以不会有重复的队伍 id
        //但是严谨点，如果有脏数据，或者是后台被修改成一个用户加入一个队伍好几次
        //，就会出现重复的队伍
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> listTeams = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(listTeams);
    }





}
