package com.goods.business.mapper;

import com.goods.common.model.business.InStock;
import com.goods.common.model.business.Product;
import com.goods.common.model.business.Supplier;
import com.goods.common.vo.business.InStockDetailVO;
import com.goods.common.vo.business.InStockVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Title :入库记录
 * @Author Ghia
 * @Create 2022-12-26 15:34
 **/
public interface InStockMapper extends Mapper<InStock> {
    /**
     * 查询入库列表
     * @param status
     * @return
     */
    List<InStockVO> findInStockList(@Param("status") Integer status);

    /**
     * 查询入库明细
     * @param id
     * @return
     */
    InStockDetailVO detail(Long id);

}
