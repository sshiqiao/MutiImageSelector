# MutiImageSelector
图片选择器，支持拍照、多张图片同时选择，兼容android7.0、8.0版本。

<pre>
Intent intent = new Intent(MainActivity.this, MutiImageSelectorActivity.class);
intent.putExtra(MAX_SELECT_NUM,25);
intent.putStringArrayListExtra(SELECTED_DATA, selectedData);
startActivityForResult(intent,SELECTED_FINISH);
</pre>

![image](https://github.com/sshiqiao/MutiImageSelector/blob/master/Screenshot.jpg)
