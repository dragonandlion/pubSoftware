package com.example.testsendwave;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.recvhzwithhz.revThread;                      /* 引入接收接口所在包 ---物理数据层*/
import com.example.recvhzwithhz.exhDataClass;                   /* 引入接收接口所在包 ---应用层继承基类*/
import com.example.sendhzwithhz.dataStruct;
import com.example.sendhzwithhz.sendFunc;                       /* 引入发送接口所在包 ---物理数据层 */

/* 在此处继承接收接口的应用层基类 */
@SuppressWarnings("unused")
public class MainActivity extends exhDataClass {

	/* 防止两次打开接收 */
	private static int dstopFlag = 0;
	/* 接收接口的物理数据层的实例 */
	private revThread recordThread=null;
	private SoundPool soundPool=null;
	private MediaPlayer   player=null;
	private String userNM = null;
	TextView msgShow=null;
	TextView amtTxt=null;
	TextView payForUserLab=null;
	Button  queryBtn=null;
	Button  cencelBtn=null;
	ImageButton waveImgBtn =null;
	sendFunc sendWav = null;
	
	private String comNM = null;
	private double price = 0.00;
	private int   count =0;
	private int   tmpFlag = 0 ;
	
	TextView userNML = null;
	TextView comNML = null;
	TextView priceL = null; 
	TextView countL = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*soundPool= new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPool.load(this,R.raw.bgwav,1);*/
		amtTxt = (TextView)(findViewById(R.id.amtTxt));
		payForUserLab = (TextView)(findViewById(R.id.payforUserLab));
		queryBtn = (Button)(findViewById(R.id.querBtn));
		cencelBtn = (Button)(findViewById(R.id.cancelBtn));
		msgShow = (TextView)findViewById(R.id.msgTxt);
		waveImgBtn = (ImageButton)findViewById(R.id.waveImgBtn);
		userNML = (TextView)findViewById(R.id.userNMTxt);
		comNML =  (TextView)findViewById(R.id.comNMTxt);
		priceL = (TextView)findViewById(R.id.priceTxt);
		countL = (TextView)findViewById(R.id.cuontTxt);
		
