console.clear();

function successModal(msg) {
  Swal.fire({
		title: msg,
		icon: 'success',
		confirmButtonText: '확인'
	});
}

function confirmModal(msg, confirmCallback) {
  Swal.fire({
    title: msg,
    text: "삭제시 복구가 불가능합니다.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "삭제",
    cancelButtonText: "취소",
  }).then((result) => {
    if (result.isConfirmed && confirmCallback) {
      confirmCallback();
    }
  });
}

