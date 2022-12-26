package com.goods.controller.business;

import com.goods.business.service.ProductService;
import com.goods.common.model.business.Product;
import com.goods.common.response.ResponseBean;
import com.goods.common.vo.business.ProductVO;
import com.goods.common.vo.system.PageVO;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title :物资物料
 * @Author Ghia
 * @Create 2022-12-25 21:52
 **/
@Api(tags = "业务管理-物资物料接口")
@RestController
@RequestMapping("business/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    //http://www.localhost:8989/business/product/findProductList?pageNum=1&pageSize=6&name=&categoryId=&supplier=&status=0
    //物资查询
    @GetMapping("findProductList")
    public ResponseBean findProductList(@RequestParam(value = "pageNum",required = false)Integer pageNum,
                                        @RequestParam(value = "pageSize",required = false)Integer pageSize,
                                        @RequestParam(value = "name",required = false)String name,
//                                        @RequestParam(value = "categoryId",required = false)Integer categoryId,
//                                        @RequestParam(value = "supplier",required = false)Integer supplier,
                                        @RequestParam(value = "categorys",required = false) List categorys,
                                        @RequestParam(value = "status")Integer status){
        //调用业务层方法，查询物资
        PageVO<ProductVO> productVOList= productService.findProductList(pageNum,pageSize,name,categorys,status);
        return ResponseBean.success(productVOList);
    }

    //http://www.localhost:8989/business/product/add
    //添加物资
    @PostMapping("add")
    public ResponseBean add(@RequestBody ProductVO productVO){
        //调用业务层方法，添加物资
        Integer addResult=productService.add(productVO);
        if(addResult!=1)return ResponseBean.error("添加失败");
        return ResponseBean.success();
    }

    //http://www.localhost:8989/business/product/edit/56
    //查询物资详情
    @GetMapping("edit/{id}")
    public ResponseBean edit(@PathVariable("id") Long id){
        //调用业务层方法，查询物资
        ProductVO productVO= productService.edit(id);
        if(null==productVO)return ResponseBean.error("查询失败！");
        return ResponseBean.success(productVO);
    }

    //http://localhost:8989/business/product/update/56
    //修改物资
    @PutMapping("update/*")
    public ResponseBean update(@RequestBody ProductVO productVO){
        //调用业务层方法，修改物资
        Integer updateResult=productService.update(productVO);
        if(updateResult!=1)return ResponseBean.error("修改失败！");
        return ResponseBean.success();
    }

    //http://www.localhost:8989/business/product/remove/56
    //删除物资
    @PutMapping("remove/{id}")
    public ResponseBean remove(@PathVariable("id") Long id){
        //调用业务层方法，删除物资
        Integer removeResult=productService.remove(id);
        if(removeResult!=1)return ResponseBean.error("修改失败！");
        return ResponseBean.success();
    }
}
