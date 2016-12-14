package com.cryptopaths.cryptofm.filemanager;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;

import java.io.File;
import java.util.HashMap;


public class FileBrowserActivity extends AppCompatActivity implements FileFragment.onClickListener{
	private String mCurrentPath;
	private String mRootPath;
	private ListView mFileListView;
	private FileListAdapter mmFileListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_activity);
		setResult(RESULT_OK);

		mCurrentPath=Environment.getExternalStorageDirectory().getPath();
		mRootPath=mCurrentPath;
		mFileListView=(ListView)findViewById(R.id.fileListView);
		mmFileListAdapter=new FileListAdapter(this);
		mmFileListAdapter.fillAdapter(mCurrentPath);
		mFileListView.setAdapter(mmFileListAdapter);
		mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String viewName=((TextView)(view.findViewById(R.id.list_textview))).getText().toString();

				String tmp=mCurrentPath+"/"+viewName;
				File f=new File(tmp);
				if(f.isDirectory()) {
					mCurrentPath=tmp;
					changeDirectory();
				}else {
					Toast.makeText(FileBrowserActivity.this,"You clicked at file",Toast.LENGTH_SHORT).show();
				}


			}
		});
	}

	private void changeDirectory() {
		mmFileListAdapter.fillAdapter(mCurrentPath);
		mmFileListAdapter.notifyDataSetChanged();
		//mFileListView.setAdapter(mmFileListAdapter);

	}

	@Override
	public void onBackPressed() {
		if(mCurrentPath.equals(mRootPath)){
			super.onBackPressed();
		}else{
			//modify the mCurrentPath
			mCurrentPath=mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/'));
			Log.d("googled","current batk in back is: "+mCurrentPath);
		mmFileListAdapter.fillAdapter(mCurrentPath);
		mFileListView.setAdapter(mmFileListAdapter);
		}
	}


	@Override
	public void onItemClick(String path) {
		//chanegFragment(path);
	}
}