/* @file TopoDroidAbout.java
 *
 * @author marco corvi
 * @date may 2012
 *
 * @brief TopoDroid about dialog
 *
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * ----------------------------------------------------------
 */
package com.topodroid.DistoX;

import android.content.Context;
// import android.content.Intent;

import android.app.Dialog;
// import android.widget.Button;
// import android.view.View;
// import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
// import android.net.Uri;

// import android.widget.Toast;

class TopoDroidAbout extends Dialog
                     // implements OnClickListener
{
  // private Button mBTok;
  // private Context mContext;
  // private MainWindow mParent;
  private int mSetup;

  int nextSetup() { return mSetup + 1; }

  TopoDroidAbout( Context context, MainWindow parent, int setup )
  {
    super( context );
    // mContext = context;
    // mParent  = parent;
    mSetup   = setup;
    setContentView(R.layout.welcome);
    getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );

    setTitle( String.format( context.getResources().getString(R.string.welcome_title), TopoDroidApp.VERSION ) );

    // mBTok     = (Button)findViewById(R.id.btn_ok);

    // mBTok.setOnClickListener( this );
  }

  // @Override
  // public void onDismiss()
  // {
  //   if ( mSetup >= 0 ) mParent.doNextSetup( mSetup + 1 );
  // }

  
}
