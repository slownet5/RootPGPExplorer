/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.filemanager.listview;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.slownet5.pgprootexplorer.root.RootUtils;
import com.slownet5.pgprootexplorer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tripleheader on 12/18/16.
 * fill the map of files against path
 */

public  class FileFillerWrapper {
    private static final String TAG = "FileFillerWrapper";
    private  List<DataModelFiles> allFiles   = new ArrayList<>();
    private  int totalFilesCount             = 0;
    private FileListAdapter mAdapter;

    private  String currentPath;


    public  void fillData(String current,FileListAdapter mAdapter){
        this.mAdapter=mAdapter;
       new FileFillerTask().execute(current);
    }
    public  DataModelFiles getFileAtPosition(int position){
        if (allFiles!=null && allFiles.size()>0) {
            if (position < allFiles.size() - 1) {
                return allFiles.get(position);
            } else {
                return allFiles.get(allFiles.size() - 1);
            }
        }else{
            return null;
        }
    }

    public  int getTotalFilesCount() {
        return totalFilesCount;
    }

    public  String getCurrentPath() {
        return currentPath;
    }

    private void sortData(){
        DataModelFiles md;
        int size=allFiles.size();
        for (int i = 0; i < size ; i++) {
            md=allFiles.get(i);
            if(!md.getFile()){
                allFiles.remove(md);
                allFiles.add(0,md);
            }
        }
    }
    private class FileFillerTask extends AsyncTask<String,Integer,Void>{

        @Override
        protected Void doInBackground(String... path) {
            if(RootUtils.isRootPath(path[0])){
                fillRootData(path[0]);
            }else{

            Log.d(TAG, "fillData: Current path is : "+path[0]);
            currentPath=path[0];
            totalFilesCount=0;
            //for each file in current path fill data
            File file              = FileUtils.getFile(currentPath);
            if(FileUtils.checkReadStatus(file)){
                if(file.list().length>0) {
                    allFiles.clear();
                    for (File f : file.listFiles()) {
                        //only add file which I can read
                        if (FileUtils.checkReadStatus(f)) {
                            allFiles.add(new DataModelFiles(f));
                            totalFilesCount++;
                        }
                    }
                }
                sortData();
            }
        }
            return null;
        }

        private void fillRootData(String s) {
            currentPath=s;
            allFiles= RootUtils.getFileNames(s);
            totalFilesCount=allFiles.size();
            sortData();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: changing google");
            RecyclerView view=mAdapter.getRecyclerView();
            view.setAdapter(null);
            view.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }
}
