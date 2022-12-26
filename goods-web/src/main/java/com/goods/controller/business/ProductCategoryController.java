package com.goods.controller.business;

import com.goods.business.service.ProductCategoryService;
import com.goods.common.response.ResponseBean;
import com.goods.common.vo.business.ProductCategoryTreeNodeVO;
import com.goods.common.vo.business.ProductCategoryVO;
import com.goods.common.vo.system.PageVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @Title :物资类别
 * @Author Ghia
 * @Create 2022-12-23 23:57
 **/
@Api(tags = "业务管理-物资类别接口")
@RestController
@RequestMapping("business/productCategory")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    //http://www.localhost:8989/business/productCategory/categoryTree?pageNum=1&pageSize=5
    //分页获取物资类别列表
    @GetMapping("categoryTree")
    public ResponseBean categoryTree(@RequestParam(value = "pageNum",required = false) Integer pageNum,
                                     @RequestParam(value = "pageSize",required = false)Integer pageSize){
        //调用业务层方法，分页获取物资类别列表
        PageVO<ProductCategoryTreeNodeVO> productCategoryTreeNodeVOList= productCategoryService.categoryTree(pageNum,pageSize);
        return ResponseBean.success(productCategoryTreeNodeVOList);
    }

    //http://www.localhost:8989/business/productCategory/getParentCategoryTree
    //获取物资类别列表
    @GetMapping("getParentCategoryTree")
    public ResponseBean getParentCategoryTree(){
        //调用业务层方法，获取物资类别列表
        List<ProductCategoryTreeNodeVO> categoryTree = productCategoryService.getCategoryTree();
        return ResponseBean.success(categoryTree);
    }

    //http://www.localhost:8989/business/productCategory/add
    //添加物资类别
    @PostMapping("add")
    public ResponseBean add(@RequestBody ProductCategoryVO productCategoryVO){
        //调用业务层方法，保存物资类别
        Integer addResult=productCategoryService.add(productCategoryVO);
        if(addResult!=1)return ResponseBean.error("添加失败！");
        return ResponseBean.success();
    }

    //http://www.localhost:8989/business/productCategory/edit/85
    //查询类别详情
    @GetMapping("edit/{id}")
    public ResponseBean edit(@PathVariable("id") Long id){
        //调用业务层方法，查询类别详情
        ProductCategoryVO productCategoryVO= productCategoryService.edit(id);
        if (productCategoryVO==null){
            return ResponseBean.error("找不到数据");
        }
        return ResponseBean.success(productCategoryVO);
    }

    //http://www.localhost:8989/business/productCategory/update/83
    //修改类别
    @PutMapping("update/*")
    public ResponseBean update(@RequestBody ProductCategoryVO productCategoryVO){
        //调用业务层方法，修改类别
        Integer result=productCategoryService.update(productCategoryVO);
        if(result!=1){
            return ResponseBean.error("修改失败！");
        }
        return ResponseBean.success();
    }

    //http://www.localhost:8989/business/productCategory/delete/82
    //删除类别
    @DeleteMapping("delete/{id}")
    public ResponseBean delete(@PathVariable("id") Long id){
        //调用业务层方法，删除类别
        Integer deleteResult=productCategoryService.delete(id);
        if(deleteResult==1)return ResponseBean.success();
        if(deleteResult==-1)return ResponseBean.error("删除失败，此类别有子类别");
        return ResponseBean.error("删除失败！");
    }
}
