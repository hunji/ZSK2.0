package io.renren.modules.knowledge.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.knowledge.dto.QuestionDTO;
import io.renren.modules.knowledge.dto.UserInfo;
import io.renren.modules.knowledge.entity.KnowledgeQuestionEntity;
import io.renren.modules.knowledge.service.KnowledgeQuestionService;
import io.renren.modules.knowledge.websocket.WebSocketServer;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.service.SysUserRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.renren.common.utils.Constant.KNOWLEDGE_GUEST_ROEL_NAME;

/**
 * @author hunji
 * @date 2018/12/4
 */
@RestController
@RequestMapping("/knowledge/question")
public class KnowledgeQuestionController  extends AbstractController {
    @Autowired
    private KnowledgeQuestionService questionService;
    @Autowired
    private SysUserRoleService userRoleService;

    @RequestMapping("/list")
    @RequiresPermissions("knowledge:question:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = questionService.queryPage(params);

        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setPage(page);
        questionDTO.setTotalCount(WebSocketServer.userOnlineCount);
        return R.ok().put("qData", questionDTO);
    }

    /**
     * 保存问题时调用
     * @param entity
     * @return
     */
    @RequestMapping("/save")
    public R save(@RequestBody KnowledgeQuestionEntity entity) throws IOException {
        entity.setCreateDate(new Date());
        questionService.save(entity);
        return R.ok();
    }

    /**
     * 获取所有管理员名称
     * @return
     */
    @RequestMapping("/adminNames")
    public R adminNames(){
        List<UserInfo> data = userRoleService.queryAdminUser();
        return R.ok().put("data",data);
    }

    /**
     * 获取所有guest名称
     */
    @RequestMapping("/guestNames")
    public R guestNames(){
        List<UserInfo> data = userRoleService.queryGuestUser();
        return R.ok().put("data",data);
    }

    /**
     * 获取两个人的聊天内容
     *
     * @return
     */
    @RequestMapping("/chatContent")
    public R chatContent(@RequestParam Map<String, Object> params){
        List<KnowledgeQuestionEntity> contentList = questionService.chatContent(params);
        return R.ok().put("contentList",contentList);
    }

    @RequestMapping("/roleInfo/{userName}")
    public R roleInfo(@PathVariable("userName") String userName){
        boolean isGuest=false;
        List<String> userInfos = userRoleService.queryRoleByUserName(userName);
        if(userInfos.contains(KNOWLEDGE_GUEST_ROEL_NAME)){
            isGuest=true;
        }
        return R.ok().put("isGuest",isGuest);
    }
}
