package com.xja.wechat.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xja.wechat.bean.JsonBean;
import com.xja.wechat.bean.MyTeam;
import com.xja.wechat.common.Constant;
import com.xja.wechat.dao.PullUserMapper;
import com.xja.wechat.dao.QrCodeMapper;
import com.xja.wechat.dao.WxUserMapper;
import com.xja.wechat.entity.QrCode;
import com.xja.wechat.entity.WxUser;
import com.xja.wechat.service.ShareService;
@Service
public class ShareServiceImpl implements ShareService {

	@Autowired
	private QrCodeMapper qrCodeMapper;
	
	@Autowired
	private PullUserMapper pullUserMapper;
	
	@Autowired
	private WxUserMapper wxUserMapper;
	
	/**
	 * 当前已有用户信息，不需要换取openid
	 */
	@Override
	public JsonBean insertQrCode(WxUser wxUser) {
		//根据用户的openid进行查询
		QrCode qrCode = getQrCode(wxUser);
		return new JsonBean(0, "", qrCode);
	}

	/**
	 * 需要先调用网页授权，再调用生成二维码
	 */
	@Override
	public JsonBean insertQrCodeAndOauth(String code) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//调用网页授权换取access_token&&openid
		String oauth = Constant.Oauth(code);
		JSONObject accessAndOpenid = new JSONObject(oauth);
		if(accessAndOpenid.has("openid")){
			WxUser wxUser = wxUserMapper.selectByOpenId(accessAndOpenid.get("openid").toString());
			resultMap.put("wxUser", wxUser);
			resultMap.put("openid", wxUser.getOpenid());
			//根据用户的openid进行查询
			QrCode qrCode = getQrCode(wxUser);
			resultMap.put("ticket", qrCode.getTicket());
		}
		return new JsonBean(0, "", resultMap);
	}
	
	private QrCode getQrCode(WxUser wxUser){
		QrCode qrCode = qrCodeMapper.selectByOpenid(wxUser.getOpenid());
		if(qrCode == null){
			//没有生成过二维码
			String qrCodeJson = Constant.getSence(String.valueOf(wxUser.getId()));
			JSONObject qrJson = new JSONObject(qrCodeJson);
			qrCode = new QrCode();
			qrCode.setOpenid(wxUser.getOpenid());
			qrCode.setScene_str(String.valueOf(wxUser.getId()));
			qrCode.setExpire_seconds(Integer.valueOf(qrJson.get("expire_seconds").toString()));
			qrCode.setTicket(qrJson.getString("ticket").toString());
			String valueOf = String.valueOf(System.currentTimeMillis()/1000);
			qrCode.setCreate_time(Integer.valueOf(valueOf));
			qrCodeMapper.insert(qrCode);
		}else{
			//生成过
			if((System.currentTimeMillis()/1000)-(qrCode.getCreate_time()) 
					>= 
					(qrCode.getExpire_seconds() - 100)){
				//已过期重新调用获取二维码，update数据库
				String qrCodeJson = Constant.getSence(String.valueOf(wxUser.getId()));
				JSONObject qrJson = new JSONObject(qrCodeJson);
				qrCode.setExpire_seconds(Integer.valueOf(qrJson.get("expire_seconds").toString()));
				qrCode.setTicket(qrJson.getString("ticket").toString());
				String valueOf = String.valueOf(System.currentTimeMillis()/1000);
				qrCode.setCreate_time(Integer.valueOf(valueOf));
				qrCodeMapper.updateByPrimaryKeySelective(qrCode);
			}
		}
		return qrCode;
	}

	@Override
	public Map<String, Object> selectMyTeam(String openid) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer count = pullUserMapper.selectMyTeamCount(openid);
		List<MyTeam> list = pullUserMapper.selectMyTeam(openid);
		map.put("count", count);
		map.put("list", list);
		return map;
	}

}
