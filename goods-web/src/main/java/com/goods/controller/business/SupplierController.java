package com.goods.controller.business;

import com.goods.business.service.SupplierService;
import com.goods.common.response.ResponseBean;
import com.goods.common.vo.business.SupplierVO;
import com.goods.common.vo.system.PageVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Title :物资来源
 * @Author Ghia
 * @Create 2022-12-25 16:37
 **/
@Api(tags = "业务管理-物资来源接口")
@RestController
@RequestMapping("business/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    //http://localhost:8989/business/supplier/findSupplierList?pageNum=1&pageSize=10&name=昌平&address=北京&contact=张三
    //分页查询来源列表
    @GetMapping("findSupplierList")
    public ResponseBean findSupplierList(@RequestParam(value = "pageNum",required = false)Integer pageNum,
                                         @RequestParam(value = "pageSize",required = false)Integer pageSize,
                                         @RequestParam(value = "name",required = false)String name,
                                         @RequestParam(value = "address",required = false)String address,
                                         @RequestParam(value = "contact",required = false)String contact){
        //调用业务层方法，分页获取物资类别列表
        PageVO<SupplierVO> supplierVOPage= supplierService.findSupplierList(pageNum,pageSize,name,address,contact);
        return ResponseBean.success(supplierVOPage);
    }

    //http://www.localhost:8989/business/supplier/edit/25
    //查询来源详情
    @GetMapping("edit/{id}")
    public ResponseBean edit(@PathVariable Long id){
        //调用业务层方法，查询来源详情
        SupplierVO supplierVO= supplierService.edit(id);
        if (supplierVO==null){
            return ResponseBean.error("找不到数据");
        }
        return ResponseBean.success(supplierVO);
    }

    //http://www.localhost:8989/business/supplier/update/25
    //修改来源信息
    @PutMapping("update/*")
    public ResponseBean update(@RequestBody SupplierVO supplierVO){
        //调用业务层方法，修改来源信息
        Integer result = supplierService.update(supplierVO);
        if(result!=1)return ResponseBean.error("修改失败！");
        return ResponseBean.success();
    }

    //http://www.localhost:8989/business/supplier/add
    //添加来源信息
    @PostMapping("add")
    public ResponseBean add(@RequestBody SupplierVO supplierVO){
        //调用业务层方法，添加来源信息
        Integer addResult=supplierService.add(supplierVO);
        if(addResult!=1)return ResponseBean.error("添加失败！");
        return ResponseBean.success();
    }

    //http://www.localhost:8989/business/supplier/delete/26
    //删除来源
    @DeleteMapping("delete/{id}")
    public ResponseBean delete(@PathVariable("id") Long id){
        //调用业务层方法，删除来源
        Integer deleteResult=supplierService.delete(id);
        if(deleteResult!=1)return ResponseBean.error("删除失败！");
        return ResponseBean.success();
    }
}
