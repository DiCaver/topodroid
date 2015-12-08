/** @file SymbolPointLibrary.java
 *
 * @author marco corvi
 * @date dec 2012
 *
 * @brief TopoDroid drawing: point symbol library
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 * CHANGES
 * 20121201 created
 * 20121211 locale
 * 20121215 avoid double th-name symbol
 * 20140422 iso
 */
package com.topodroid.DistoX;

import java.util.Locale;
import java.util.ArrayList;
import java.util.TreeSet;
import java.io.File;
import java.io.PrintWriter;

import android.graphics.Paint;
import android.graphics.Path;
import android.content.res.Resources;

import android.util.Log;

class SymbolPointLibrary
{
  static final String DefaultPoints[] = {
    "air-draught", "blocks", "clay", "continuation", "debris", "entrance", "sand", "stalactite", "stalagmite", "water-flow"
  };

  // ArrayList< SymbolPoint > mPoint;    // enabled points
  ArrayList< SymbolPoint > mAnyPoint; // all points
  int mPointUserIndex;
  int mPointLabelIndex;
  int mPointDangerIndex;
  private int mPointNr;
  int mAnyPointNr;


  SymbolPointLibrary( Resources res )
  {
    // Log.v(  TopoDroidApp.TAG, "cstr SymbolPointLibrary()" );
    // mPoint = new ArrayList< SymbolPoint >();
    mAnyPoint = new ArrayList< SymbolPoint >();
    mPointUserIndex   = 0;
    mPointLabelIndex  = -1;
    mPointDangerIndex = -1;
    loadSystemPoints( res );
    loadUserPoints();
    makeEnabledList();
  }

  int getSymbolIndex( Symbol symbol ) 
  {
    for ( int k=0; k<mAnyPoint.size(); ++k ) {
      if ( symbol == mAnyPoint.get(k) ) return k;
    }
    return -1;
  }

  // =============================================================
  // int size() { return mNrPoint; }

  
  SymbolPoint getSymbolAnyPoint( String th_name )
  {
    for ( SymbolPoint p : mAnyPoint ) {
      if ( p.hasThName( th_name ) ) return p;
    }
    return null;
  }

  boolean pointHasText( int k ) 
  {
    if ( k < 0 || k >= mAnyPointNr ) return false;
    return mAnyPoint.get(k).mHasText;
  }

  boolean hasPoint( String th_name )
  {
    for ( SymbolPoint p : mAnyPoint ) {
      if ( p.hasThName( th_name ) ) {
        return p.isEnabled();
      }
    }
    return false;
  }

  boolean hasAnyPoint( String th_name )
  {
    for ( SymbolPoint p : mAnyPoint ) {
      if ( p.hasThName( th_name ) ) {
        return true;
      }
    }
    return false;
  }

  // boolean removePoint( String th_name ) 
  // {
  //   for ( SymbolPoint p : mPoint ) {
  //     if ( p.hasThName( th_name ) ) {
  //       mPoint.remove( p );
  //       TopoDroidApp.mData.setSymbolEnabled( "p_" + th_name, false );
  //       return true;
  //     }
  //   }
  //   return false;
  // }

  // SymbolPoint getPoint( int k ) 
  // {
  //   if ( k < 0 || k >= mAnyPointNr ) return null;
  //   SymbolPoint p = mAnyPoint.get( k );
  //   return p.isEnabled()? p : null;
  // }

  SymbolPoint getAnyPoint( int k ) 
  {
    if ( k < 0 || k >= mAnyPointNr ) return null;
    return mAnyPoint.get( k );
  }

  String getAnyPointName( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return null;
    return mAnyPoint.get( k ).getName();
  }


  String getPointThName( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return null;
    return mAnyPoint.get(k).getThName( );
  }

  Paint getPointPaint( int k ) 
  {
    if ( k < 0 || k >= mAnyPointNr ) return null;
    return mAnyPoint.get(k).getPaint( );
  }
  
  boolean canRotate( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return false;
    return mAnyPoint.get(k).mOrientable;
  }

  double getPointOrientation( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return 0.0;
    return mAnyPoint.get(k).mOrientation;
  }

  void resetOrientations()
  {
    // Log.v(  TopoDroidApp.TAG, "SymbolPointLibrary::resetOrientations()" );
    for ( SymbolPoint sp : mAnyPoint ) sp.resetOrientation();
    // JAVA8 mAnyPoint.stream().forEach( SymbolPoint::resetOrientation );
  }

  void rotateGrad( int k, double a )
  {
    if ( k < 0 || k >= mAnyPointNr ) return;
    mAnyPoint.get(k).rotateGrad( a );
  }

  Path getPointPath( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return null;
    return mAnyPoint.get(k).getPath( );
  }

  Path getPointOrigPath( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return null;
    return mAnyPoint.get(k).getOrigPath( );
  }

  int pointCsxLayer( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return -1;
    return mAnyPoint.get(k).mCsxLayer;
  }

  int pointCsxType( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return -1;
    return mAnyPoint.get(k).mCsxType;
  }

  int pointCsxCategory( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return -1;
    return mAnyPoint.get(k).mCsxCategory;
  }

  String pointCsx( int k )
  {
    if ( k < 0 || k >= mAnyPointNr ) return "";
    return mAnyPoint.get(k).mCsx;
  }


  // ========================================================================

  final String p_label = "moveTo 0 3 lineTo 0 -6 lineTo -3 -6 lineTo 3 -6";
  final String p_user = "addCircle 0 0 6";

