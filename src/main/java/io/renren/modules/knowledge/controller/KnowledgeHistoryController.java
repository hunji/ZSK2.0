package io.renren.modules.knowledge.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.knowledge.entity.KnowledgeHistoryEntity;
import io.renren.modules.knowledge.service.KnowledgeHistoryService;
import io.renren.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/29
 */
@RestController
@RequestMapping("/knowledge/history")
public class KnowledgeHistoryController  extends AbstractController {
    @Autowired
    private KnowledgeHistoryService historyService;

    @RequestMapping("/list")
    @RequiresPermissions("knowledge:history:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = historyService.queryPage(params);
        return R.ok().put("page", page);
    }

    @RequestMapping("/save")
    public R save(@RequestBody KnowledgeHistoryEntity entity){
        ValidatorUtils.validateEntity(entity);

        entity.setUseDate(new Date());
        historyService.save(entity);
        return R.ok();
    }
}
