package com.goods.business.service;

import com.goods.common.vo.business.ProductVO;
import com.goods.common.vo.system.PageVO;

import java.util.List;

/**
 * @Title :物资物料
 * @Author Ghia
 * @Create 2022-12-25 22:14
 **/
public interface ProductService {
    /**
     * 查询物资
     * @param pageNum
     * @param pageSize
     * @param name
     * @param categorys
     * @param status
     * @return
     */
    PageVO<ProductVO> findProductList(Integer pageNum, Integer pageSize, String name, List categorys, Integer status);

    /**
     * 添加物资
     * @param productVO
     * @return
     */
    Integer add(ProductVO productVO);

    /**
     * 查询物资
     * @param id
     * @return
     */
    ProductVO edit(Long id);

    /**
     * 修改物资
     * @param productVO
     * @return
     */
    Integer update(ProductVO productVO);

    /**
     * 删除物资
     * @param id
     * @return
     */
    Integer remove(Long id);
}