		msgShow.setText("正在聆听...");
		recordThread = new revThread(2000,44100,handler,callback);
		recordThread.start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	/******************
	 * 
	 *  function:发送按钮事件函数
	 * 
	 *  无参数输入输出
	 *  调用发送接口将从应用层用户输入的数据发送出去
	 * 
	 * @param v
	 * @throws InterruptedException 
	 * @throws IOException 
	 ******************/
	public void sendBtnClick(View v) throws IOException, InterruptedException{
		//msgShow.setText("正在搜索...");
		try {
			sendWav = new sendFunc(userNM,comNM,price,count,price*count);
			String res=sendWav.loopSendSeq();
			if(res==null){
			//soundPool.play(1,1, 1, 0, 0, 1);
			//player.start();
				msgShow.setText("在靠近点...");
			}
			else{
				payForUserLab.setText(res+" 正在等待收款...");
				amtTxt.setVisibility(View.VISIBLE);
				queryBtn.setVisibility(View.VISIBLE);
				cencelBtn.setVisibility(View.VISIBLE);
				payForUserLab.setVisibility(View.VISIBLE);
				msgShow.setVisibility(View.INVISIBLE);
				waveImgBtn.setVisibility(View.INVISIBLE);
				
			}
		}catch(Exception ex){
			Toast.makeText(this, "连接服务器失败..."+ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void queryPayCilck(View v) throws UnsupportedEncodingException, IOException{
		if(sendWav.payAmt(amtTxt.getText().toString()) == -1){
			Toast.makeText(this, "抱歉,付款失败...",Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "恭喜，付款成功!", Toast.LENGTH_SHORT).show();
			msgShow.setText("正在聆听...");
			amtTxt.setVisibility(View.INVISIBLE);
			queryBtn.setVisibility(View.INVISIBLE);
			cencelBtn.setVisibility(View.INVISIBLE);
			payForUserLab.setVisibility(View.INVISIBLE);
			msgShow.setVisibility(View.VISIBLE);
			waveImgBtn.setVisibility(View.VISIBLE);
		}
	}
	
	public void cancelPayCilck(View v) throws UnsupportedEncodingException, IOException{
		if(sendWav.payAmt("0.00") == -1){
			Toast.makeText(this, "抱歉,取消失败...",Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "恭喜，取消成功!", Toast.LENGTH_SHORT).show();
			msgShow.setText("正在聆听...");
			amtTxt.setVisibility(View.INVISIBLE);
			queryBtn.setVisibility(View.INVISIBLE);
			cencelBtn.setVisibility(View.INVISIBLE);
			payForUserLab.setVisibility(View.INVISIBLE);
			msgShow.setVisibility(View.VISIBLE);
			waveImgBtn.setVisibility(View.VISIBLE);
		}
	}
	
	public void setClick(View v){
		if(tmpFlag==1){
			this.userNM = userNML.getText().toString();
			this.comNM  = comNML.getText().toString();
			this.price  = Double.parseDouble(priceL.getText().toString());
			this.count  = Integer.parseInt(countL.getText().toString());
			tmpFlag = 0;
			userNML.setVisibility(View.INVISIBLE);
			comNML.setVisibility(View.INVISIBLE);
			priceL.setVisibility(View.INVISIBLE);
			countL.setVisibility(View.INVISIBLE);
		}else{
			userNML.setVisibility(View.VISIBLE);
			comNML.setVisibility(View.VISIBLE);
			priceL.setVisibility(View.VISIBLE);
			countL.setVisibility(View.VISIBLE);
			tmpFlag = 1;
		}
	}
	
	/**************************
	 * 
	 *  function:回调函数
	 * 
	 *   无参数输入输出
	 *   此函数需要覆盖
	 *   
	 *   此函数由接受接口物理数据层实例触发
	 *   在此函数中调用retWaveData获得接收到的数据
	 *   
	 */
	@Override
	public void getDataFromLis(){
		String buffer=null;
		buffer=revThread.retWaveData();
		if(recordThread.recvAmtOrListen==1){
			Toast.makeText(this, buffer, Toast.LENGTH_SHORT).show();
			msgShow.setText("正在聆听...");
			return ;
		}
		try{
			SocketAddress isa = new InetSocketAddress("192.168.43.54", 9999);
			Socket client = new Socket();
			client.connect(isa, 3000);
			byte[] rbyte = new byte[1024];
	
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();
			Log.d("getDataFromLis提示","链接服务器成功,192.168.43.54:9999");
			String wbuf = "006|"+buffer;
			out.write(wbuf.getBytes("GB2312"));
			
			in.read(rbyte);
			String rbuf=new String(rbyte, "GB2312").trim();
			Log.d("all",rbuf);
			String[] tmpbuf = rbuf.split("\\|");
			
			if(!tmpbuf[1].equals("no")){
				tmpbuf = tmpbuf[1].split("\\+");
				msgShow.setText(tmpbuf[0].toString()+" 正在为 "+tmpbuf[3].toString()+" 个"
						        +tmpbuf[2].toString()+",共计 "+tmpbuf[1].toString()+"元 付款" );
				Log.d("one", tmpbuf[0].toString()+" 正在为 "+tmpbuf[3].toString()+" 个"+tmpbuf[2].toString()+",共计 "+tmpbuf[1].toString()+"元 付款");
				wbuf = "004|"+buffer+"+"+userNM;
				Log.d("two", wbuf);
				out.write(wbuf.getBytes("GB2312"));
				rbyte = new byte[1024];
				in.read(rbyte);
				rbuf=new String(rbyte, "GB2312").trim();
				Log.d("three",rbuf);
				tmpbuf = rbuf.split("\\|");
				if(tmpbuf[1].equals("ok")){
					recordThread.stopFlag=0;
					recordThread.recvAmtOrListen=1;
					recordThread.exhData=buffer;
				}
			}
			try{
				client.shutdownInput();
				client.shutdownOutput();
				client.close();
			}catch(Exception e){}
			
		}catch(Exception ex){
			Log.d("getDataFromLis警告","链接服务器失败,192.168.43.54:9999|"+ex.getMessage());
		}
	}
}
