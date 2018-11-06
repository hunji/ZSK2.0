package io.renren.modules.knowledge.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.knowledge.entity.KnowledgeTypeEntity;
import io.renren.modules.knowledge.service.KnowledgeTypeService;
import io.renren.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/6
 */
@RestController
@RequestMapping("/knowledge/type")
public class KnowledgeTypeController extends AbstractController {
    @Autowired
    private KnowledgeTypeService knowledgeTypeService;

    @RequestMapping("/list")
    @RequiresPermissions("knowledge:type:list")
    public R list(@RequestParam Map<String, Object> params){
        //查询列表数据
        PageUtils page=knowledgeTypeService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 类型信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("knowledge:type:info")
    public R info(@PathVariable("id") Long id){
        KnowledgeTypeEntity knowledgeType = knowledgeTypeService.selectById(id);

        return R.ok().put("knowledgeType", knowledgeType);
    }

    /**
     * 保存类型
     */
    @SysLog("新增知识类型")
    @RequestMapping("/save")
    @RequiresPermissions("knowledge:type:save")
    public R save(@RequestBody KnowledgeTypeEntity knowledgeTypeEntity){
        ValidatorUtils.validateEntity(knowledgeTypeEntity);

        knowledgeTypeService.save(knowledgeTypeEntity);

        return R.ok();
    }

    /**
     * 修改类型
     */
    @SysLog("修改知识类型")
    @RequestMapping("/update")
    @RequiresPermissions("knowledge:type:update")
    public R update(@RequestBody KnowledgeTypeEntity knowledgeType){
        ValidatorUtils.validateEntity(knowledgeType);

        knowledgeTypeService.update(knowledgeType);

        return R.ok();
    }

    /**
     * 删除知识类型
     */
    @SysLog("删除知识类型")
    @RequestMapping("/delete")
    @RequiresPermissions("knowledge:type:delete")
    public R delete(@RequestBody Long[] ids){
        knowledgeTypeService.deleteBatch(ids);

        return R.ok();
    }

    /**
     * 查找所有类型信息
     * @param params
     * @return
     */
    @RequestMapping("/allInfo")
    public R allInfo(){
        //查询列表数据
        List<KnowledgeTypeEntity> list=knowledgeTypeService.queryAll();
        return R.ok().put("allTypes", list);
    }

}
