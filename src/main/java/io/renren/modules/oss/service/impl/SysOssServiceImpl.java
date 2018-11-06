/**
 * Copyright 2018 人人开源 http://www.renren.io
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.renren.modules.oss.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.oss.dao.SysOssDao;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;


@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Long contentID =new Long(-1);
		if(!StringUtils.isEmpty((String) params.get("contentID"))){
			contentID = Long.parseLong((String) params.get("contentID"));
		}
		Page<SysOssEntity> page = this.selectPage(
				new Query<SysOssEntity>(params).getPage(),
				new EntityWrapper<SysOssEntity>()
						.eq("content_id", contentID)
		);
		return new PageUtils(page);
	}

	@Override
	public void uploadFileUpdate(Long id, String url) {
		SysOssEntity entity = this.selectOne(
				new EntityWrapper<SysOssEntity>()
						.eq("url", url)
		);
		entity.setContentId(id);
		this.updateById(entity);
	}

	@Override
	public void deleteByContentId(Long id,String fileFolder){
		List<SysOssEntity> fileList = this.selectList(
				new EntityWrapper<SysOssEntity>()
						.eq("content_id", id)
		);
		//删除文件
		for (SysOssEntity file : fileList) {
			File fileToDelete = new File(fileFolder + file.getUrl());
			if (fileToDelete.exists()) {
				fileToDelete.delete();
			}
		}
		//删除数据库记录 这样比一条一条根据id删除要好
		this.delete(new EntityWrapper<SysOssEntity>().eq("content_id",id));
	}
}

