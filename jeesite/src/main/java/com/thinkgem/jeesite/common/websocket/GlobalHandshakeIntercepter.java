package com.thinkgem.jeesite.common.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

public class GlobalHandshakeIntercepter implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request,ServerHttpResponse response, WebSocketHandler wsHandler,Map<String, Object> attributes) throws Exception {
		String userName = UserUtils.getUser().getName();
		if(userName==null){
			System.out.println("握手失败");
			return false;
		}else{
			attributes.put("userName", userName);
			return true;
		}
	}

	@Override
	public void afterHandshake(ServerHttpRequest request,ServerHttpResponse response, WebSocketHandler wsHandler,Exception exception) {
	}

}
