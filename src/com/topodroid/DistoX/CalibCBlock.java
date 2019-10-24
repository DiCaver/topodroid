/* @file CalibCBlock.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief TopoDroid DistoX calibration data
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.DistoX;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Locale;

class CalibCBlock
{
  private static final int[] colors = { // 0xffcccccc, 0xffffcccc, 0xffccccff
    TDColor.LIGHT_GRAY,
    TDColor.LIGHT_PINK,
    TDColor.LIGHT_BLUE
  };

  long mId;
  long mCalibId;
  long gx;
  long gy;
  long gz;
  long mx;
  long my;
  long mz;
  long  mGroup;
  float mBearing;  // computed compass [degrees]
  float mClino;    // computed clino [degrees]
  float mRoll;     // computed roll
  float mError;    // error in the calibration algo associated to this data
  long mStatus;
  private boolean mFarness; // farness from reference item (previous item of a group)
  // float   mFarCosine;  // cos(angle) of farness (default 0 is ok)
  private boolean mOffGroup;

  boolean isSaturated()
  { 
    return ( mx >= 32768 || my >= 32768 || mz >= 32768 );
  }

  boolean isGZero()
  {
    return ( gx == 0 && gy == 0 && gz == 0 );
  }

  void setOffGroup( boolean b ) { mOffGroup = b; }
  boolean isOffGroup() { return mOffGroup; }

  void setFarness( boolean b ) { mFarness = b; }
  boolean isFar() { return mFarness; }
  // float   getFarCosine() { return mFarCosine; }

  // void computeFarness( CalibCBlock ref, float thr )
  // {
  //   mFarness = isFarFrom( ref.mBearing, ref.mClino, thr );
  // }

  CalibCBlock()
  {
    mId = 0;
    mCalibId = 0;
    gx = 0;
    gy = 0;
    gz = 0;
    mx = 0;
    my = 0;
    mz = 0;
    mGroup = 0;
    mError = 0.0f;
    mFarness = false;
  }

  boolean isFarFrom( float b0, float c0, float thr )
  {
    computeBearingAndClino();
    float c = c0 * TDMath.DEG2RAD;
    float b = b0 * TDMath.DEG2RAD;
    Vector v1 = new Vector( (float)Math.cos(c) * (float)Math.cos(b), 
                            (float)Math.cos(c) * (float)Math.sin(b),
                            (float)Math.sin(c) );
    c = mClino   * TDMath.DEG2RAD; 
    b = mBearing * TDMath.DEG2RAD;
    Vector v2 = new Vector( (float)Math.cos(c) * (float)Math.cos(b), 
                            (float)Math.cos(c) * (float)Math.sin(b),
                            (float)Math.sin(c) );
    float mFarCosine = v1.dot(v2);
    return mFarCosine < thr; // 0.70: approx 45 degrees
  }

  void setId( long id, long cid )
  {
    mId = id;
    mCalibId = cid;
  }
  // FIXME ZERO-DATA
  void setGroupIfNonZero( long g ) { mGroup = isGZero() ? 0 : g; }

  void setGroup( long g ) { mGroup = g; }
  void setError( float err ) { mError = err; }

  int color() 
  {
    if ( mGroup <= 0 ) return colors[0];
    return colors[ 1 + (int)(mGroup % 2) ];
  }

  void setStatus( long s ) { mStatus = s; }

  void setData( long gx0, long gy0, long gz0, long mx0, long my0, long mz0 )
  {
    gx = ( gx0 > TDUtil.ZERO ) ? gx0 - TDUtil.NEG : gx0;
    gy = ( gy0 > TDUtil.ZERO ) ? gy0 - TDUtil.NEG : gy0;
    gz = ( gz0 > TDUtil.ZERO ) ? gz0 - TDUtil.NEG : gz0;
    mx = ( mx0 > TDUtil.ZERO ) ? mx0 - TDUtil.NEG : mx0;
    my = ( my0 > TDUtil.ZERO ) ? my0 - TDUtil.NEG : my0;
    mz = ( mz0 > TDUtil.ZERO ) ? mz0 - TDUtil.NEG : mz0;
  } 

  void computeBearingAndClino()
  {
    float f = TDUtil.FV;
    // StringWriter sw = new StringWriter();
    // PrintWriter pw = new PrintWriter( sw );
    // pw.format("Locale.US, G %d %d %d M %d %d %d E %.2f", gx, gy, gz, mx, my, mz, mError );
    // TDLog.Log( TDLog.LOG_DATA, sw.getBuffer().toString() );
    Vector g = new Vector( gx/f, gy/f, gz/f );
    Vector m = new Vector( mx/f, my/f, mz/f );
    doComputeBearingAndClino( g, m );
  }

  void computeBearingAndClino( CalibAlgo calib )
  {
    float f = TDUtil.FV;
    Vector g = new Vector( gx/f, gy/f, gz/f );
    Vector m = new Vector( mx/f, my/f, mz/f );
    Vector g0 = calib.GetAG().timesV( g );
    Vector m0 = calib.GetAM().timesV( m );
    Vector g1 = calib.GetBG().plus( g0 );
    Vector m1 = calib.GetBM().plus( m0 );
    doComputeBearingAndClino( g1, m1 );
  }

  private void doComputeBearingAndClino( Vector g, Vector m )
  {
    g.normalize();
    m.normalize();
    Vector e = new Vector( 1.0f, 0.0f, 0.0f );
    Vector y = m.cross( g );
    Vector x = g.cross( y );
    y.normalize();
    x.normalize();
    float ex = e.dot( x );
    float ey = e.dot( y );
    float ez = e.dot( g );
    mBearing =   TDMath.atan2( -ey, ex );
    mClino   = - TDMath.atan2( ez, (float)Math.sqrt(ex*ex+ey*ey) );
    mRoll    =   TDMath.atan2( g.y, g.z );
    if ( mBearing < 0.0f ) mBearing += TDMath.M_2PI;
    if ( mRoll < 0.0f ) mRoll += TDMath.M_2PI;
    mClino   *= TDMath.RAD2DEG;
    mBearing *= TDMath.RAD2DEG;
    mRoll    *= TDMath.RAD2DEG;
  }

  public String toString()
  {
    float ua = TDSetting.mUnitAngle;

    StringWriter sw = new StringWriter();
    PrintWriter pw  = new PrintWriter(sw);
    computeBearingAndClino();
    pw.format(Locale.US, "%d <%d> %5.1f %5.1f %5.1f %4.2f",
      mId, mGroup, mBearing*ua, mClino*ua, mRoll*ua, mError*TDMath.RAD2DEG );
    if ( TDSetting.mRawCData == 1 ) {
      pw.format( "  %d %d %d  %d %d %d", gx, gy, gz, mx, my, mz );
    } else if ( TDSetting.mRawCData == 2 ) {
      pw.format( "  %04x %04x %04x  %04x %04x %04x", gx & 0xffff, gy & 0xffff, gz & 0xffff, mx & 0xffff, my & 0xffff, mz & 0xffff );
    }
    return sw.getBuffer().toString();
  }
}

