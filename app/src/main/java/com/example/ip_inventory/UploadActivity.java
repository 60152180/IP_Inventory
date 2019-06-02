package com.example.ip_inventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadActivity extends AppCompatActivity{
    Button btn;
    EditText edit_entry;
    FileInputStream fis;
    URL url;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        edit_entry =
                (EditText)findViewById(R.id.edit_entry);
        //   makeFile(); --> 사진 전송할땐 필요 없음.

        btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try{
                    //서버에 업로드할 파일 경로
                    String file = "/data/data/"
                            +getPackageName()
                            +"/files/TEST.jpg"; // 지금은 text.txt를 업로드 한다.

                    fileUpload(file);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    void makeFile(){
        // /data/data/패키지 이름 /files/파일 이름 ==> 내장 메모리 파일 영역
        String path = "/data/data/"+getPackageName()+"/files";
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File("/data/data/"+getPackageName()+"/files/TEST.jpg");
        try{
            FileOutputStream fos = new FileOutputStream(file);
            String str = "hello android";
            fos.write(str.getBytes());
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    void fileUpload(final String file){

        Thread th = new Thread(new Runnable(){
            public void run(){
                //업로드를 처리할 웹서버의 url
                httpFileUpload("http://54.180.116.239/mobile/upload.php",file);
            }
        });
        th.start();
    }
    void httpFileUpload(String urlString,String file){
        try{
            //sdcard의 파일 입력 스트림 생성
            fis = new FileInputStream(file);
            // url 객체 생성
            url = new URL(urlString);
            //서버 url에 접근 하여 연결을 확립시킴
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            // 커넥션을 통해 입출력이 가능하도록 설정
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false); // 캐시 사용 x
            conn.setRequestMethod("POST"); //포스트 방식으로 업로드
            // POST방식으로 넘길 자료의 정보 설정
            conn.setRequestProperty("Connection","Keep-Alive");
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
            //파일 업로드 이므로 다양한 파일 포맷을 지원하기 위해 dataOutputStream 객체를 생성
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //파일에 저장할 내용 기록
            dos.writeBytes(twoHyphens+boundary+lineEnd);
            dos.writeBytes("Content-Disposition:form-data;name=\"userfile\";filename=\""
                    +file+"\""+lineEnd);
            dos.writeBytes(lineEnd);
            int bytesAvailable = fis.available();
            int maxBufferSize = 1024;

            //Math.min(값1,값2) => 작은 값 리턴
            int bufferSize = Math.min(bytesAvailable,maxBufferSize);

            //버퍼로 사용할 바이트 배열 생성
            byte[] buffer = new byte[bufferSize];

            //파일 입력 스트림을 통해 내용을 읽어서 버퍼에 저장
            int bytesRead = fis.read(buffer,0,bufferSize);

            //내용이 있으면
            while(bytesRead > 0){
                // 버퍼의 사이즈 만큼 읽어서 내용을 데이터 출력 스트림에 기록
                dos.write(buffer,0,bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = fis.read(buffer,0,bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);
            // 파일 입력스트림 닫기
            fis.close();
            //데이터 출력 스트림을 비움
            dos.flush();
            int ch;
            //url 커넥션으로 결과 값을 받아서 스트링 버퍼에 저장.
            InputStream is = conn.getInputStream();
            StringBuffer b = new StringBuffer();

            //1바이트씩 읽어서 내용이 있으면 스트링 버퍼에 추가
            // 더이상 내용이 없으면 -1을 리턴
            while( (ch = is.read()) != -1 ){
                b.append((char)ch);
            }
            //바이트 배열을 스트링으로 변환
            result = b.toString().trim();
            // 데이터 출력 스트링을 닫음
            dos.close();
            JSONObject jsonObj = new JSONObject(result);
            // json.get(변수명) json변수의 값
            result=jsonObj.getString("message");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edit_entry.setText(result);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}