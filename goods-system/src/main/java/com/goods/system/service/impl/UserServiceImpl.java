package com.goods.system.service.impl;

import com.goods.common.enums.system.UserStatusEnum;
import com.goods.common.enums.system.UserTypeEnum;
import com.goods.common.error.SystemCodeEnum;
import com.goods.common.error.SystemException;
import com.goods.common.model.system.*;
import com.goods.common.response.ActiveUser;
import com.goods.common.utils.JWTUtils;
import com.goods.common.utils.MenuTreeBuilder;
import com.goods.common.vo.business.ProductVO;
import com.goods.common.vo.business.SupplierVO;
import com.goods.system.converter.MenuConverter;
import com.goods.system.converter.UserConverter;
import com.goods.system.mapper.*;
import com.goods.system.mapper.*;
import com.goods.system.service.DepartmentService;
import com.goods.system.service.UserService;
import com.goods.system.shiro.JWTToken;
import com.goods.system.util.MD5Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.goods.common.vo.system.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DepartmentService departmentService;

    /**
     * ????????????
     * @param name ?????????
     * @return
     */
    @Override
    public User findUserByName(String name) {
        User t = new User();
        t.setUsername(name);
        return userMapper.selectOne(t);
    }

    /**
     * ??????????????????
     * @param id ??????ID
     * @return
     */
    @Override
    public List<Role> findRolesById(Long id) throws SystemException {
        User dbUser = userMapper.selectByPrimaryKey(id);
        if(dbUser==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"??????????????????");
        }
        List<Role> roles=new ArrayList<>();
        UserRole t = new UserRole();
        t.setUserId(dbUser.getId());
        List<UserRole> userRoleList = userRoleMapper.select(t);
        List<Long> rids=new ArrayList<>();
        if(!CollectionUtils.isEmpty(userRoleList)){
            for (UserRole userRole : userRoleList) {
                rids.add(userRole.getRoleId());
            }
            if(!CollectionUtils.isEmpty(rids)){
                for (Long rid : rids) {
                    Role role = roleMapper.selectByPrimaryKey(rid);
                    if(role!=null){
                        roles.add(role);
                    }
                }
            }
        }
        return roles;
    }

    /**
     * ????????????
     * @param roles ???????????????
     * @return
     */
    @Override
    public List<Menu> findMenuByRoles(List<Role> roles) {
        List<Menu> menus=new ArrayList<>();
        if(!CollectionUtils.isEmpty(roles)){
            Set<Long> menuIds=new HashSet<>();//?????????????????????id
            List<RoleMenu> roleMenus;
            for (Role role : roles) {
                //????????????ID????????????ID
                Example o = new Example(RoleMenu.class);
                o.createCriteria().andEqualTo("roleId",role.getId());
                roleMenus= roleMenuMapper.selectByExample(o);
                if(!CollectionUtils.isEmpty(roleMenus)){
                    for (RoleMenu roleMenu : roleMenus) {
                        menuIds.add(roleMenu.getMenuId());
                    }
                }
            }
            if(!CollectionUtils.isEmpty(menuIds)){
                for (Long menuId : menuIds) {
                    //????????????????????????
                    Menu menu = menuMapper.selectByPrimaryKey(menuId);
                    if(menu!=null){
                        menus.add(menu);
                    }
                }
            }
        }
        return menus;
    }

    /**
     * ????????????
     * @return
     */
    @Override
    public List<MenuNodeVO> findMenu() {
        List<Menu> menus=null;
        List<MenuNodeVO> menuNodeVOS=new ArrayList<>();
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        if(activeUser.getUser().getType()== UserTypeEnum.SYSTEM_ADMIN.getTypeCode()){
            //???????????????
            menus=menuMapper.selectAll();
        }else if(activeUser.getUser().getType()== UserTypeEnum.SYSTEM_USER.getTypeCode()){
            //??????????????????
            menus= activeUser.getMenus();
        }
        if(!CollectionUtils.isEmpty(menus)){
            menuNodeVOS= MenuConverter.converterToMenuNodeVO(menus);
        }
        //??????????????????
        return MenuTreeBuilder.build(menuNodeVOS);
    }

    /**
     * ????????????
     * @param userVO
     * @return
     */
    @Override
    public PageVO<UserVO> findUserList(Integer pageNum, Integer pageSize, UserVO userVO) {
        //???????????????
        PageHelper.startPage(pageNum,pageSize);
        Example o = new Example(User.class);
        String username = userVO.getUsername();
        String nickname = userVO.getNickname();
        Long departmentId = userVO.getDepartmentId();
        Integer sex = userVO.getSex();
        String email = userVO.getEmail();
        Example.Criteria criteria = o.createCriteria();
        if(username!=null&&!"".equals(username)){
            criteria.andLike("username","%"+username+"%");
        }
        if(nickname!=null&&!"".equals(nickname)){
            criteria.andLike("nickname","%"+nickname+"%");
        }
        if(email!=null&&!"".equals(email)){
            criteria.andLike("email","%"+email+"%");
        }
        if(sex!=null){
            criteria.andEqualTo("sex",sex);
        }
        if(departmentId!=null){
            criteria.andEqualTo("departmentId",departmentId);
        }

        criteria.andNotEqualTo("type",0);
        List<User> userList = userMapper.selectByExample(o);
        List<UserVO> userVOS = userConverter.converterToUserVOList(userList);
        PageInfo<User> info=new PageInfo<>(userList);
        return new PageVO<>(info.getTotal(),userVOS);
    }

    /**
     * ????????????
     * @param id ??????ID
     */
    @Transactional
    @Override
    public void deleteById(Long id) throws SystemException {
        User user = userMapper.selectByPrimaryKey(id);
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();

        if(user==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"???????????????????????????");
        }

        if(user.getId().equals(activeUser.getUser().getId())){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"??????????????????????????????");
        }

        userMapper.deleteByPrimaryKey(id);
        //????????????[??????-??????]??????
        Example o = new Example(UserRole.class);
        o.createCriteria().andEqualTo("userId",id);
        userRoleMapper.deleteByExample(o);
    }

    /**
     * ????????????????????????
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(Long id, Boolean status) throws SystemException {
        User dbUser = userMapper.selectByPrimaryKey(id);
        if(dbUser==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"?????????????????????????????????");
        }
        ActiveUser activeUser= (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        if(dbUser.getId().equals(activeUser.getUser().getId())){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"??????????????????????????????");
        }else {
            User t = new User();
            t.setId(id);
            t.setStatus(status? UserStatusEnum.DISABLE.getStatusCode() :
                    UserStatusEnum.AVAILABLE.getStatusCode());
            userMapper.updateByPrimaryKeySelective(t);
        }
    }

    /**
     * ????????????
     * @param userVO
     */
    @Transactional
    @Override
    public void add(UserVO userVO) throws SystemException {
        @NotBlank(message = "?????????????????????") String username = userVO.getUsername();
        @NotNull(message = "??????id????????????") Long departmentId = userVO.getDepartmentId();
        Example o = new Example(User.class);
        o.createCriteria().andEqualTo("username",username);
        int i = userMapper.selectCountByExample(o);
        if(i!=0){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"????????????????????????");
        }
        Department department = departmentMapper.selectByPrimaryKey(departmentId);
        if(department==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"??????????????????");
        }
        User user = new User();
        BeanUtils.copyProperties(userVO,user);
        String salt=UUID.randomUUID().toString().substring(0,32);
        user.setPassword(MD5Utils.md5Encryption(user.getPassword(), salt));
        user.setModifiedTime(new Date());
        user.setCreateTime(new Date());
        user.setSalt(salt);
        user.setType(UserTypeEnum.SYSTEM_USER.getTypeCode());//????????????????????????????????????
        user.setStatus(UserStatusEnum.AVAILABLE.getStatusCode());//???????????????????????????
        user.setAvatar("http://badidol.com/uploads/images/avatars/201910/24/18_1571921832_HG9E55x9NY.jpg");
        userMapper.insert(user);
    }

    /**
     * ??????
     * @param id
     * @param userVO
     */
    @Transactional
    @Override
    public void update(Long id, UserEditVO userVO) throws SystemException {
        User dbUser = userMapper.selectByPrimaryKey(id);
        @NotBlank(message = "?????????????????????") String username = userVO.getUsername();
        @NotNull(message = "??????????????????") Long departmentId = userVO.getDepartmentId();
        if(dbUser==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"???????????????????????????");
        }
        Department department = departmentMapper.selectByPrimaryKey(departmentId);
        if(department==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"??????????????????");
        }
        Example o = new Example(User.class);
        o.createCriteria().andEqualTo("username",username);
        List<User> users = userMapper.selectByExample(o);
        if(!CollectionUtils.isEmpty(users)){
            if(!users.get(0).getId().equals(id)){
                throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"????????????????????????");
            }
        }
        User user = new User();
        BeanUtils.copyProperties(userVO,user);
        user.setModifiedTime(new Date());
        user.setId(dbUser.getId());
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * ??????
     * @param id
     * @return
     */
    @Transactional
    @Override
    public UserEditVO edit(Long id) throws SystemException {
        User user = userMapper.selectByPrimaryKey(id);
        if(user==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"???????????????????????????");
        }
        UserEditVO userEditVO = new UserEditVO();
        BeanUtils.copyProperties(user,userEditVO);
        Department department = departmentMapper.selectByPrimaryKey(user.getDepartmentId());
        if(department!=null){
            userEditVO.setDepartmentId(department.getId());
        }
        return userEditVO;
    }

    /**
     * ?????????????????????ID
     * @param id ??????id
     * @return
     */
    @Transactional
    @Override
    public List<Long> roles(Long id) throws SystemException {
        User user = userMapper.selectByPrimaryKey(id);
        if(user==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"??????????????????");
        }
        Example o = new Example(UserRole.class);
        o.createCriteria().andEqualTo("userId",user.getId());
        List<UserRole> userRoleList = userRoleMapper.selectByExample(o);
        List<Long> roleIds=new ArrayList<>();
        if(!CollectionUtils.isEmpty(userRoleList)){
            for (UserRole userRole : userRoleList) {
                Role role = roleMapper.selectByPrimaryKey(userRole.getRoleId());
                if(role!=null){
                    roleIds.add(role.getId());
                }
            }
        }
        return roleIds;
    }

    /**
     * ????????????
     * @param id ??????id
     * @param rids ????????????
     */
    @Override
    @Transactional
    public void assignRoles(Long id, Long[] rids) throws SystemException {
        //?????????????????????????????????
        User user = userMapper.selectByPrimaryKey(id);
        if(user==null){
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"???????????????");
        }
        //?????????????????????
        Example o = new Example(UserRole.class);
        o.createCriteria().andEqualTo("userId",user.getId());
        userRoleMapper.deleteByExample(o);
        //?????????????????????
        if(rids.length>0){
            for (Long rid : rids) {
                Role role = roleMapper.selectByPrimaryKey(rid);
                if(role==null){
                    throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"roleId="+rid+",??????????????????");
                }
                //??????????????????
                if(role.getStatus()==0){
                    throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"roleName="+role.getRoleName()+",??????????????????");
                }
                UserRole userRole = new UserRole();
                userRole.setUserId(id);
                userRole.setRoleId(rid);
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectAll();
    }

    /**
     * ????????????
     * @param username
     * @param password
     * @return
     */
    @Override
    public String login(String username, String password) throws SystemException {
        String token;
        User user = findUserByName(username);
        if (user != null) {
            String salt = user.getSalt();
            //????????????
            String target = MD5Utils.md5Encryption(password, salt);
            //??????Token
            token = JWTUtils.sign(username, target);
            JWTToken jwtToken = new JWTToken(token);
            try {
                SecurityUtils.getSubject().login(jwtToken);
            } catch (AuthenticationException e) {
                throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,e.getMessage());
            }
        } else {
            throw new SystemException(SystemCodeEnum.PARAMETER_ERROR,"???????????????");
        }
        return token;
    }

    /**
     * ????????????
     *
     * @return
     */
    @Override
    public UserInfoVO info() throws SystemException {
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setAvatar(activeUser.getUser().getAvatar());
        userInfoVO.setUsername(activeUser.getUser().getUsername());
        userInfoVO.setUrl(activeUser.getUrls());
        userInfoVO.setNickname(activeUser.getUser().getNickname());
        List<String> roleNames = activeUser.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
        userInfoVO.setRoles(roleNames);
        userInfoVO.setPerms(activeUser.getPermissions());
        userInfoVO.setIsAdmin(activeUser.getUser().getType()==UserTypeEnum.SYSTEM_ADMIN.getTypeCode());
        DepartmentVO dept = departmentService.edit(activeUser.getUser().getDepartmentId());
        if(dept!=null){
            userInfoVO.setDepartment(dept.getName());
        }
        return userInfoVO;
    }
}
