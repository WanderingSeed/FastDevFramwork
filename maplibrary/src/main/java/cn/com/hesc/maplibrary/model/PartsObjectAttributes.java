package cn.com.hesc.maplibrary.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: PartsObjectAttributes
 * Description: 查询部件图层属性时用到的配置信息
 * Author: liujunlin
 * Date: 2016-04-11 16:20
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class PartsObjectAttributes implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<String,String> attributeMap = null;
    //query.xml中的common
    public static final String[] mapKey={"ObjCode","ObjName","ObjState","CDepName","DEPTNAME1"};

    public PartsObjectAttributes() {
        attributeMap = new HashMap<String,String>();
    }
    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }

}
