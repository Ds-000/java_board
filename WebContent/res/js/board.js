'user strict'

//글 제목 클릭
function clkArticle(i_board) {		
	var url = `/board/detail.korea?i_board=${i_board}`;
	location.href = url; //주소값 이동
}

//삭제 버튼 클릭
function clkDel(i_board, typ) {
	if(confirm('삭제 하시겠습니까?')) {
		location.href = `del?i_board=${i_board}&typ=${typ}`;
	}
}

//지금은 사용 X, 혹시나 나중에 욕이 있는지 체크하는 용도로 사용
function chk() {
	var frm = document.querySelector('#frm');
	if(chkEmptyEle(frm.title, '제목') || chkEmptyEle(frm.ctnt, '내용')){
		return false;
	}
}

//댓글에서 수정버튼 클릭 > 수정FORM 나타나게 처리
function clkCmtMod(i_cmt) {
	var trForm = document.querySelector('#mod_' + i_cmt);
	trForm.classList.remove('cmd_mod_form');
	console.log(trForm);
}

function clkCmtClose(i_cmt) {
	var trForm = document.querySelector('#mod_' + i_cmt);
	trForm.classList.add('cmd_mod_form');
}

function toggleFavorite(i_board) {
	var fc = document.querySelector('#favoriteContainer');
	var state = fc.getAttribute('is_favorite'); //1: 좋아요, 0:안 좋아요	
	var state = 1 - state;
	
	axios.get('/board/ajaxFavorite.korea', {
		params: {
			'state': state,
			'i_board': i_board
		}	
	}).then(function(res) { //통신 성공
		console.log(res);
		if(res.data.result == 1) {
			var iconClass = state == 1 ? 'fas' : 'far';
			fc.innerHTML = `<i class="${iconClass} fa-heart"></i>`;
			fc.setAttribute('is_favorite', state);
		} else {
			alert('에러가 발생하였습니다.');
		}
	}).catch(function(err) { //통신 실패
		console.err('err 발생 : ' + err);
	});
}