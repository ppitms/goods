package com.goods.business.service.imp;

import com.goods.business.mapper.SupplierMapper;
import com.goods.business.service.SupplierService;
import com.goods.common.model.business.Supplier;
import com.goods.common.utils.ListPageUtils;
import com.goods.common.vo.business.SupplierVO;
import com.goods.common.vo.system.PageVO;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title :物资来源
 * @Author Ghia
 * @Create 2022-12-25 17:08
 **/
@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;

    //分页获取物资类别列表
    @Override
    public PageVO<SupplierVO> findSupplierList(Integer pageNum, Integer pageSize,
                                               String name, String address, String contact) {
        //根据条件查询物资来源
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(name)){
            criteria.andLike("name","%"+name+"%");
        }
        if(!StringUtils.isEmpty(address)){
            criteria.andLike("address","%"+address+"%");
        }
        if(!StringUtils.isEmpty(contact)){
            criteria.andLike("contact","%"+contact+"%");
        }
        List<Supplier> supplierList = supplierMapper.selectByExample(example);

        //判空
        if(CollectionUtils.isEmpty(supplierList))return null;

        //遍历，复制属性值
        List<SupplierVO> supplierVOList = supplierList.stream().map(supplier -> {
            SupplierVO supplierVO = new SupplierVO();
            BeanUtils.copyProperties(supplier, supplierVO);
            return supplierVO;
        }).collect(Collectors.toList());

        //分页
        if(pageNum==null||pageSize==null){
            return new PageVO<>(supplierVOList.size(),supplierVOList);
        }
        return new PageVO<>(supplierVOList.size(), ListPageUtils.page(supplierVOList,pageSize,pageNum));
    }

    //查询来源详情
    @Override
    public SupplierVO edit(Long id) {
        //查询
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier=supplierMapper.selectOne(supplier);
        //属性值复制
        SupplierVO supplierVO = new SupplierVO();
        BeanUtils.copyProperties(supplier,supplierVO);
        return supplierVO;
    }

    @Override
    public Integer update(SupplierVO supplierVO) {
        //修改时间
        supplierVO.setModifiedTime(new Date());
        //属性值复制
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierVO,supplier);
        //依据主键修改
        return supplierMapper.updateByPrimaryKey(supplier);
    }

    //添加来源信息
    @Override
    public Integer add(SupplierVO supplierVO) {
        //设置创建时间和修改时间
        supplierVO.setModifiedTime(new Date());
        supplierVO.setCreateTime(new Date());
        //属性值复制
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierVO,supplier);
        //添加
        return supplierMapper.insert(supplier);
    }

    //删除来源
    @Override
    public Integer delete(Long id) {
        return supplierMapper.deleteByPrimaryKey(id);
    }
}
