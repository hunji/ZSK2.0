package io.renren.modules.knowledge.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.knowledge.entity.KnowledgeTypeEntity;
import io.renren.modules.knowledge.service.KnowledgeTypeService;
import io.renren.modules.sys.controller.AbstractController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public List<KnowledgeTypeEntity> list(){
        //递归查询出主题下的所有分类
        List<KnowledgeTypeEntity> knowledgeTypeList = knowledgeTypeService.queryAll();
        for (KnowledgeTypeEntity knowledgeTypeEntity : knowledgeTypeList) {
            KnowledgeTypeEntity parentEntity = knowledgeTypeService.selectById(knowledgeTypeEntity.getParentId());
            if(parentEntity!=null){
                knowledgeTypeEntity.setParentName(parentEntity.getTypeName());
            }
        }
        return knowledgeTypeList;
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
    @PostMapping("/delete/{id}")
    @RequiresPermissions("knowledge:type:delete")
    public R delete(@PathVariable Long id){

        //判断是否有子类型
        List<KnowledgeTypeEntity> list = knowledgeTypeService.queryListParentId(id);
        if(list.size()>0){
            return R.error("请先删除该类下的子类型");
        }
        knowledgeTypeService.deleteEntity(id);


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

    /**
     * 查找除了主题的所有类型信息
     * @param params
     * @return
     */
    @RequestMapping("/allInfobuttheme")
    public R allInfobuttheme(){
        //查询列表数据
        List<KnowledgeTypeEntity> list=knowledgeTypeService.queryAllButTheme();
        return R.ok().put("allTypes", list);
    }

    @GetMapping("/select")
    public R select(){
        List<KnowledgeTypeEntity> queryAll = knowledgeTypeService.queryAll();

        //添加顶级菜单
        KnowledgeTypeEntity root=new KnowledgeTypeEntity();
        root.setTypeName("根节点");
        root.setId(0L);
        root.setParentId(-1L);
        queryAll.add(root);
        return R.ok().put("typeList", queryAll);
    }

}
