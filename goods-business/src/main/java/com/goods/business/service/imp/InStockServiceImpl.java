package com.goods.business.service.imp;

import com.goods.business.mapper.InStockMapper;
import com.goods.business.service.InStockService;
import com.goods.common.model.business.InStock;
import com.goods.common.utils.ListPageUtils;
import com.goods.common.vo.business.InStockDetailVO;
import com.goods.common.vo.business.InStockVO;
import com.goods.common.vo.system.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title :入库记录
 * @Author Ghia
 * @Create 2022-12-26 15:32
 **/
@Service
public class InStockServiceImpl implements InStockService {

    @Autowired
    private InStockMapper inStockMapper;

    //分页查询入库列表
    @Override
    public PageVO<InStockVO> findInStockList(Integer pageNum, Integer pageSize, Integer status) {
        //自定义查询
        List<InStockVO> inStockVOList = inStockMapper.findInStockList(status);
        //判空
        if(CollectionUtils.isEmpty(inStockVOList)){
            return null;
        }

        //分页
        if(pageNum==null||pageSize==null){
            return new PageVO<>(inStockVOList.size(),inStockVOList);
        }
        return new PageVO<>(inStockVOList.size(), ListPageUtils.page(inStockVOList,pageSize,pageNum));
    }

    //查询入库明细
    @Override
    public InStockDetailVO detail(Long id) {
        InStockDetailVO detail = inStockMapper.detail(id);
        detail.setTotal(detail.getItemVOS().size());
        return detail;
    }
}
