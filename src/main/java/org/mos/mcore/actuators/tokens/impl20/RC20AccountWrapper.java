package org.mos.mcore.actuators.tokens.impl20;

import com.google.protobuf.ByteString;
import lombok.Data;
import org.mos.mcore.actuators.tokencontracts20.TokensContract20.TokenRC20Info;
import org.mos.mcore.concurrent.AccountInfoWrapper;
import org.mos.mcore.model.Account.AccountInfo;

import java.util.List;

@Data
public class RC20AccountWrapper extends AccountInfoWrapper {

	TokenRC20Info.Builder tokeninfo;

	public RC20AccountWrapper(AccountInfo.Builder info) {
		super(info);
		try {
			if (!info.getExtData().isEmpty()) {
				tokeninfo = TokenRC20Info.newBuilder().mergeFrom(info.getExtData());
			}
		} catch (Exception e) {
		}

	}

	@Override
	public synchronized AccountInfo build(long blocknumber) {
		tokeninfo.setTotalSupply(ByteString.copyFrom(getBalance().toByteArray())).build();
		getInfo().setExtData(tokeninfo.build().toByteString());
		return super.build(blocknumber);
	}
	
	public synchronized void addManager(ByteString address) {
		super.setDirty(true);
		tokeninfo.addManagers(address);
	}

	
	public synchronized void removeManager(ByteString address) {
		super.setDirty(true);
		List<ByteString> existList=tokeninfo.getManagersList();
		tokeninfo.clearManagers();
		for(ByteString one:existList) {
			if(!one.equals(address)) {
				tokeninfo.addManagers(one);
			}
		}
		
	}
	
}
