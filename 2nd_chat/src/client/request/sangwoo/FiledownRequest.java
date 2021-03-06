package client.request.sangwoo;

import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Filedownmessage;
import model.vo.Header;

public class FiledownRequest {
	
	public FiledownRequest(ClientBack clientback,Long groupid,String dir,boolean isImg) {
		try {
			ObjectOutputStream oos = clientback.getOos();
			synchronized(oos)
			{
				int bodylength=0;
				Header header = new Header(ClientBack.FIDOWN,bodylength);
				Filedownmessage filedownmessage = new Filedownmessage(groupid,dir,isImg);
				Data sendData = new Data(header,filedownmessage);
				oos.writeObject(sendData);
				oos.flush();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
