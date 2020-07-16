/* @file ExportDialogPlot.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief TopoDroid survey sketch export dialog
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.DistoX;

import com.topodroid.ui.MyDialog;
import com.topodroid.prefs.TDSetting;

import android.util.Log;

// import android.app.Dialog;
// import android.app.Activity;
import android.os.Bundle;

// import android.content.Intent;

import android.content.Context;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.widget.EditText;

import android.view.View;
// import android.view.View.OnKeyListener;
// import android.view.KeyEvent;
// import android.view.ViewGroup.LayoutParams;

public class ExportDialogPlot extends MyDialog
                   implements AdapterView.OnItemSelectedListener
                   , View.OnClickListener
{
  private Button   mBtnOk;
  // private Button   mBtnBack;

  private final IExporter mParent;
  private String[]  mTypes;
  private String    mSelected;
  private final int mTitle;
  private int mSelectedPos;

  private LinearLayout mLayoutTherion;
  private LinearLayout mLayoutCSurvey;
  private LinearLayout mLayoutDxf;
  private LinearLayout mLayoutSvg;
  private LinearLayout mLayoutShp;
  private LinearLayout mLayoutPng;

  public ExportDialogPlot( Context context, IExporter parent, String[] types, int title )
  {
    super( context, R.string.ExportDialog );
    mParent = parent;
    mTypes  = types;
    mSelected = null;
    mTitle = title;
  }

// -------------------------------------------------------------------
  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);

    initLayout( R.layout.export_dialog_plot, mTitle );

    Spinner spin = (Spinner)findViewById( R.id.spin );
    spin.setOnItemSelectedListener( this );
    ArrayAdapter adapter = new ArrayAdapter<>( mContext, R.layout.menu, mTypes );
    spin.setAdapter( adapter );

    mLayoutTherion = (LinearLayout) findViewById( R.id.layout_therion );
    mLayoutCSurvey = (LinearLayout) findViewById( R.id.layout_csurvey );
    mLayoutDxf     = (LinearLayout) findViewById( R.id.layout_dxf );
    mLayoutSvg     = (LinearLayout) findViewById( R.id.layout_svg );
    mLayoutShp     = (LinearLayout) findViewById( R.id.layout_shp );
    mLayoutPng     = (LinearLayout) findViewById( R.id.layout_png );

    if ( ! TDLevel.overAdvanced ) {
      ((CheckBox) findViewById( R.id.therion_xvi )).setVisibility( View.GONE );
      ((CheckBox) findViewById( R.id.therion_scale )).setVisibility( View.GONE );
      ((CheckBox) findViewById( R.id.svg_grid )).setVisibility( View.GONE );
      ((CheckBox) findViewById( R.id.svg_linedir )).setVisibility( View.GONE );
      if ( ! TDLevel.overExpert ) {
        ((CheckBox) findViewById( R.id.svg_roundtrip )).setVisibility( View.GONE );
      }
    }

    mBtnOk   = (Button) findViewById(R.id.button_ok );
    mBtnOk.setOnClickListener( this );
    // mBtnBack = (Button) findViewById(R.id.button_back );
    // mBtnBack.setOnClickListener( this );
    ( (Button) findViewById(R.id.button_back ) ).setOnClickListener( this );

    // Bundle extras = getIntent().getExtras();
    // String title  = extras.getString( TopoDroidApp.TOPODROID_SURVEY );

    mSelectedPos = 0;
    mSelected = mTypes[ mSelectedPos ];
    initOptions();
    updateLayouts();
  }

  @Override
  public void onItemSelected( AdapterView av, View v, int pos, long id ) 
  {
    mSelected = mTypes[ pos ];
    mSelectedPos = pos;
    updateLayouts();
  }

  @Override
  public void onNothingSelected( AdapterView av ) 
  { 
    mSelected = null;
    mSelectedPos = -1;
    updateLayouts();
  }

  @Override
  public void onClick(View v) 
  {
    // Log.v("DistoX-C3D", "Selected " + mSelected );
    Button b = (Button)v;
    if ( b == mBtnOk && mSelected != null ) {
      setOptions();
      mParent.doExport( mSelected );
    // } else if ( b == mBtnBack ) {
    //   /* nothing */
    }
    dismiss();
  }

  private void updateLayouts()
  {
    mLayoutTherion.setVisibility( View.GONE );
    mLayoutCSurvey.setVisibility( View.GONE );
    mLayoutDxf.setVisibility( View.GONE );
    mLayoutSvg.setVisibility( View.GONE );
    mLayoutShp.setVisibility( View.GONE );
    mLayoutPng.setVisibility( View.GONE );
    switch ( mSelectedPos ) {
      case 0: mLayoutTherion.setVisibility( View.VISIBLE ); break;
      case 1: mLayoutCSurvey.setVisibility( View.VISIBLE ); break;
      case 2: mLayoutDxf.setVisibility( View.VISIBLE ); break;
      case 3: mLayoutSvg.setVisibility( View.VISIBLE ); break;
      case 4: if ( TDLevel.overExpert ) mLayoutShp.setVisibility( View.VISIBLE ); break;
      case 5: mLayoutPng.setVisibility( View.VISIBLE ); break;
    }
  }

  private void setOptions()
  {
    switch ( mSelectedPos ) {
      case 0: // Therion
        {
          TDSetting.mTherionSplays = ((CheckBox) findViewById( R.id.therion_splays )).isChecked();
          TDSetting.mTherionXvi = ((CheckBox) findViewById( R.id.therion_xvi )).isChecked();
          try { 
            int scale = Integer.parseInt( ((EditText) findViewById( R.id.therion_scale )).getText().toString() );
            if ( scale < 40 ) { scale = 40; }
            if ( scale > 2000 ) { scale = 2000; }
            TDSetting.mTherionScale = scale;
            TDSetting.mToTherion = TDSetting.THERION_SCALE / scale;
          } catch ( NumberFormatException e ) { }
        }
        break;
      case 1: // CSurvey
        {
          TDSetting.mExportStationsPrefix = ((CheckBox) findViewById( R.id.csurvey_prefix )).isChecked();
        }
        break;
      case 2: // DXF
        {
          TDSetting.mDxfBlocks = ((CheckBox) findViewById( R.id.dxf_blocks )).isChecked();
          // TDSetting.mAcadVersion
        }
        break;
      case 3: // SVG
        {
          TDSetting.mSvgRoundTrip = ((CheckBox) findViewById( R.id.svg_roundtrip )).isChecked();
          TDSetting.mSvgGrid      = ((CheckBox) findViewById( R.id.svg_grid )).isChecked();
          TDSetting.mSvgLineDirection   = ((CheckBox) findViewById( R.id.svg_linedir )).isChecked();
          TDSetting.mSvgSplays    = ((CheckBox) findViewById( R.id.svg_splays )).isChecked();
        }
        break;
      case 4: // Shapefile
        {
          TDSetting.mShpGeoref = ((CheckBox) findViewById( R.id.shp_georeference )).isChecked();
        }
        break;
      case 5: // PNG
        {
          TDSetting.mTherionSplays = ((CheckBox) findViewById( R.id.png_splays )).isChecked();
          TDSetting.mSvgGrid   = ((CheckBox) findViewById( R.id.png_grid )).isChecked();
          if ( ((CheckBox) findViewById( R.id.png_bgcolor )).isChecked() ) TDSetting.mBitmapBgcolor = 0xffffffff;
          try { 
            float sc = Float.parseFloat( ((EditText) findViewById( R.id.therion_scale )).getText().toString() );
            if ( sc > 0 ) TDSetting.mBitmapScale = sc;
          } catch ( NumberFormatException e ) { }
        }
        break;
    }
  }

  private void initOptions()
  {
    ((CheckBox) findViewById( R.id.therion_splays )).setChecked( TDSetting.mTherionSplays );
    ((CheckBox) findViewById( R.id.therion_xvi )).setChecked( TDSetting.mTherionXvi );
    ((EditText) findViewById( R.id.therion_scale )).setText( Integer.toString( TDSetting.mTherionScale ) );

    ((CheckBox) findViewById( R.id.csurvey_prefix )).setChecked( TDSetting.mExportStationsPrefix );
    
    ((CheckBox) findViewById( R.id.dxf_blocks )).setChecked( TDSetting.mDxfBlocks );
    // TDSetting.mAcadVersion
    
    ((CheckBox) findViewById( R.id.svg_roundtrip )).setChecked( TDSetting.mSvgRoundTrip );
    ((CheckBox) findViewById( R.id.svg_grid )).setChecked( TDSetting.mSvgGrid );
    ((CheckBox) findViewById( R.id.svg_linedir )).setChecked( TDSetting.mSvgLineDirection );
    ((CheckBox) findViewById( R.id.svg_splays )).setChecked( TDSetting.mSvgSplays );
    
    ((CheckBox) findViewById( R.id.shp_georeference )).setChecked( TDSetting.mShpGeoref );

    ((CheckBox) findViewById( R.id.png_splays )).setChecked( TDSetting.mTherionSplays );
    ((CheckBox) findViewById( R.id.png_grid )).setChecked( TDSetting.mSvgGrid );
    ((CheckBox) findViewById( R.id.png_bgcolor )).setChecked( TDSetting.mBitmapBgcolor == 0xffffffff );
    ((EditText) findViewById( R.id.png_scale )).setText( Float.toString( TDSetting.mBitmapScale ) );
  }


}


