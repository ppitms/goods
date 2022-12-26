package com.goods.controller.business;

import com.goods.business.service.InStockService;
import com.goods.common.model.business.InStock;
import com.goods.common.model.business.InStockInfo;
import com.goods.common.model.business.Supplier;
import com.goods.common.response.ResponseBean;
import com.goods.common.vo.business.InStockDetailVO;
import com.goods.common.vo.business.InStockItemVO;
import com.goods.common.vo.business.InStockVO;
import com.goods.common.vo.system.PageVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title :入库记录
 * @Author Ghia
 * @Create 2022-12-26 15:18
 **/
@Api(tags = "业务管理-入库记录接口")
@RestController
@RequestMapping("business/inStock")
public class InStockController {

    @Autowired
    private InStockService inStockService;

    //http://www.localhost:8989/business/inStock/findInStockList?pageNum=1&pageSize=10&status=0
    //分页查询入库列表
    @GetMapping("findInStockList")
    public ResponseBean findInStockList(@RequestParam(value = "pageNum",required = false)Integer pageNum,
                                        @RequestParam(value = "pageSize",required = false)Integer pageSize,
                                        @RequestParam(value = "status")Integer status){
        //调用业务层方法，分页查询入库列表
        PageVO<InStockVO> inStockVOList= inStockService.findInStockList(pageNum,pageSize,status);
        return ResponseBean.success(inStockVOList);
    }

    //http://www.localhost:8989/business/inStock/detail/117?pageNum=1
    //入库明细
    @GetMapping("detail/{id}")
    public ResponseBean detail(@PathVariable("id") Long id){
        //调用业务层方法，查询入库明细
        InStockDetailVO inStockDetailVO=inStockService.detail(id);
        if(inStockDetailVO==null)return ResponseBean.error("数据异常！");
        return ResponseBean.success(inStockDetailVO);
    }

    //http://www.localhost:8989/business/inStock/addIntoStock
    //新增入库
//    @PostMapping("addIntoStock")
//    public ResponseBean addIntoStock(@RequestBody InStockVO){
//
//    }
}
