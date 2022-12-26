package com.goods.business.service.imp;

import com.goods.business.mapper.ProductCategoryMapper;
import com.goods.business.service.ProductCategoryService;
import com.goods.common.model.business.Product;
import com.goods.common.model.business.ProductCategory;
import com.goods.common.utils.ListPageUtils;
import com.goods.common.vo.business.ProductCategoryTreeNodeVO;
import com.goods.common.vo.business.ProductCategoryVO;
import com.goods.common.vo.system.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title :物资类别
 * @Author Ghia
 * @Create 2022-12-24 0:09
 **/
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    //分页获取物资类别列表
    @Override
    public PageVO<ProductCategoryTreeNodeVO> categoryTree(Integer pageNum, Integer pageSize){
        //获取物资类别列表
        List<ProductCategoryTreeNodeVO> rootMenu = getCategoryTree();

        //分页
        if(pageNum==null||pageSize==null){
            return new PageVO<>(rootMenu.size(),rootMenu);
        }
        return new PageVO<>(rootMenu.size(), ListPageUtils.page(rootMenu,pageSize,pageNum));
    }

    //获取物资类别列表
    public List<ProductCategoryTreeNodeVO> getCategoryTree(){
        //查询所有物资类别列表
        List<ProductCategory> productCategories = productCategoryMapper.selectAll();
        if (CollectionUtils.isEmpty(productCategories)){
            return null;
        }
        //遍历，将productCategory集合封装成productCategoryTreeNodeVO集合
        List<ProductCategoryTreeNodeVO> productCategoryTreeNodeVOList = productCategories.stream().map(productCategory -> {
            ProductCategoryTreeNodeVO productCategoryTreeNodeVO = new ProductCategoryTreeNodeVO();
            BeanUtils.copyProperties(productCategory, productCategoryTreeNodeVO);
            return productCategoryTreeNodeVO;
        }).collect(Collectors.toList());
        //遍历出根节点的集合
        List<ProductCategoryTreeNodeVO> rootMenu = productCategoryTreeNodeVOList.stream().filter(productCategoryTreeNodeVO -> productCategoryTreeNodeVO.getPid() == 0).collect(Collectors.toList());
        //排序
        Collections.sort(rootMenu,ProductCategoryTreeNodeVO.order());
        //遍历根节点
        for (ProductCategoryTreeNodeVO nav : rootMenu) {
            //设置为一级分类
            nav.setLev(1);
            //获取子节点
            List<ProductCategoryTreeNodeVO> child = getChild(nav, productCategoryTreeNodeVOList);
            nav.setChildren(child);
        }
        return rootMenu;
    }

    //获取子集合
    private List<ProductCategoryTreeNodeVO> getChild(ProductCategoryTreeNodeVO vo,List<ProductCategoryTreeNodeVO> list){
        List<ProductCategoryTreeNodeVO> childList = list.stream().filter(productCategoryTreeNodeVO -> productCategoryTreeNodeVO.getPid() == vo.getId()).map(productCategoryTreeNodeVO -> {
            productCategoryTreeNodeVO.setLev(vo.getLev()+1);
            return productCategoryTreeNodeVO;
        }).collect(Collectors.toList());
        //递归
        for (ProductCategoryTreeNodeVO nav : childList) {
            nav.setChildren(getChild(nav,list));
        }
        //排序
        Collections.sort(childList,ProductCategoryTreeNodeVO.order());
        //无子节点，递归退出
        if(childList.size()==0){
            return null;
        }
        return childList;
    }

    //保存物资类别
    @Override
    public Integer add(ProductCategoryVO productCategoryVO) {
        //设置创建时间和修改时间
        productCategoryVO.setCreateTime(new Date());
        productCategoryVO.setModifiedTime(new Date());
        ProductCategory productCategory = new ProductCategory();
        //属性值复制
        BeanUtils.copyProperties(productCategoryVO,productCategory);
        //保存
        return productCategoryMapper.insert(productCategory);
    }

    //查询类别详情
    @Override
    public ProductCategoryVO edit(Long id) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(id);
        //查询
        productCategory = productCategoryMapper.selectOne(productCategory);
        //属性值复制
        ProductCategoryVO productCategoryVO = new ProductCategoryVO();
        BeanUtils.copyProperties(productCategory,productCategoryVO);
        return productCategoryVO;
    }

    //修改类别
    @Override
    public Integer update(ProductCategoryVO productCategoryVO) {
        //修改时间
        productCategoryVO.setModifiedTime(new Date());
        //属性值复制
        ProductCategory productCategory = new ProductCategory();
        BeanUtils.copyProperties(productCategoryVO,productCategory);
        //修改
        return productCategoryMapper.updateByPrimaryKey(productCategory);
    }

    //删除类别
    @Override
    public Integer delete(Long id) {
        //查询是否有子类别
        Example example = new Example(ProductCategory.class);
        example.createCriteria().andEqualTo("pid",id);
        List<ProductCategory> productCategories = productCategoryMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(productCategories)&&productCategories.size()>0)return -1;
        return productCategoryMapper.deleteByPrimaryKey(id);
    }
}
