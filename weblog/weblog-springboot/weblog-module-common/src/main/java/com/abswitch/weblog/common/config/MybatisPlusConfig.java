package com.abswitch.weblog.common.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-03-17 20:04
 * @Description： Mybatis Plus 配置文件
 */
@Configuration
@MapperScan("com.abswitch.weblog.common.domain.mapper")
//通过 @MapperScan 指定 Mapper 接口所在包，确保该包下的所有 Mapper 都能使用扩展后的方法。
public class MybatisPlusConfig {

    /**
     * 分页插件
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){

        //拦截器链容器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //添加一个分页内部拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 自定义批量插入 SQL 注入器
     */
    @Bean
    public InsertBatchSqlInjector insertBatchSqlInjector() {
        return new InsertBatchSqlInjector();
    }
}
