package com.goods.business.service.imp;

import com.goods.business.mapper.ProductMapper;
import com.goods.business.service.ProductService;
import com.goods.common.model.business.Product;
import com.goods.common.utils.ListPageUtils;
import com.goods.common.vo.business.ProductVO;
import com.goods.common.vo.system.PageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Title :物资物料
 * @Author Ghia
 * @Create 2022-12-25 22:15
 **/
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    //查询物资
    @Override
    public PageVO<ProductVO> findProductList(Integer pageNum, Integer pageSize, String name, List categorys, Integer status) {
        //根据条件查询物资
        Example example = new Example(Product.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",status);
        if(!StringUtils.isEmpty(name)){
            criteria.andLike("name","%"+name+"%");
        }
        if(!CollectionUtils.isEmpty(categorys)){
            int size = categorys.size();
            if(size>0){
                criteria.andEqualTo("oneCategoryId",categorys.get(0));
                if(size>1){
                    criteria.andEqualTo("twoCategoryId",categorys.get(1));
                    if(size>2){
                        criteria.andEqualTo("threeCategoryId",categorys.get(2));
                    }
                }
            }
        }
        //查询
        List<Product> productList = productMapper.selectByExample(example);

        //为空
        if (CollectionUtils.isEmpty(productList))return null;

        //属性值复制
        List<ProductVO> productVOList = productList.stream().map(product -> {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            return productVO;
        }).collect(Collectors.toList());

        //分页
        if(pageNum==null||pageSize==null){
            return new PageVO<>(productVOList.size(),productVOList);
        }
        return new PageVO<>(productVOList.size(), ListPageUtils.page(productVOList,pageSize,pageNum));
    }

    @Override
    public Integer add(ProductVO productVO) {
        //设置创建和修改时间
        productVO.setCreateTime(new Date());
        productVO.setModifiedTime(new Date());
        //设置状态，待审核
        productVO.setStatus(2);
        //设置p_num
        productVO.setPNum(UUID.randomUUID().toString().substring(0,32));
        //设置category
        Long[] categoryKeys = productVO.getCategoryKeys();
        int length = categoryKeys.length;
        if(length>0){
            productVO.setOneCategoryId(categoryKeys[0]);
            if(length>1){
                productVO.setTwoCategoryId(categoryKeys[1]);
                if(length>2){
                    productVO.setThreeCategoryId(categoryKeys[2]);
                }
            }
        }
        //属性值复制
        Product product = new Product();
        BeanUtils.copyProperties(productVO,product);
        return productMapper.insert(product);
    }

    //查询物资
    @Override
    public ProductVO edit(Long id) {
        //查询
        Product product = new Product();
        product.setId(id);
        product=productMapper.selectOne(product);
        //属性值复制
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product,productVO);
        return productVO;
    }

    //修改物资
    @Override
    public Integer update(ProductVO productVO) {
        //修改时间
        productVO.setModifiedTime(new Date());
        //属性值复制
        Product product = new Product();
        BeanUtils.copyProperties(productVO,product);
        return productMapper.updateByPrimaryKey(product);
    }

    //删除物资
    @Override
    public Integer remove(Long id) {
        return productMapper.deleteByPrimaryKey(id);
    }
}
