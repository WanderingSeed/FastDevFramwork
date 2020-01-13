package cn.com.hesc.gpslibrary.utils;

import java.util.ArrayList;

/**
 * created by liujunlin on 2018/12/11 15:18
 * 判断点位是否在多边形内
 */
public class PointInPolygonUtil {

    /**
     * 是否在多边形内
     * @param px
     * @param py
     * @param polygonXA
     * @param polygonYA
     * @return
     */
    public boolean isPointInPolygon(double px, double py, ArrayList<Double> polygonXA, ArrayList<Double> polygonYA) {
        boolean isInside = false;
        double ESP = 1e-9;
        int count = 0;
        double linePoint1x;
        double linePoint1y;
        double linePoint2x = 180;
        double linePoint2y;
        linePoint1x = px;
        linePoint1y = py;
        linePoint2y = py;
        for (int i = 0; i < polygonXA.size() - 1; i++) {
            double cx1 = polygonXA.get(i);
            double cy1 = polygonYA.get(i);
            double cx2 = polygonXA.get(i + 1);
            double cy2 = polygonYA.get(i + 1);
            if (isPointOnLine(px, py, cx1, cy1, cx2, cy2)) {
                return true;
            }
            if (Math.abs(cy2 - cy1) < ESP) {
                continue;
            }

            if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                if (cy1 > cy2)
                    count++;
            } else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                if (cy2 > cy1)
                    count++;
            } else if (isIntersect(cx1, cy1, cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                count++;
            }
        }
        if (count % 2 == 1) {
            isInside = true;
        }
        return isInside;
    }

    /**
     *
     * @param px0
     * @param py0
     * @param px1
     * @param py1
     * @param px2
     * @param py2
     * @return
     */
    public static double Multiply(double px0, double py0, double px1, double py1, double px2, double py2) {
        return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));
    }

    /**
     * 是否在边界上
     * @param px0
     * @param py0
     * @param px1
     * @param py1
     * @param px2
     * @param py2
     * @return
     */
    public boolean isPointOnLine(double px0, double py0, double px1, double py1, double px2, double py2) {
        boolean flag = false;
        double ESP = 1e-9;
        if ((Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP) && ((px0 - px1) * (px0 - px2) <= 0)
                && ((py0 - py1) * (py0 - py2) <= 0)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 
     * @param px1
     * @param py1
     * @param px2
     * @param py2
     * @param px3
     * @param py3
     * @param px4
     * @param py4
     * @return
     */
    public boolean isIntersect(double px1, double py1, double px2, double py2, double px3, double py3, double px4,
                               double py4) {
        boolean flag = false;
        double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
        if (d != 0) {
            double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3)) / d;
            double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1)) / d;
            if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) {
                flag = true;
            }
        }
        return flag;
    }
}
