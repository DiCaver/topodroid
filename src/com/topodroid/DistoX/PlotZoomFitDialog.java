/* @file PlotZoomFitDialog.java
 *
 * @author marco corvi
 * @date jul 2017
 *
 * @brief TopoDroid plot zomm-fit / landscape
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.DistoX;

// import java.util.List;
// import java.util.ArrayList;

import android.os.Bundle;
// import android.app.Dialog;

import android.content.Context;

import android.view.View;
import android.view.View.OnClickListener;
// import android.view.ViewGroup.LayoutParams;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.EditText;

// import android.widget.Toast;

class PlotZoomFitDialog extends MyDialog
                               implements OnClickListener
{
  private final DrawingWindow mParent;
  private Button mBtnPortrait;
  private Button mBtnLandscape;
  private Button mBtnStation;
  private EditText mETstation;
  // private Button mBtnZoomFit;

  PlotZoomFitDialog( Context context, DrawingWindow parent )
  {
    super( context, R.string.PlotZoomFitDialog );
    mParent = parent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    initLayout( R.layout.plot_zoomfit_dialog, R.string.title_plot_zoomfit );

    int size = TDSetting.mSizeButtons; // TopoDroidApp.getScaledSize( mContext );

    LinearLayout layout1 = (LinearLayout) findViewById( R.id.layout1 );
    layout1.setMinimumHeight( size + 20 );
    
    LinearLayout.LayoutParams lp = TDLayout.getLayoutParams( 0, 10, 20 ,10 );

    // mBtnZoomFit = new MyCheckBox( mContext, size, R.drawable.iz_zoomfit, R.drawable.iz_zoomfit );
    // mBtnZoomFit.setOnClickListener( this );
    // layout1.addView( mBtnZoomFit );
    // mBtnZoomFit.setLayoutParams( lp );

    // ((Button) findViewById( R.id.button_zoomfit )).setOnClickListener( this );
    // mBtnPortrait  = (Button) findViewById( R.id.button_portrait );
    // mBtnLandscape = (Button) findViewById( R.id.button_landscape );
    // mBtnPortrait.setOnClickListener( this );
    // mBtnLandscape.setOnClickListener( this );
    // if ( mParent.isLandscape() ) {
      mBtnPortrait = new MyCheckBox( mContext, size, R.drawable.iz_northup, R.drawable.iz_northup );
      mBtnPortrait.setOnClickListener( this );
      layout1.addView( mBtnPortrait );
      mBtnPortrait.setLayoutParams( lp );
      // mBtnLandscape.setText( R.string.button_zoomfit );
    // } else {
      mBtnLandscape = new MyCheckBox( mContext, size, R.drawable.iz_northleft, R.drawable.iz_northleft );
      mBtnLandscape.setOnClickListener( this );
      layout1.addView( mBtnLandscape );
      mBtnLandscape.setLayoutParams( lp );
      // mBtnPortrait.setText( R.string.button_zoomfit );
    // }
    ((Button) findViewById( R.id.button_cancel )).setOnClickListener( this );

    mBtnStation  = (Button) findViewById( R.id.button_station );
    mETstation = (EditText) findViewById( R.id.center_station );
    mBtnStation.setOnClickListener( this );
  }
 
  // ---------------------------------------------------------------

  @Override 
  public void onClick( View v ) 
  {
    if ( v.getId() == R.id.button_cancel ) {
      // nothing
    } else {
      Button btn = (Button) v;
      if ( btn == mBtnPortrait ) {
        mParent.setOrientation( PlotInfo.ORIENTATION_PORTRAIT );
      } else if ( btn == mBtnLandscape ) {
        mParent.setOrientation( PlotInfo.ORIENTATION_LANDSCAPE );
      // } else if ( btn == mBtnZoomFit ) {
      //   mParent.doZoomFit();
      } else if ( btn == mBtnStation ) {
        String station = mETstation.getText().toString();
	if ( station == null || station.length() == 0 ) {
          mETstation.setError( mContext.getResources().getString( R.string.error_station_required ) );
	  return;
	}
	mParent.centerAtStation( station );
      }
    }
    dismiss();
  }

}
