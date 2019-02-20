package io.renren.modules.knowledge.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.modules.knowledge.dto.UserInfo;
import io.renren.modules.knowledge.entity.KnowledgeQuestionEntity;
import io.renren.modules.knowledge.service.KnowledgeQuestionService;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Collections;
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
        Query query = new Query(params);
        List<KnowledgeQuestionEntity> contentList = questionService.chatContentPage(query);
        // 分页查出最新的20条数据后进行反转 让最新的消息放在最后面
        Collections.reverse(contentList);
        int total = questionService.chatContentPageCount(query);
        PageUtils page = new PageUtils(contentList, total, query.getLimit(), query.getCurrPage());
        return R.ok().put("page",page);
    }

    @RequestMapping("/roleInfo/{userName}")
    public R roleInfo(@PathVariable("userName") String userName){
        boolean isGuest=false;
        List<String> userInfos = userRoleService.queryRoleByUserName(userName);
        // 没有角色时也认为是游客
        if(userInfos.size()==0 ||userInfos.contains(KNOWLEDGE_GUEST_ROEL_NAME)){
            isGuest=true;
        }
        return R.ok().put("isGuest",isGuest);
    }
}