  private void loadSystemPoints( Resources res )
  {
    SymbolPoint symbol;
    // Log.v(  TopoDroidApp.TAG, "SymbolPointLibrary::loadSystemPoints()" );

    mPointUserIndex = mAnyPoint.size();
    symbol = new SymbolPoint( res.getString(R.string.thp_user), "user", 0xffffffff, p_user, false, false );
    symbol.mCsxLayer = 6;
    symbol.mCsxType  = 8;
    symbol.mCsxCategory = 81;
    mAnyPoint.add( symbol );

    mPointLabelIndex = mAnyPoint.size();
    symbol = new SymbolPoint( res.getString(R.string.thp_label), "label", 0xffffffff, p_label, false, true );
    symbol.mCsxLayer = 6;
    symbol.mCsxType  = 8;
    symbol.mCsxCategory = 81;
    mAnyPoint.add( symbol );

    mAnyPointNr = mAnyPoint.size();
  }

  void loadUserPoints()
  {
    String locale = "name-" + TopoDroidApp.mLocale.toString().substring(0,2);
    String iso = "ISO-8859-1";
    // String iso = "UTF-8";
    // if ( locale.equals( "name-de" ) ) iso = "UTF-8";
    // Charset.forName("ISO-8859-1")

    File dir = new File( TopoDroidPath.APP_POINT_PATH );
    if ( dir.exists() ) {
      int systemNr = mAnyPoint.size();
      File[] files = dir.listFiles();
      for ( File file : files ) {
        SymbolPoint symbol = new SymbolPoint( file.getPath(), locale, iso );
        if ( symbol.mThName == null ) {
          TopoDroidLog.Log( TopoDroidLog.LOG_ERR, "point with null ThName" );
          continue;
        }
        if ( ! hasAnyPoint( symbol.getThName() ) ) {
          mAnyPoint.add( symbol );
          String thname = symbol.mThName;
          String name = "p_" + thname;
          boolean enable = false;
          if ( ! TopoDroidApp.mData.hasSymbolName( name ) ) {
            for ( int k=0; k<DefaultPoints.length; ++k ) { 
              if ( DefaultPoints[k].equals( thname ) ) { enable = true; break; }
            }
            TopoDroidApp.mData.setSymbolEnabled( name, enable );
          } else {
            enable = TopoDroidApp.mData.getSymbolEnabled( name );
          }
          symbol.setEnabled( enable );
        }
      }
      mAnyPointNr = mAnyPoint.size();
      sortSymbolByName( systemNr );
    } else {
      dir.mkdirs( );
    }
  }

  private void sortSymbolByName( int start )
  {
    for ( int k=start+1; k<mAnyPointNr; ) {
      SymbolPoint prev = mAnyPoint.get(k-1);
      SymbolPoint curr = mAnyPoint.get(k);
      if ( prev.getName().compareTo(curr.getName()) > 0  ) { // swap
        mAnyPoint.set( k-1, curr );
        mAnyPoint.set( k, prev );
        if ( k > start+1 ) --k;
      } else {
        ++k;
      }
    }
  }


  boolean tryLoadMissingPoint( String p )
  {
    String locale = "name-" + Locale.getDefault().toString().substring(0,2);
    String iso = "ISO-8859-1";
    // String iso = "UTF-8";
    // if ( locale.equals( "name-es" ) ) iso = "ISO-8859-1";

    if ( hasPoint( p ) ) return true;
    SymbolPoint symbol = getSymbolAnyPoint( p );
    if ( symbol == null ) {
      // Log.v( TopoDroidApp.TAG, "load missing point " + p );
      File file = new File( TopoDroidPath.APP_SAVE_POINT_PATH + p );
      if ( ! file.exists() ) return false;

      symbol = new SymbolPoint( file.getPath(), locale, iso );
      mAnyPoint.add( symbol );
    } else {
      // Log.v( TopoDroidApp.TAG, "enabling missing point " + p );
    }
    if ( symbol == null ) return false;

    symbol.setEnabled( true ); // TopoDroidApp.mData.isSymbolEnabled( "a_" + symbol.mThName ) );
    makeEnabledList();
    return true;
  }

// ------------------------------------------------------------------
     
  void makeEnabledList()
  {
    // mPoint.clear();
    mPointNr = 0;
    int index = 0;
    for ( SymbolPoint symbol : mAnyPoint ) {
      if ( symbol.mEnabled ) {
        if ( symbol.getThName().equals("user") )    mPointUserIndex   = index; // mPoint.size();
        if ( symbol.getThName().equals("label") )   mPointLabelIndex  = index; // mPoint.size();
        if ( symbol.getThName().equals("danger" ) ) mPointDangerIndex = index; // mPoint.size();
        // mPoint.add( symbol );
        ++ mPointNr;
      }
      ++ index;
    }
  }

  void setRecentPoints( Symbol recent[] )
  {
    int k = 0;
    for ( SymbolPoint symbol : mAnyPoint ) {
      if ( symbol.mEnabled ) {
        recent[k++] = symbol;
        if ( k >= ItemDrawer.NR_RECENT ) break;
      }
    }
  }


  void makeEnabledListFromPalette( SymbolsPalette palette )
  {
    for ( SymbolPoint symbol : mAnyPoint ) {
      symbol.setEnabled( symbol.getThName().equals("user") || symbol.getThName().equals("label") );
      // symbol.setEnabled( false );
    }
    for ( String p : palette.mPalettePoint ) {
      SymbolPoint symbol = getSymbolAnyPoint( p );
      if ( symbol != null ) symbol.setEnabled( true );
    }
    makeEnabledList();
  }

  void writePalette( PrintWriter pw ) 
  {
    for ( SymbolPoint symbol : mAnyPoint ) {
      if ( symbol.isEnabled( ) ) pw.format( " %s", symbol.getThName() );
    }
  }

}    
