package cn.com.hesc.maplibrary.model;

import java.util.List;

/**
 * ProjectName: 2015_esscm_szcg_standard
 * ClassName: PartsAttributeOfWFS
 * Description: WFS格式的部件属性
 * Author: liujunlin
 * Date: 2016-06-22 15:39
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */

public class PartsAttributeOfWFS {

    /**
     * type : FeatureCollection
     * totalFeatures : 1
     * features : [{"type":"Feature","id":"公共自行车.1","geometry":{"type":"Point","coordinates":[120.75555555555559,29.496296296296304]},"geometry_name":"the_geom","properties":{"Id":0,"name":"测试名称","lat":120.755555556,"lng":29.4962962963,"address":"洞头测试地址"}}]
     * crs : {"type":"name","properties":{"name":"urn:ogc:def:crs:EPSG::4490"}}
     */

    private String type;
    private int totalFeatures;
    /**
     * type : name
     * properties : {"name":"urn:ogc:def:crs:EPSG::4490"}
     */

    private CrsBean crs;
    /**
     * type : Feature
     * id : 公共自行车.1
     * geometry : {"type":"Point","coordinates":[120.75555555555559,29.496296296296304]}
     * geometry_name : the_geom
     * properties : {"Id":0,"name":"测试名称","lat":120.755555556,"lng":29.4962962963,"address":"洞头测试地址"}
     */

    private List<FeaturesBean> features;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTotalFeatures() {
        return totalFeatures;
    }

    public void setTotalFeatures(int totalFeatures) {
        this.totalFeatures = totalFeatures;
    }

    public CrsBean getCrs() {
        return crs;
    }

    public void setCrs(CrsBean crs) {
        this.crs = crs;
    }

    public List<FeaturesBean> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeaturesBean> features) {
        this.features = features;
    }

    public static class CrsBean {
        private String type;
        /**
         * name : urn:ogc:def:crs:EPSG::4490
         */

        private PropertiesBean properties;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public PropertiesBean getProperties() {
            return properties;
        }

        public void setProperties(PropertiesBean properties) {
            this.properties = properties;
        }

        public static class PropertiesBean {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

    public static class FeaturesBean {
        private String type;
        private String id;
        /**
         * type : Point
         * coordinates : [120.75555555555559,29.496296296296304]
         */

        private GeometryBean geometry;
        private String geometry_name;
        /**
         * Id : 0
         * name : 测试名称
         * lat : 120.755555556
         * lng : 29.4962962963
         * address : 洞头测试地址
         */

        private PropertiesBean properties;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public GeometryBean getGeometry() {
            return geometry;
        }

        public void setGeometry(GeometryBean geometry) {
            this.geometry = geometry;
        }

        public String getGeometry_name() {
            return geometry_name;
        }

        public void setGeometry_name(String geometry_name) {
            this.geometry_name = geometry_name;
        }

        public PropertiesBean getProperties() {
            return properties;
        }

        public void setProperties(PropertiesBean properties) {
            this.properties = properties;
        }

        public static class GeometryBean {
            private String type;
            private List<Double> coordinates;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<Double> getCoordinates() {
                return coordinates;
            }

            public void setCoordinates(List<Double> coordinates) {
                this.coordinates = coordinates;
            }
        }

        public static class PropertiesBean {
            private int Id;
            private String name;
            private double lat;
            private double lng;
            private String address;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }
    }
}
