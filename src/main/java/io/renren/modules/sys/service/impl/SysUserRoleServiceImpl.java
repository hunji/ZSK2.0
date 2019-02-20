package io.renren.modules.sys.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.renren.common.utils.MapUtils;
import io.renren.modules.knowledge.dto.UserInfo;
import io.renren.modules.knowledge.websocket.WsUserRepository;
import io.renren.modules.sys.dao.SysUserRoleDao;
import io.renren.modules.sys.entity.SysUserRoleEntity;
import io.renren.modules.sys.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * 用户与角色对应关系
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:45:48
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRoleEntity> implements SysUserRoleService {

	@Override
	public void saveOrUpdate(Long userId, List<Long> roleIdList) {
		//先删除用户与角色关系
		this.deleteByMap(new MapUtils().put("user_id", userId));

		if(roleIdList == null || roleIdList.size() == 0){
			return ;
		}

		//保存用户与角色关系
		List<SysUserRoleEntity> list = new ArrayList<>(roleIdList.size());
		for(Long roleId : roleIdList){
			SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
			sysUserRoleEntity.setUserId(userId);
			sysUserRoleEntity.setRoleId(roleId);

			list.add(sysUserRoleEntity);
		}
		this.insertBatch(list);
	}

	@Override
	public List<Long> queryRoleIdList(Long userId) {
		return baseMapper.queryRoleIdList(userId);
	}

	@Override
	public int deleteBatch(Long[] roleIds){
		return baseMapper.deleteBatch(roleIds);
	}

	@Override
	public List<String> queryRoleByUserName(String userName) {
		return baseMapper.queryRoleByUserName(userName);
	}

	@Override
	public List<UserInfo> queryAdminUser() {
		List<String> list = baseMapper.queryAdminUserName();
		return generateUserInfo(list);
	}

	@Override
	public List<UserInfo> queryGuestUser() {
		List<String> list = new ArrayList<>();
		WsUserRepository.guestUsers.forEach(list::add);
		return generateUserInfo(list);
	}

	/**
	 * 生成用户信息
	 * 包含，用户名称、是否在线等
	 * @param 用户名称
	 * @return
	 */
	private List<UserInfo> generateUserInfo(List<String> list) {
		ArrayList<UserInfo> returnList= new ArrayList<>();
		for (String name: list) {
			UserInfo userInfo = new UserInfo(name);
			if(WsUserRepository.guestUsers.stream().anyMatch(n ->n.equals(name))){
				userInfo.setIsOnline(true);
			}
			if(WsUserRepository.adminUsers.stream().anyMatch(n ->n.equals(name))){
				userInfo.setIsOnline(true);
			}
			returnList.add(userInfo);
		}
		// 进行了排序把未在线的放在后面在线的放在前面
		Collections.sort(returnList, (o1, o2) -> Boolean.compare(o1.getIsOnline(),o2.getIsOnline()));
		Collections.reverse(returnList);
		return returnList;
	}
}
