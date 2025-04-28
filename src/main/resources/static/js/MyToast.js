// Enum처럼 사용할 객체
const ToastType = Object.freeze({
    INFO: "info",
    SUCCESS: "success",
    WARNING: "warning",
    ERROR: "error"
});

// 타입별 배경색 매핑
const ToastColors = {
    [ToastType.INFO]: "bg-blue-500",
    [ToastType.SUCCESS]: "bg-green-500",
    [ToastType.WARNING]: "bg-yellow-500",
    [ToastType.ERROR]: "bg-red-500"
};

// ProgressBar 색상 (배경보다 조금 더 진한 색)
const ProgressBarColors = {
    [ToastType.INFO]: "bg-blue-600",
    [ToastType.SUCCESS]: "bg-green-600",
    [ToastType.WARNING]: "bg-yellow-600",
    [ToastType.ERROR]: "bg-red-600"
};

function showToast(title, message, type = ToastType.INFO, duration = 2000, buttons = [], onClose = null) {
    // `toastContainer`가 없으면 생성
    let toastContainer = document.getElementById("toastContainer");
    if (!toastContainer) {
        toastContainer = document.createElement("div");
        toastContainer.id = "toastContainer";
        toastContainer.className = "fixed inset-0 flex justify-center pointer-events-none items-center z-50";
        document.body.appendChild(toastContainer);
    }

    // 잘못된 type 값이 전달되었을 경우 기본값 INFO 사용
    if (!Object.values(ToastType).includes(type)) {
        console.warn(`Invalid toast type: "${type}". Defaulting to INFO.`);
        type = ToastType.INFO;
    }
    
    // 토스트 고유 ID 생성
    const toastId = `toast-${Date.now()}`;

    // 토스트 팝업 생성
    const toast = document.createElement("div");
    toast.id = toastId;
    toast.className = `relative text-white px-6 py-4 rounded-lg shadow-lg text-center ${ToastColors[type]} transition-opacity opacity-0 pointer-events-auto`;
    toast.style.position = "absolute";
    toast.style.maxWidth = "400px";
    toast.style.width = "90%";
    toast.style.top = "50%";
    toast.style.left = "50%";
    toast.style.transform = "translate(-50%, -50%)";

    // ProgressBar 추가
    const progressBar = document.createElement("div");
    progressBar.className = `absolute top-0 left-0 h-[3px] ${ProgressBarColors[type]}`;
    progressBar.style.width = "100%";
    progressBar.style.transition = `width linear ${duration}ms`;

    // 제목 추가
    const titleElement = document.createElement("div");
    titleElement.className = "font-bold text-lg";
    titleElement.innerText = title;

    // 내용 추가
    const messageElement = document.createElement("div");
    messageElement.className = "text-sm";
    messageElement.innerText = message;

    // 버튼 컨테이너 추가
    const buttonContainer = document.createElement("div");
    buttonContainer.className = "mt-3 flex justify-center space-x-2";

    // 버튼 생성 및 이벤트 바인딩
    buttons.forEach((btn, index) => {
        const button = document.createElement("button");
        button.className = "bg-gray-200 text-black px-3 py-1 rounded-md hover:bg-gray-300";
        button.innerText = btn.label;
        button.addEventListener("click", () => {
            btn.onClick(); // 원래 이벤트 실행
            closeToast(toastId); // Toast 닫기
        });
        buttonContainer.appendChild(button);
    });

    // 요소 추가
    toast.appendChild(progressBar);
    toast.appendChild(titleElement);
    toast.appendChild(messageElement);
    if (buttons.length > 0) {
        toast.appendChild(buttonContainer);
    }

    // 추가
    toastContainer.appendChild(toast);

    // 애니메이션 효과
    setTimeout(() => {
        toast.classList.add("opacity-100");
        progressBar.style.width = "0%"; // ProgressBar가 점점 줄어듦
    }, 50);

    // 자동 제거
    setTimeout(() => {
    	closeToast(toastId, onClose);
    }, duration);
}

/**
 * 특정 Toast를 닫는 함수 (이벤트 제거 포함)
 */
function closeToast(toastId, onClose = null) {
    const toast = document.getElementById(toastId);
    if (!toast) return;

    // 애니메이션 효과 적용 후 제거
    toast.classList.remove("opacity-100");
    setTimeout(() => {
        toast.remove();
        if (typeof onClose === "function") {
            onClose(); // Toast 종료 시 실행할 콜백 함수 실행
        }
    }, 300);
}