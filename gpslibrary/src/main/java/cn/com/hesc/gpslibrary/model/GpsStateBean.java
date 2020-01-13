package cn.com.hesc.gpslibrary.model;

import android.location.GpsSatellite;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: GpsStateBean
 * Description: GPS数据结构
 * Author: liujunlin
 * Date: 2016-04-21 17:00
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class GpsStateBean{

        private String latString;
        private String logString;
        private GpsSatellite gs;
        public String getLatString() {
            return latString;
        }
        public void setLatString(String latString) {
            this.latString = latString;
        }
        public String getLogString() {
            return logString;
        }
        public void setLogString(String logString) {
            this.logString = logString;
        }
        public GpsSatellite getGs() {
            return gs;
        }
        public void setGs(GpsSatellite gs) {
            this.gs = gs;
        }



}
