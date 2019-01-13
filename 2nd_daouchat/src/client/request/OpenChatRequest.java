package client.request;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;

public class OpenChatRequest {

	public OpenChatRequest(ClientBack clientBack, Long groupid) {
		try {
			ObjectOutputStream oos = clientBack.getOos();
			int bodylength = 0; // 데이터 길이가 필요한가?
			Header header = new Header(ClientBack.OPENCHAT,bodylength);
			Data sendData = new Data(header,groupid);
			oos.writeObject(sendData);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
