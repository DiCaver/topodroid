/** @file Point2D.java
 *
 * @author marco corvi
 * @date may 2020
 *
 * @brief 2D point
 *
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.Cave3X;

// import java.io.StringWriter;
// import java.io.PrintWriter;

public class Point2D
{
  public double x, y;

  public Point2D( double xx, double yy )
  {
    x = xx;
    y = yy;
  }

  public Point2D midpoint2D( Point2D p ) { return new Point2D( (x+p.x)/2, (y+p.y)/2 ); }

  public double distance2D( Point2D p ) 
  { 
    double dx = x - p.x;
    double dy = y - p.y;
    return Math.sqrt( dx*dx + dy*dy );
  }

  // boolean isDistinct2D( Point2D p, double eps ) 
  // {
  //   return distance2D(p) > eps;
  // }

}
