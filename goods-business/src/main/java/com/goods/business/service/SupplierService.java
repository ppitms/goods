package com.goods.business.service;

import com.goods.common.vo.business.SupplierVO;
import com.goods.common.vo.system.PageVO;

/**
 * @Title :物资来源
 * @Author Ghia
 * @Create 2022-12-25 17:07
 **/
public interface SupplierService {
    /**
     * 分页获取物资类别列表
     * @param pageNum
     * @param pageSize
     * @param name
     * @param address
     * @param contact
     * @return
     */
    PageVO<SupplierVO> findSupplierList(Integer pageNum, Integer pageSize, String name, String address, String contact);

    /**
     * 查询来源详情
     * @param id
     * @return
     */
    SupplierVO edit(Long id);

    /**
     * 修改来源信息
     * @param supplierVO
     * @return
     */
    Integer update(SupplierVO supplierVO);

    /**
     * 添加来源信息
     * @param supplierVO
     * @return
     */
    Integer add(SupplierVO supplierVO);

    /**
     * 删除来源
     * @param id
     * @return
     */
    Integer delete(Long id);
}
