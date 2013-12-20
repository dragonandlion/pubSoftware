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

import com.example.recvhzwithhz.revThread;                      /* ������սӿ����ڰ� ---�������ݲ�*/
import com.example.recvhzwithhz.exhDataClass;                   /* ������սӿ����ڰ� ---Ӧ�ò�̳л���*/
import com.example.sendhzwithhz.dataStruct;
import com.example.sendhzwithhz.sendFunc;                       /* ���뷢�ͽӿ����ڰ� ---�������ݲ� */

/* �ڴ˴��̳н��սӿڵ�Ӧ�ò���� */
@SuppressWarnings("unused")
public class MainActivity extends exhDataClass {

	/* ��ֹ���δ򿪽��� */
	private static int dstopFlag = 0;
	/* ���սӿڵ��������ݲ��ʵ�� */
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
		
		msgShow.setText("��������...");
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
	 *  function:���Ͱ�ť�¼�����
	 * 
	 *  �޲����������
	 *  ���÷��ͽӿڽ���Ӧ�ò��û���������ݷ��ͳ�ȥ
	 * 
	 * @param v
	 * @throws InterruptedException 
	 * @throws IOException 
	 ******************/
	public void sendBtnClick(View v) throws IOException, InterruptedException{
		//msgShow.setText("��������...");
		try {
			sendWav = new sendFunc(userNM,comNM,price,count,price*count);
			String res=sendWav.loopSendSeq();
			if(res==null){
			//soundPool.play(1,1, 1, 0, 0, 1);
			//player.start();
				msgShow.setText("�ڿ�����...");
			}
			else{
				payForUserLab.setText(res+" ���ڵȴ��տ�...");
				amtTxt.setVisibility(View.VISIBLE);
				queryBtn.setVisibility(View.VISIBLE);
				cencelBtn.setVisibility(View.VISIBLE);
				payForUserLab.setVisibility(View.VISIBLE);
				msgShow.setVisibility(View.INVISIBLE);
				waveImgBtn.setVisibility(View.INVISIBLE);
				
			}
		}catch(Exception ex){
			Toast.makeText(this, "���ӷ�����ʧ��..."+ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void queryPayCilck(View v) throws UnsupportedEncodingException, IOException{
		if(sendWav.payAmt(amtTxt.getText().toString()) == -1){
			Toast.makeText(this, "��Ǹ,����ʧ��...",Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "��ϲ������ɹ�!", Toast.LENGTH_SHORT).show();
			msgShow.setText("��������...");
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
			Toast.makeText(this, "��Ǹ,ȡ��ʧ��...",Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "��ϲ��ȡ���ɹ�!", Toast.LENGTH_SHORT).show();
			msgShow.setText("��������...");
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
	 *  function:�ص�����
	 * 
	 *   �޲����������
	 *   �˺�����Ҫ����
	 *   
	 *   �˺����ɽ��ܽӿ��������ݲ�ʵ������
	 *   �ڴ˺����е���retWaveData��ý��յ�������
	 *   
	 */
	@Override
	public void getDataFromLis(){
		String buffer=null;
		buffer=revThread.retWaveData();
		if(recordThread.recvAmtOrListen==1){
			Toast.makeText(this, buffer, Toast.LENGTH_SHORT).show();
			msgShow.setText("��������...");
			return ;
		}
		try{
			SocketAddress isa = new InetSocketAddress("192.168.43.54", 9999);
			Socket client = new Socket();
			client.connect(isa, 3000);
			byte[] rbyte = new byte[1024];
	
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();
			Log.d("getDataFromLis��ʾ","���ӷ������ɹ�,192.168.43.54:9999");
			String wbuf = "006|"+buffer;
			out.write(wbuf.getBytes("GB2312"));
			
			in.read(rbyte);
			String rbuf=new String(rbyte, "GB2312").trim();
			Log.d("all",rbuf);
			String[] tmpbuf = rbuf.split("\\|");
			
			if(!tmpbuf[1].equals("no")){
				tmpbuf = tmpbuf[1].split("\\+");
				msgShow.setText(tmpbuf[0].toString()+" ����Ϊ "+tmpbuf[3].toString()+" ��"
						        +tmpbuf[2].toString()+",���� "+tmpbuf[1].toString()+"Ԫ ����" );
				Log.d("one", tmpbuf[0].toString()+" ����Ϊ "+tmpbuf[3].toString()+" ��"+tmpbuf[2].toString()+",���� "+tmpbuf[1].toString()+"Ԫ ����");
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
			Log.d("getDataFromLis����","���ӷ�����ʧ��,192.168.43.54:9999|"+ex.getMessage());
		}
	}
}
