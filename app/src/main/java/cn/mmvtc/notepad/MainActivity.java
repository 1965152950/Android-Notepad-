package cn.mmvtc.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.mmvtc.notepad.adapter.NotepadAdapter;
import cn.mmvtc.notepad.bean.NotepadBean;
import cn.mmvtc.notepad.database.SQLiteHelper;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    List<NotepadBean> list;
    SQLiteHelper mSQLiteHelper;
    NotepadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //用于显示记录的列表
        listView = findViewById(R.id.listview);
        ImageView add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RecordActivity.class);
                startActivityForResult(intent,1);
            }
        });
        initData();
    }

    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(this); //创建数据库
        showQueryData();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long id) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("确定删除此记录吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NotepadBean notepadBean = list.get(position);
                                if(mSQLiteHelper.deleteData(notepadBean.getId())){
                                    list.remove(position);  //删除对应的Item
                                    adapter.notifyDataSetChanged(); //更新记事本界面
                                    Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();   //关闭对话框
                            }
                        });
                dialog = builder.create();  //创建对话框
                dialog.show();  //显示对话框
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                final NotepadBean notepadBean = list.get(position);
                Intent intent = new Intent(MainActivity.this,RecordActivity.class);
                intent.putExtra("id",notepadBean.getId());  //记录id
                intent.putExtra("time",notepadBean.getNotepadTime());   //记录的时间
                intent.putExtra("content",notepadBean.getNotepadContent()); //记录的内容
                //跳转到修改记录界面
                startActivityForResult(intent,1);
            }
        });
    }

    private void showQueryData() {
        if(list != null){
            list.clear();
        }
        //从数据库中查询数据（保存的记录）
        list = mSQLiteHelper.query();
        adapter = new NotepadAdapter(this,list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==2){
            showQueryData();
        }
    }
}
