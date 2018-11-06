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

package io.renren.modules.oss.controller;

import io.renren.common.exception.RRException;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 文件上传
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-25 12:13:26
 */
@Slf4j
@RestController
@RequestMapping("sys/oss")
public class SysOssController {
	@Autowired
	private SysOssService sysOssService;
	@Autowired
	private SysConfigService sysConfigService;

	@GetMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysOssService.queryPage(params);

		return R.ok().put("page", page);
	}

	@GetMapping("/listForUser")
	public R listForUser(@RequestParam Map<String, Object> params){
		PageUtils page = sysOssService.queryPage(params);

		return R.ok().put("page", page);
	}
	/**
	 * 上传文件
	 */
	@PostMapping("/upload")
	public R upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
		if (file.isEmpty()) {
			throw new RRException("上传文件不能为空");
		}

		// 上传文件
		// 写入文件 文件目录是按照配置文件夹和日期格式设置
		String fileFolder= sysConfigService.getValue("FILE_FOLDER")+"//"+DateUtils.format(new Date(),"yyyyMMdd");
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String fileName=file.getOriginalFilename();
		//创建目录
		File filePathFile=new File(fileFolder);
		if(!filePathFile.exists()){
			filePathFile.mkdirs();
		}

		// 进行判断文件是否存在，存在的话给文件名称后面加当前分和秒避免重复
		File fileExist=new File(fileFolder+"//"+fileName);
		if(fileExist.exists()){
			fileName=fileName.substring(0,fileName.lastIndexOf(suffix))+DateUtils.format(new Date(),"mmss")+suffix;
		}

		Path path =Paths.get(fileFolder,fileName);
		byte[] bytes = file.getBytes();
		Files.write(path, bytes);

		//保存文件信息
		String url="//"+DateUtils.format(new Date(),"yyyyMMdd")+"//"+fileName;
		SysOssEntity ossEntity = new SysOssEntity();
		ossEntity.setUrl(url);
		ossEntity.setCreateDate(new Date());
		ossEntity.setSuffix(suffix);
		ossEntity.setName(fileName);
		ossEntity.setContentId(new Long(-1));
		sysOssService.insert(ossEntity);

		return R.ok().put("url", url);
	}

	@GetMapping("/uploadFileUpdate")
	public R uploadFileUpdate(@RequestParam Map<String, Object> params){
		Long id=Long.parseLong((String) params.get("id"));
		String url=(String)params.get("url");
		sysOssService.uploadFileUpdate(id,url);
		return R.ok();
	}
	/**
	 * 下载文件
	 * @param id
	 * @param response
	 * @return R
	 */
	@GetMapping("/download/{id}")
	public void downloadFile(@PathVariable("id") Long id, HttpServletResponse response) throws UnsupportedEncodingException {
		SysOssEntity entity=sysOssService.selectById(id);
		String fileUrl=entity.getUrl();
		String fileName=entity.getName();
		String filePath=sysConfigService.getValue("FILE_FOLDER")+"//"+fileUrl;

		File file=new File(filePath);

		if(file.exists()){
			// 设置下载不打开 // 设置文件名
			response.setHeader("Access-Control-Expose-Headers","content-disposition");
			response.setHeader("content-type", "application/octet-stream");
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("content-disposition", "attachment;filename=" +URLEncoder.encode(fileName, "UTF8"));
			byte[] buffer = new byte[1024];
			FileInputStream fis;
			BufferedInputStream bis;
			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer, 0, i);
					os.flush();
					i = bis.read(buffer);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public R delete(@RequestBody Long[] ids){
		String fileFolder= sysConfigService.getValue("FILE_FOLDER");
		for (Long id:ids) {
			String fileName=sysOssService.selectById(id).getUrl();
			File fileToDelete=new File(fileFolder+fileName);
			if(fileToDelete.exists()){
				fileToDelete.delete();
			}
		}
		sysOssService.deleteBatchIds(Arrays.asList(ids));

		return R.ok();
	}
}
