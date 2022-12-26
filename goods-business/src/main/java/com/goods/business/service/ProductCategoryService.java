package com.goods.business.service;

import com.goods.common.model.business.ProductCategory;
import com.goods.common.vo.business.ProductCategoryTreeNodeVO;
import com.goods.common.vo.business.ProductCategoryVO;
import com.goods.common.vo.system.PageVO;

import java.util.List;

/**
 * @Title :物资类别
 * @Author Ghia
 * @Create 2022-12-24 0:08
 **/
public interface ProductCategoryService {

    /**
     * 分页获取物资类别列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<ProductCategoryTreeNodeVO> categoryTree(Integer pageNum, Integer pageSize);

    /**
     * 获取物资类别列表
     * @return
     */
    List<ProductCategoryTreeNodeVO> getCategoryTree();

    /**
     * 保存物资类别
     * @param productCategoryVO
     */
    Integer add(ProductCategoryVO productCategoryVO);

    /**
     * 查询类别详情
     * @param id
     * @return
     */
    ProductCategoryVO edit(Long id);

    /**
     * 修改类别
     * @param productCategoryVO
     */
    Integer update(ProductCategoryVO productCategoryVO);

    /**
     * 删除类别
     * @param id
     */
    Integer delete(Long id);
}
