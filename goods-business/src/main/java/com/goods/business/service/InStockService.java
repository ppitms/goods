package com.goods.business.service;

import com.goods.common.vo.business.InStockDetailVO;
import com.goods.common.vo.business.InStockVO;
import com.goods.common.vo.system.PageVO;

import java.util.List;

/**
 * @Title :入库记录
 * @Author Ghia
 * @Create 2022-12-26 15:32
 **/
public interface InStockService {
    /**
     * 分页查询入库列表
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    PageVO<InStockVO> findInStockList(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 查询入库明细
     * @param id
     * @return
     */
    InStockDetailVO detail(Long id);
}
