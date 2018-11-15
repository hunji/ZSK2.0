package io.renren.modules.knowledge.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.knowledge.dto.ESContentDTO;
import io.renren.modules.knowledge.entity.KnowledgeContentEntity;
import io.renren.modules.knowledge.search.ISearchService;
import io.renren.modules.knowledge.service.KnowledgeContentService;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.service.SysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/5
 */
@RestController
@RequestMapping("/knowledge/content")
public class KnowledgeContentController extends AbstractController {
    @Autowired
    private KnowledgeContentService contentService;

    /**
     * 这里是使用了本地的oss服务 替代使用云文件服务器的文件服务器
     */
    @Autowired
    private SysOssService sysOssService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private ISearchService searchService;

    @RequestMapping("/list")
    @RequiresPermissions("knowledge:content:list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询数据列表
        params.put("userId", getUserId());
        PageUtils page = contentService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 内容信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("knowledge:content:info")
    public R info(@PathVariable("id") Long id) {
        KnowledgeContentEntity knowledgeContentEntity = contentService.selectById(id);

        //浏览数增加的操作；增加后同步到es中
        contentService.addViewSum(id);
        return R.ok().put("knowledgeContent", knowledgeContentEntity);
    }

    @SysLog("新增知识库内容")
    @RequestMapping("/save")
    @RequiresPermissions("knowledge:content:save")
    public R save(@RequestBody KnowledgeContentEntity knowledgeContentEntity) {
        ValidatorUtils.validateEntity(knowledgeContentEntity);
        knowledgeContentEntity.setRank(0);
        knowledgeContentEntity.setCreateDate(new Date());
        knowledgeContentEntity.setUserId(this.getUserId());
        knowledgeContentEntity.setRstate(0);
        knowledgeContentEntity.setLikeNum(0L);
        contentService.save(knowledgeContentEntity);


        return R.ok();
    }

    @SysLog("修改知识库内容")
    @RequestMapping("/update")
    @RequiresPermissions("knowledge:content:update")
    public R update(@RequestBody KnowledgeContentEntity updateDtoEntity) {
        ValidatorUtils.validateEntity(updateDtoEntity);

        KnowledgeContentEntity entity = contentService.selectById(updateDtoEntity.getId());
        entity.setTypeId(updateDtoEntity.getTypeId());
        entity.setTitle(updateDtoEntity.getTitle());
        entity.setBrief(updateDtoEntity.getBrief());
        entity.setContent(updateDtoEntity.getContent());

        contentService.update(entity);

        return R.ok();
    }

    @SysLog("删除知识内容")
    @RequestMapping("/delete")
    @RequiresPermissions("knowledge:content:delete")
    public R delete(@RequestBody Long[] ids) {
        contentService.deleteBatch(ids);

        // 删除该内容下的文件列表
        String fileFolder = sysConfigService.getValue("FILE_FOLDER");
        for (Long id : ids) {
            sysOssService.deleteByContentId(id,fileFolder);
        }

        return R.ok();
    }

    @RequestMapping("/common")
    @RequiresPermissions("knowledge:content:common")
    public R common(@RequestParam Map<String, Object> params) {
        //查询公共知识库分页数据
        PageUtils page = contentService.commonContentPage(params);
        return R.ok().put("page", page);
    }

    @SysLog("审核知识内容")
    @RequestMapping("/review/{id}")
    @RequiresPermissions("knowledge:content:review")
    public R review(@PathVariable("id") Long id) {
        contentService.review(id);

        return R.ok();
    }

    @SysLog("返回重填")
    @RequestMapping("/sendBack/{id}")
    @RequiresPermissions("knowledge:content:sendBack")
    public R sendBack(@PathVariable("id") Long id) {
        contentService.sendback(id);

        return R.ok();
    }

    @RequestMapping("/search")
    public R search(@RequestParam Map<String, Object> params) {
        //查询公共知识库分页数据
        //添加了es 当有关键词的时候 使用es查询
        //es条件进行了修改，不只是关键词还增加了浏览数、点赞数和时间范围等
        ESContentDTO esContentDTO = searchService.queryIndex(params);
        return R.ok().put("esData",esContentDTO);
    }

    /**
     * search内容信息
     */
    @RequestMapping("/searchDetail/{id}")
    public R searchDetail(@PathVariable("id") Long id) {
        KnowledgeContentEntity knowledgeContentEntity = contentService.selectById(id);

        return R.ok().put("knowledgeContent", knowledgeContentEntity);
    }

    /**
     * 后台获取title列表
     */
    @RequestMapping("/search/titleForRemote/{key}")
    public R titleForRemote(@PathVariable("key") String key) {
        List<String> titleList = contentService.titleForRemote(key);
        return R.ok().put("titleList", titleList);
    }

    @RequestMapping("/search/addLikeSum/{id}")
    public R addLikeSum(@PathVariable("id") Long id) {
        contentService.addLikeSum(id);
        return R.ok();
    }

    @RequestMapping("/search/titleForRemoteAutoComplete")
    public List<String> titleForRemoteAutoComplete(@RequestParam String term) {
        List<String> titleList = contentService.titleForRemote(term);
        return titleList;
    }
}
