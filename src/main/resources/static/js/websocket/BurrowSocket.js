class ReceiveInfo {
	constructor(address, callback) {
		this.address = address;
		this.callback = callback;
		this.subscription = null;
	}
}

class ConnectionInfo {
	constructor(serverAddress, headers = {}) {
		this.serverAddress = serverAddress;
		this.headers = headers;
		this.onOpen = null;
		this.onClose = null;
	}

	setOpenHandler(handler) {
		this.onOpen = handler;
	}

	setCloseHandler(handler) {
		this.onClose = handler;
	}
}

class SocketScript {
	constructor(connectionInfo) {
		this.connectionInfo = connectionInfo;
		this.stompClient = null;
		this.connected = false;
		this.receiveInfos = [];
	}

	addReceiveInfo(info) {
		this.receiveInfos.push(info);
	}

	connect() {
		console.log("연결 시도", this.connectionInfo.serverAddress);
		if (this.stompClient && this.stompClient.connected) {
			console.log("이미 연결됨");
			return true;
		}
		if (this.receiveInfos.length === 0) {
			console.warn("구독 정보가 없습니다.");
			return false;
		}

		const socket = new SockJS(this.connectionInfo.serverAddress);
		this.stompClient = Stomp.over(socket);

		this.stompClient.connect(
			this.connectionInfo.headers,
			(frame) => this._onConnect(frame),
			(error) => this._onError(error)
		);

		socket.onclose = () => this.disconnect();
		
		console.log("연결 시도 완료");
		
		return true;
	}

	disconnect() {
		if (this.stompClient) {
			this.stompClient.disconnect(() => {
				this.connected = false;
				this.stompClient = null;
				if (this.connectionInfo.onClose) this.connectionInfo.onClose();
			});
		}
	}

	_onConnect(frame) {
		this.connected = true;
		this.receiveInfos.forEach((info) => {
			info.subscription = this.stompClient.subscribe(info.address, info.callback);
			console.log("Subscribed to", info.address);
		});
		if (this.connectionInfo.onOpen) this.connectionInfo.onOpen(frame);
	}

	_onError(error) {
		console.error("WebSocket error:", error);
		if (this.connectionInfo.onClose) this.connectionInfo.onClose();
	}

	sendMessage(address, data, headers = {}) {
		if (!this.stompClient?.connected) {
			console.warn("소켓이 연결되어 있지 않음");
			if (this.connectionInfo.onClose) this.connectionInfo.onClose();
			return;
		}
		this.stompClient.send(address, headers, JSON.stringify(data));
	}
}

/*

use example:)

// 이 값을 backend로 전달하면 personalCallBackStatus 로 수신 가능.
let currentUserKey = null;

const connectionInfo = new ConnectionInfo('/MyCall/BackEvent', { service: 'BackOffice' });
connectionInfo.setOpenHandler((frame) => {
	currentUserKey = frame.headers['user-name'];
	console.log("연결됨", currentUserKey );
});
connectionInfo.setCloseHandler(() => {
	currentUserKey = null;
	console.log("연결 해제");
});

const socketScript = new SocketScript(connectionInfo);
socketScript.addReceiveInfo(new ReceiveInfo('/user/toFront/RecieveStatus', personalCallBackStatus));
socketScript.addReceiveInfo(new ReceiveInfo('/toFront/RecieveStatus', publicCallBackStatus));
socketScript.connect();

// 나만을 위한 콜백
function personalCallBackStatus(receiveData) {
		
	console.log("소켓 데이터 수신 " + receiveData.body);

	const json = JSON.parse(receiveData.body);
	
	// 데이터 처리
}

// 아무나...
function publicCallBackStatus(receiveData) {
	
	console.log("소켓 데이터 수신 " + receiveData.body);

	const json = JSON.parse(receiveData.body);
	
	// 데이터 처리
}

 */
