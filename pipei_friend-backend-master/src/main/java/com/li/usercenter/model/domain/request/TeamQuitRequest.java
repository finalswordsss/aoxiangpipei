package com.li.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 * @author li

 * */
@Data
public class TeamQuitRequest implements Serializable {



    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long teamId;

}
