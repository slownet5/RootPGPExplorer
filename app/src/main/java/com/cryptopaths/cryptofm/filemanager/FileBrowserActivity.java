package com.cryptopaths.cryptofm.filemanager;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.utils.ActionHandler;

import java.util.HashMap;


public class FileBrowserActivity extends AppCompatActivity implements ActionMode.Callback,FileListAdapter.LongClickCallBack {
	private String 			mCurrentPath;
	private String 			mRootPath;
	private RecyclerView 	mFileListView;
	private FileListAdapter mmFileListAdapter;

	public static HashMap<String,FileFillerWrapper> mFilesData	= new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_activity);
		setResult(RESULT_OK);


		mCurrentPath 	  = Environment.getExternalStorageDirectory().getPath();
		mRootPath	 	  = mCurrentPath+"/";
		mFileListView 	  = (RecyclerView) findViewById(R.id.fileListView);
		mmFileListAdapter = new FileListAdapter(this);

		mFileListView.setLayoutManager(new LinearLayoutManager(this));
		mmFileListAdapter.setmFile(mFilesData.get(mCurrentPath+"/"));
		// item decoration for displaying divider
		DividerItemDecoration dividerItemDecoration =
				new DividerItemDecoration(mFileListView.getContext(),
						1);
		mFileListView.addItemDecoration(dividerItemDecoration);

		mFileListView.setAdapter(mmFileListAdapter);


	}

	@ActionHandler(layoutResource = R.id.floating_add)
	public void onAddFloatingClicked(View v){
		createFileDialog();
	}

	private void changeDirectory() {
		Log.d("files","current path: "+mCurrentPath);
		mmFileListAdapter.setmFile(mFilesData.get(mCurrentPath));
		mmFileListAdapter.notifyDataSetChanged();
		mFileListView.requestLayout();

	}

	@Override
	public void onBackPressed() {
		mCurrentPath = FileUtils.CURRENT_PATH;

		if(mCurrentPath.equals(mRootPath)){
			super.onBackPressed();
		}else{
			//modify the mCurrentPath
			mCurrentPath		   = mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/'));
			mCurrentPath 		   = mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/')+1);
			FileUtils.CURRENT_PATH = mCurrentPath;
			changeDirectory();

		}
	}


	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.file_select_options,menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if(item.getItemId()==R.id.encrypt_menu_item){
				//TODO
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		selectCount = 0;
		actionMode  = null;
		mmFileListAdapter.setmSelectionMode(false);
	}


	ActionMode actionMode;
	private int selectCount=0;
	@Override
	public void onLongClick() {
		if(actionMode==null){
			actionMode = startSupportActionMode(this);
			actionMode.setTitle(selectCount+" Selected");
		}else{
			actionMode.setTitle(++selectCount+" Selected");
		}
	}
	@Override
	public void incrementSelectionCount(){
		actionMode.setTitle(++selectCount+" Selected");
	}

	@Override
	public void decrementSelectionCount() {
		actionMode.setTitle(--selectCount+" Selected");
	}
	private void createFileDialog(){
		final Dialog dialog = new Dialog(this);
		dialog.setTitle("Create Folder");
		dialog.setContentView(R.layout.create_file_dialog);
		final EditText folderEditText=(EditText)dialog.findViewById(R.id.foldername_edittext);
		Button okayButton=(Button)dialog.findViewById(R.id.create_file_button);
		okayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				   String folderName=folderEditText.getText().toString();
				if(folderName.length()<1){
					folderEditText.setError("Give me the folder name");
					return;
				}else{
					if(!FileUtils.createFolder(folderName)){
						Toast.makeText(
								FileBrowserActivity.this,
								"Folder name already exist",
								Toast.LENGTH_SHORT
						).show();
					}else{
						dialog.dismiss();
						String path=mmFileListAdapter.getmFile().getCurrentPath();
						mFilesData.remove(path);
						FileFillerWrapper temp=new FileFillerWrapper(path,FileBrowserActivity.this);
						mFilesData.put(path,temp);
						mmFileListAdapter.setmFile(temp);
						mmFileListAdapter.notifyDataSetChanged();
					}
				}
			}
		});
		((Button)dialog.findViewById(R.id.cancel_file_button)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
