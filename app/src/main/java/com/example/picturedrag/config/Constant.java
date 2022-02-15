package com.example.picturedrag.config;


import androidx.annotation.StringDef;

/**
 * 常量
 *
 * @author wuczh
 * @date 2022/2/14
 */
public class Constant {


    /**
     * FileProvider
     */
    @StringDef({FileProvider.AUTHORITIES})
    public @interface FileProvider {
        /**
         * FileProvider名称
         */
        String AUTHORITIES = "com.example.picturedrag.fileprovider";
    }

}
