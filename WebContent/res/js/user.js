  
//기본 이미지 사용 (프로필 이미지 삭제)
function delProfileImg() {
	axios.get('/user/delProfileImg.korea').then(function(res) {
		var basicProfileImg = '/res/img/basic_profile.jpg';
		
		if(res != null && res.status == 200) {
			if(res.data.result == 1) { //프로필 이미지 삭제가 완료 됨!!
				var img = document.querySelector('#profileImg');
				var container = document.querySelector('#delProfileBtnContainer');
				
				img.src = basicProfileImg;
				container.remove();
			}
		}
		
	}).catch(function(err) {
		console.err('err 발생 : ' + err);
	})
}